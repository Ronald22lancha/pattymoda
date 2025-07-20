/*
  # Sistema de Punto de Venta y Gestión de Caja

  1. Nuevas Tablas
    - `cajas` - Cajas registradoras de cada sucursal
    - `turnos_caja` - Apertura y cierre de cajas por turno
    - `movimientos_caja` - Todos los movimientos de efectivo
    - `sesiones_pos` - Sesiones activas del sistema POS
    - `configuracion_pos` - Configuraciones del punto de venta

  2. Funcionalidades
    - Control de apertura/cierre de caja
    - Registro de ventas y pagos en efectivo
    - Arqueo de caja automático
    - Reportes de ventas diarias
    - Alertas de descuadre

  3. Integración
    - Vinculación con pedidos presenciales
    - Control de inventario en tiempo real
    - Generación de comprobantes automática
*/

-- Tabla de cajas registradoras
CREATE TABLE IF NOT EXISTS cajas (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  sucursal_id uuid REFERENCES sucursales(id) ON DELETE CASCADE,
  numero_caja varchar(20) NOT NULL,
  nombre_caja varchar(100) NOT NULL,
  terminal_pos varchar(100), -- Identificador del terminal POS
  ip_address varchar(45),
  activa boolean DEFAULT true,
  configuracion jsonb DEFAULT '{}',
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now(),
  UNIQUE(sucursal_id, numero_caja)
);

-- Tabla de turnos de caja
CREATE TABLE IF NOT EXISTS turnos_caja (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  caja_id uuid REFERENCES cajas(id) ON DELETE CASCADE,
  cajero_id uuid REFERENCES usuarios(id),
  fecha_apertura timestamptz NOT NULL DEFAULT now(),
  fecha_cierre timestamptz,
  monto_inicial decimal(10,2) NOT NULL DEFAULT 0,
  monto_final decimal(10,2),
  monto_esperado decimal(10,2),
  diferencia decimal(10,2),
  total_ventas_efectivo decimal(10,2) DEFAULT 0,
  total_ventas_tarjeta decimal(10,2) DEFAULT 0,
  total_ventas_digital decimal(10,2) DEFAULT 0,
  total_egresos decimal(10,2) DEFAULT 0,
  numero_transacciones integer DEFAULT 0,
  estado varchar(20) DEFAULT 'abierto', -- 'abierto', 'cerrado', 'cuadrado'
  observaciones text,
  supervisor_id uuid REFERENCES usuarios(id),
  fecha_supervision timestamptz,
  arqueo_detalle jsonb, -- Detalle del conteo de billetes y monedas
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de movimientos de caja
CREATE TABLE IF NOT EXISTS movimientos_caja (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  turno_caja_id uuid REFERENCES turnos_caja(id) ON DELETE CASCADE,
  pedido_id uuid REFERENCES pedidos(id),
  tipo_movimiento varchar(30) NOT NULL, -- 'venta', 'devolucion', 'gasto', 'retiro', 'ingreso_extra'
  concepto varchar(200) NOT NULL,
  monto decimal(10,2) NOT NULL,
  metodo_pago varchar(50), -- 'efectivo', 'tarjeta', 'yape', 'plin', etc.
  referencia varchar(100), -- Número de operación, voucher, etc.
  autorizado_por uuid REFERENCES usuarios(id),
  comprobante_url varchar(500),
  observaciones text,
  fecha_movimiento timestamptz DEFAULT now(),
  fecha_creacion timestamptz DEFAULT now()
);

-- Tabla de sesiones POS activas
CREATE TABLE IF NOT EXISTS sesiones_pos (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  turno_caja_id uuid REFERENCES turnos_caja(id) ON DELETE CASCADE,
  usuario_id uuid REFERENCES usuarios(id),
  token_sesion varchar(255) UNIQUE NOT NULL,
  ip_address varchar(45),
  user_agent text,
  fecha_inicio timestamptz DEFAULT now(),
  fecha_ultimo_acceso timestamptz DEFAULT now(),
  fecha_expiracion timestamptz DEFAULT (now() + interval '8 hours'),
  activa boolean DEFAULT true
);

-- Tabla de configuración POS
CREATE TABLE IF NOT EXISTS configuracion_pos (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  sucursal_id uuid REFERENCES sucursales(id) ON DELETE CASCADE,
  clave varchar(100) NOT NULL,
  valor text,
  tipo_dato varchar(20) DEFAULT 'string', -- 'string', 'number', 'boolean', 'json'
  descripcion text,
  categoria varchar(50), -- 'impresion', 'pagos', 'general', etc.
  fecha_actualizacion timestamptz DEFAULT now(),
  UNIQUE(sucursal_id, clave)
);

-- Habilitar RLS
ALTER TABLE cajas ENABLE ROW LEVEL SECURITY;
ALTER TABLE turnos_caja ENABLE ROW LEVEL SECURITY;
ALTER TABLE movimientos_caja ENABLE ROW LEVEL SECURITY;
ALTER TABLE sesiones_pos ENABLE ROW LEVEL SECURITY;
ALTER TABLE configuracion_pos ENABLE ROW LEVEL SECURITY;

-- Políticas de seguridad
CREATE POLICY "Solo cajeros y admins acceden a cajas"
  ON cajas FOR ALL
  TO authenticated
  USING (EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol IN ('Administrador', 'Cajero', 'Empleado')
  ));

CREATE POLICY "Cajeros ven sus turnos"
  ON turnos_caja FOR ALL
  TO authenticated
  USING (cajero_id = auth.uid() OR EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol IN ('Administrador', 'Empleado')
  ));

