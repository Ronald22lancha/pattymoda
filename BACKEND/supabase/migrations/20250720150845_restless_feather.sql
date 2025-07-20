/*
  # Sistema de Promociones y Cupones

  1. Nuevas Tablas
    - `cupones` - Cupones de descuento
    - `promociones` - Promociones automáticas
    - `uso_cupones` - Historial de uso de cupones
    - `reglas_promocion` - Reglas para promociones automáticas

  2. Funcionalidades
    - Cupones de descuento con códigos únicos
    - Promociones automáticas por categoría, cantidad, etc.
    - Límites de uso y fechas de vigencia
    - Descuentos por porcentaje o monto fijo
    - Reglas complejas (2x1, monto mínimo, etc.)

  3. Validaciones
    - Verificación de vigencia
    - Control de usos máximos
    - Validación de condiciones mínimas
*/

-- Tabla de cupones de descuento
CREATE TABLE IF NOT EXISTS cupones (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  codigo_cupon varchar(50) UNIQUE NOT NULL,
  nombre varchar(100) NOT NULL,
  descripcion text,
  tipo_descuento varchar(20) NOT NULL, -- 'porcentaje', 'monto_fijo'
  valor_descuento decimal(10,2) NOT NULL,
  monto_minimo_compra decimal(10,2),
  monto_maximo_descuento decimal(10,2),
  fecha_inicio timestamptz NOT NULL,
  fecha_fin timestamptz NOT NULL,
  usos_maximos integer,
  usos_por_usuario integer DEFAULT 1,
  usos_actuales integer DEFAULT 0,
  solo_primera_compra boolean DEFAULT false,
  aplicable_envio boolean DEFAULT false,
  categorias_incluidas uuid[], -- Array de IDs de categorías
  productos_incluidos uuid[], -- Array de IDs de productos específicos
  usuarios_incluidos uuid[], -- Array de IDs de usuarios específicos (cupones personalizados)
  activo boolean DEFAULT true,
  codigo_promocional varchar(100), -- Para campañas específicas
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de promociones automáticas
CREATE TABLE IF NOT EXISTS promociones (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre_promocion varchar(100) NOT NULL,
  descripcion text,
  tipo_promocion varchar(30) NOT NULL, -- '2x1', '3x2', 'descuento_cantidad', 'descuento_categoria'
  condiciones jsonb NOT NULL, -- Condiciones específicas de la promoción
  descuento jsonb NOT NULL, -- Configuración del descuento
  prioridad integer DEFAULT 1, -- Orden de aplicación (mayor prioridad primero)
  fecha_inicio timestamptz NOT NULL,
  fecha_fin timestamptz NOT NULL,
  dias_semana integer[] DEFAULT '{1,2,3,4,5,6,7}', -- 1=Lunes, 7=Domingo
  horas_inicio time,
  horas_fin time,
  sucursales_incluidas uuid[], -- Array de IDs de sucursales donde aplica
  categorias_incluidas uuid[], -- Array de IDs de categorías
  productos_incluidos uuid[], -- Array de IDs de productos específicos
  aplicable_online boolean DEFAULT true,
  aplicable_presencial boolean DEFAULT true,
  limite_usos integer,
  usos_actuales integer DEFAULT 0,
  activa boolean DEFAULT true,
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de uso de cupones (historial)
CREATE TABLE IF NOT EXISTS uso_cupones (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  cupon_id uuid REFERENCES cupones(id) ON DELETE CASCADE,
  usuario_id uuid REFERENCES usuarios(id),
  pedido_id uuid REFERENCES pedidos(id),
  monto_descuento decimal(10,2) NOT NULL,
  fecha_uso timestamptz DEFAULT now()
);

-- Tabla de reglas de promoción (para promociones complejas)
CREATE TABLE IF NOT EXISTS reglas_promocion (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  promocion_id uuid REFERENCES promociones(id) ON DELETE CASCADE,
  tipo_regla varchar(50) NOT NULL, -- 'cantidad_minima', 'monto_minimo', 'categoria_especifica', etc.
  parametros jsonb NOT NULL,
  orden_evaluacion integer DEFAULT 1,
  activa boolean DEFAULT true,
  fecha_creacion timestamptz DEFAULT now()
);

-- Habilitar RLS
ALTER TABLE cupones ENABLE ROW LEVEL SECURITY;
ALTER TABLE promociones ENABLE ROW LEVEL SECURITY;
ALTER TABLE uso_cupones ENABLE ROW LEVEL SECURITY;
ALTER TABLE reglas_promocion ENABLE ROW LEVEL SECURITY;

-- Políticas de seguridad
CREATE POLICY "Cupones públicos para lectura"
  ON cupones FOR SELECT
  TO anon, authenticated
  USING (activo = true AND fecha_inicio <= now() AND fecha_fin >= now());

CREATE POLICY "Solo admins gestionan cupones"
  ON cupones FOR INSERT, UPDATE, DELETE
  TO authenticated
  USING (EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol IN ('Administrador', 'Empleado')
  ));

CREATE POLICY "Promociones públicas para lectura"
  ON promociones FOR SELECT
  TO anon, authenticated
  USING (activa = true AND fecha_inicio <= now() AND fecha_fin >= now());

-- Índices de optimización
CREATE INDEX IF NOT EXISTS idx_cupones_codigo ON cupones(codigo_cupon);
CREATE INDEX IF NOT EXISTS idx_cupones_fecha ON cupones(fecha_inicio, fecha_fin);
CREATE INDEX IF NOT EXISTS idx_cupones_activo ON cupones(activo);
CREATE INDEX IF NOT EXISTS idx_promociones_fecha ON promociones(fecha_inicio, fecha_fin);
CREATE INDEX IF NOT EXISTS idx_promociones_activa ON promociones(activa);
CREATE INDEX IF NOT EXISTS idx_uso_cupones_cupon ON uso_cupones(cupon_id);
CREATE INDEX IF NOT EXISTS idx_uso_cupones_usuario ON uso_cupones(usuario_id);

-- Función para validar cupón
CREATE OR REPLACE FUNCTION validar_cupon(
  p_codigo varchar(50),
  p_usuario_id uuid,
  p_monto_compra decimal(10,2),
  p_productos_carrito uuid[]
) RETURNS jsonb AS $$
DECLARE
  cupon_info record;
  usos_usuario integer;
  resultado jsonb;
BEGIN
  -- Buscar el cupón
  SELECT * INTO cupon_info
  FROM cupones
  WHERE codigo_cupon = p_codigo
  AND activo = true
  AND fecha_inicio <= now()
  AND fecha_fin >= now();
  
  IF NOT FOUND THEN
    RETURN jsonb_build_object('valido', false, 'mensaje', 'Cupón no válido o expirado');
  END IF;
  
  -- Verificar usos máximos globales
  IF cupon_info.usos_maximos IS NOT NULL AND cupon_info.usos_actuales >= cupon_info.usos_maximos THEN
    RETURN jsonb_build_object('valido', false, 'mensaje', 'Cupón agotado');
  END IF;
  
  -- Verificar usos por usuario
  SELECT COUNT(*) INTO usos_usuario
  FROM uso_cupones
  WHERE cupon_id = cupon_info.id AND usuario_id = p_usuario_id;
  
  IF cupon_info.usos_por_usuario IS NOT NULL AND usos_usuario >= cupon_info.usos_por_usuario THEN
    RETURN jsonb_build_object('valido', false, 'mensaje', 'Ya utilizaste este cupón el máximo de veces permitido');
  END IF;
  
  -- Verificar monto mínimo
  IF cupon_info.monto_minimo_compra IS NOT NULL AND p_monto_compra < cupon_info.monto_minimo_compra THEN
    RETURN jsonb_build_object('valido', false, 'mensaje', 'El monto de compra no alcanza el mínimo requerido: S/ ' || cupon_info.monto_minimo_compra);
  END IF;
  
  -- Verificar si es solo para primera compra
  IF cupon_info.solo_primera_compra = true THEN
    IF EXISTS (SELECT 1 FROM pedidos WHERE usuario_id = p_usuario_id AND estado IN ('confirmado', 'entregado')) THEN
      RETURN jsonb_build_object('valido', false, 'mensaje', 'Este cupón es solo para la primera compra');
    END IF;
  END IF;
  
  -- Calcular descuento
  DECLARE
    descuento decimal(10,2);
  BEGIN
    IF cupon_info.tipo_descuento = 'porcentaje' THEN
      descuento := p_monto_compra * (cupon_info.valor_descuento / 100);
    ELSE
      descuento := cupon_info.valor_descuento;
    END IF;
    
    -- Aplicar máximo de descuento si existe
    IF cupon_info.monto_maximo_descuento IS NOT NULL AND descuento > cupon_info.monto_maximo_descuento THEN
      descuento := cupon_info.monto_maximo_descuento;
    END IF;
    
    RETURN jsonb_build_object(
      'valido', true,
      'cupon_id', cupon_info.id,
      'descuento', descuento,
      'tipo_descuento', cupon_info.tipo_descuento,
      'valor_descuento', cupon_info.valor_descuento
    );
  END;
END;
$$ LANGUAGE plpgsql;

-- Función para aplicar promociones automáticas
CREATE OR REPLACE FUNCTION aplicar_promociones_automaticas(
  p_productos_carrito jsonb,
  p_monto_subtotal decimal(10,2),
  p_sucursal_id uuid DEFAULT NULL
) RETURNS jsonb AS $$
DECLARE
  promocion record;
  descuentos jsonb := '[]';
  descuento_aplicado jsonb;
BEGIN
  -- Obtener promociones activas ordenadas por prioridad
  FOR promocion IN
    SELECT * FROM promociones
    WHERE activa = true
    AND fecha_inicio <= now()
    AND fecha_fin >= now()
    AND (p_sucursal_id IS NULL OR sucursales_incluidas IS NULL OR p_sucursal_id = ANY(sucursales_incluidas))
    AND EXTRACT(ISODOW FROM now()) = ANY(COALESCE(dias_semana, '{1,2,3,4,5,6,7}'))
    ORDER BY prioridad DESC
  LOOP
    -- Aquí se evaluarían las condiciones específicas de cada promoción
    -- Esta es una implementación básica que se puede extender
    
    IF promocion.tipo_promocion = 'descuento_cantidad' THEN
      -- Ejemplo: 10% descuento si compras 3 o más items
      IF (p_productos_carrito->>'cantidad_total')::integer >= (promocion.condiciones->>'cantidad_minima')::integer THEN
        descuento_aplicado := jsonb_build_object(
          'promocion_id', promocion.id,
          'nombre', promocion.nombre_promocion,
          'tipo', 'descuento_cantidad',
          'descuento', p_monto_subtotal * ((promocion.descuento->>'porcentaje')::decimal / 100)
        );
        descuentos := descuentos || descuento_aplicado;
      END IF;
    END IF;
  END LOOP;
  
  RETURN descuentos;
END;
$$ LANGUAGE plpgsql;

-- Insertar cupones de ejemplo
INSERT INTO cupones (codigo_cupon, nombre, descripcion, tipo_descuento, valor_descuento, monto_minimo_compra, fecha_inicio, fecha_fin, usos_maximos)
VALUES 
('BIENVENIDO10', 'Descuento de Bienvenida', '10% de descuento en tu primera compra', 'porcentaje', 10.00, 50.00, now(), now() + interval '30 days', 1000),
('VERANO25', 'Descuento de Verano', 'S/ 25 de descuento en compras mayores a S/ 150', 'monto_fijo', 25.00, 150.00, now(), now() + interval '60 days', 500)
ON CONFLICT (codigo_cupon) DO NOTHING;

-- Insertar promociones de ejemplo
INSERT INTO promociones (nombre_promocion, descripcion, tipo_promocion, condiciones, descuento, fecha_inicio, fecha_fin)
VALUES 
('Descuento por Cantidad', '15% descuento comprando 3 o más prendas', 'descuento_cantidad', '{"cantidad_minima": 3}', '{"tipo": "porcentaje", "valor": 15}', now(), now() + interval '90 days'),
('Oferta Fines de Semana', '20% descuento todos los fines de semana', 'descuento_categoria', '{"categorias": []}', '{"tipo": "porcentaje", "valor": 20}', now(), now() + interval '180 days')
ON CONFLICT DO NOTHING;