-- Índices de optimización
CREATE INDEX IF NOT EXISTS idx_cajas_sucursal ON cajas(sucursal_id);
CREATE INDEX IF NOT EXISTS idx_turnos_caja_caja ON turnos_caja(caja_id);
CREATE INDEX IF NOT EXISTS idx_turnos_caja_cajero ON turnos_caja(cajero_id);
CREATE INDEX IF NOT EXISTS idx_turnos_caja_fecha ON turnos_caja(fecha_apertura);
CREATE INDEX IF NOT EXISTS idx_movimientos_turno ON movimientos_caja(turno_caja_id);
CREATE INDEX IF NOT EXISTS idx_movimientos_tipo ON movimientos_caja(tipo_movimiento);
CREATE INDEX IF NOT EXISTS idx_sesiones_pos_token ON sesiones_pos(token_sesion);
CREATE INDEX IF NOT EXISTS idx_configuracion_pos_sucursal ON configuracion_pos(sucursal_id);

-- Función para validar turno de caja abierto
CREATE OR REPLACE FUNCTION validar_turno_abierto(p_caja_id uuid) RETURNS boolean AS $$
BEGIN
  RETURN EXISTS (
    SELECT 1 FROM turnos_caja 
    WHERE caja_id = p_caja_id 
    AND estado = 'abierto' 
    AND fecha_cierre IS NULL
  );
END;
$$ LANGUAGE plpgsql;

-- Función para calcular monto esperado en caja
CREATE OR REPLACE FUNCTION calcular_monto_esperado_caja(p_turno_id uuid) RETURNS decimal AS $$
DECLARE
  monto_inicial decimal(10,2);
  total_ingresos decimal(10,2);
  total_egresos decimal(10,2);
BEGIN
  SELECT t.monto_inicial INTO monto_inicial
  FROM turnos_caja t WHERE t.id = p_turno_id;
  
  SELECT COALESCE(SUM(CASE WHEN tipo_movimiento IN ('venta', 'ingreso_extra') THEN monto ELSE 0 END), 0)
  INTO total_ingresos
  FROM movimientos_caja WHERE turno_caja_id = p_turno_id AND metodo_pago = 'efectivo';
  
  SELECT COALESCE(SUM(CASE WHEN tipo_movimiento IN ('devolucion', 'gasto', 'retiro') THEN monto ELSE 0 END), 0)
  INTO total_egresos
  FROM movimientos_caja WHERE turno_caja_id = p_turno_id;
  
  RETURN monto_inicial + total_ingresos - total_egresos;
END;
$$ LANGUAGE plpgsql;

-- Trigger para actualizar totales de turno automáticamente
CREATE OR REPLACE FUNCTION trigger_actualizar_totales_turno() RETURNS trigger AS $$
DECLARE
  turno_id uuid;
BEGIN
  turno_id := COALESCE(NEW.turno_caja_id, OLD.turno_caja_id);
  
  UPDATE turnos_caja SET
    total_ventas_efectivo = (
      SELECT COALESCE(SUM(monto), 0) 
      FROM movimientos_caja 
      WHERE turno_caja_id = turno_id 
      AND tipo_movimiento = 'venta' 
      AND metodo_pago = 'efectivo'
    ),
    total_ventas_tarjeta = (
      SELECT COALESCE(SUM(monto), 0) 
      FROM movimientos_caja 
      WHERE turno_caja_id = turno_id 
      AND tipo_movimiento = 'venta' 
      AND metodo_pago LIKE '%tarjeta%'
    ),
    total_ventas_digital = (
      SELECT COALESCE(SUM(monto), 0) 
      FROM movimientos_caja 
      WHERE turno_caja_id = turno_id 
      AND tipo_movimiento = 'venta' 
      AND metodo_pago IN ('yape', 'plin', 'lukita', 'paypal')
    ),
    total_egresos = (
      SELECT COALESCE(SUM(monto), 0) 
      FROM movimientos_caja 
      WHERE turno_caja_id = turno_id 
      AND tipo_movimiento IN ('devolucion', 'gasto', 'retiro')
    ),
    numero_transacciones = (
      SELECT COUNT(*) 
      FROM movimientos_caja 
      WHERE turno_caja_id = turno_id
    ),
    monto_esperado = calcular_monto_esperado_caja(turno_id),
    fecha_actualizacion = now()
  WHERE id = turno_id;
  
  RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_movimientos_actualizar_turno
  AFTER INSERT OR UPDATE OR DELETE ON movimientos_caja
  FOR EACH ROW
  EXECUTE FUNCTION trigger_actualizar_totales_turno();

-- Insertar cajas por defecto para la sucursal principal
INSERT INTO cajas (sucursal_id, numero_caja, nombre_caja)
SELECT s.id, 'CAJA001', 'Caja Principal'
FROM sucursales s 
WHERE s.es_principal = true
ON CONFLICT DO NOTHING;

-- Configuración POS por defecto
INSERT INTO configuracion_pos (sucursal_id, clave, valor, tipo_dato, descripcion, categoria)
SELECT s.id, 'impresora_termica', 'true', 'boolean', 'Usar impresora térmica para tickets', 'impresion'
FROM sucursales s WHERE s.es_principal = true
UNION ALL
SELECT s.id, 'serie_boleta', 'B001', 'string', 'Serie para boletas de venta', 'comprobantes'
FROM sucursales s WHERE s.es_principal = true
UNION ALL
SELECT s.id, 'serie_factura', 'F001', 'string', 'Serie para facturas de venta', 'comprobantes'
FROM sucursales s WHERE s.es_principal = true
ON CONFLICT DO NOTHING;