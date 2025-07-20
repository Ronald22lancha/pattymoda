/*
  # Sistema de Pedidos y Ventas

  1. Nuevas Tablas
    - `direcciones_envio` - Direcciones de entrega de clientes
    - `pedidos` - Pedidos online y ventas presenciales
    - `detalle_pedidos` - Items de cada pedido
    - `pagos` - Registro de transacciones de pago
    - `comprobantes` - Boletas y facturas electrónicas
    - `envios` - Seguimiento de envíos
    - `devoluciones` - Gestión de devoluciones y cambios

  2. Estados y Flujos
    - Estados de pedidos (pendiente, procesando, enviado, entregado, cancelado)
    - Estados de pagos (pendiente, procesado, fallido, reembolsado)
    - Estados de envíos (preparando, en_camino, entregado)

  3. Integraciones
    - Preparado para integración con SUNAT
    - Preparado para integración con transportistas
    - Sistema de reembolsos
*/

-- Tabla de direcciones de envío
CREATE TABLE IF NOT EXISTS direcciones_envio (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id uuid REFERENCES usuarios(id) ON DELETE CASCADE,
  alias_direccion varchar(100),
  nombres_destinatario varchar(100) NOT NULL,
  apellidos_destinatario varchar(100) NOT NULL,
  telefono_destinatario varchar(20),
  direccion_linea1 text NOT NULL,
  direccion_linea2 text,
  ciudad varchar(100) NOT NULL,
  departamento varchar(100) NOT NULL,
  codigo_postal varchar(20),
  pais varchar(100) DEFAULT 'Perú',
  referencia text,
  coordenadas_gps point,
  es_principal boolean DEFAULT false,
  activa boolean DEFAULT true,
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de pedidos
CREATE TABLE IF NOT EXISTS pedidos (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  numero_pedido varchar(50) UNIQUE NOT NULL,
  usuario_id uuid REFERENCES usuarios(id),
  sucursal_id uuid REFERENCES sucursales(id),
  direccion_envio_id uuid REFERENCES direcciones_envio(id),
  tipo_venta varchar(20) NOT NULL DEFAULT 'online', -- 'online', 'presencial'
  estado varchar(30) NOT NULL DEFAULT 'pendiente', -- 'pendiente', 'confirmado', 'procesando', 'enviado', 'entregado', 'cancelado'
  subtotal decimal(10,2) NOT NULL DEFAULT 0,
  descuento_total decimal(10,2) DEFAULT 0,
  impuestos_total decimal(10,2) DEFAULT 0,
  costo_envio decimal(10,2) DEFAULT 0,
  total decimal(10,2) NOT NULL DEFAULT 0,
  moneda varchar(10) DEFAULT 'PEN',
  metodo_pago varchar(50), -- 'efectivo', 'tarjeta', 'yape', 'plin', 'lukita', 'paypal', etc.
  estado_pago varchar(30) DEFAULT 'pendiente', -- 'pendiente', 'procesado', 'fallido', 'reembolsado'
  notas_cliente text,
  notas_internas text,
  cupones_aplicados jsonb DEFAULT '[]',
  datos_cliente jsonb, -- Para ventas presenciales sin registro
  fecha_estimada_entrega timestamptz,
  fecha_entrega_real timestamptz,
  vendedor_id uuid REFERENCES usuarios(id),
  caja_id uuid, -- Referencia a la caja donde se realizó la venta presencial
  comprobante_requerido boolean DEFAULT false,
  tipo_comprobante varchar(20), -- 'boleta', 'factura'
  datos_facturacion jsonb,
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de detalle de pedidos
CREATE TABLE IF NOT EXISTS detalle_pedidos (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  pedido_id uuid REFERENCES pedidos(id) ON DELETE CASCADE,
  variante_id uuid REFERENCES variantes_producto(id),
  cantidad integer NOT NULL DEFAULT 1,
  precio_unitario decimal(10,2) NOT NULL,
  descuento_unitario decimal(10,2) DEFAULT 0,
  subtotal decimal(10,2) GENERATED ALWAYS AS (cantidad * (precio_unitario - COALESCE(descuento_unitario, 0))) STORED,
  datos_producto jsonb, -- Snapshot del producto al momento de la venta
  fecha_creacion timestamptz DEFAULT now()
);

-- Tabla de pagos
CREATE TABLE IF NOT EXISTS pagos (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  pedido_id uuid REFERENCES pedidos(id) ON DELETE CASCADE,
  metodo_pago varchar(50) NOT NULL,
  monto decimal(10,2) NOT NULL,
  moneda varchar(10) DEFAULT 'PEN',
  estado varchar(30) NOT NULL DEFAULT 'pendiente', -- 'pendiente', 'procesado', 'fallido', 'reembolsado'
  referencia_externa varchar(255), -- ID de transacción del proveedor de pagos
  datos_transaccion jsonb, -- Respuesta completa del proveedor
  fecha_procesamiento timestamptz,
  fecha_vencimiento timestamptz,
  intentos_procesamiento integer DEFAULT 0,
  comision decimal(10,2) DEFAULT 0,
  monto_neto decimal(10,2),
  notas text,
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de comprobantes electrónicos
CREATE TABLE IF NOT EXISTS comprobantes (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  pedido_id uuid REFERENCES pedidos(id) ON DELETE CASCADE,
  tipo_comprobante varchar(20) NOT NULL, -- 'boleta', 'factura'
  serie varchar(10) NOT NULL,
  numero integer NOT NULL,
  numero_completo varchar(50) GENERATED ALWAYS AS (serie || '-' || LPAD(numero::text, 8, '0')) STORED,
  ruc_emisor varchar(20) NOT NULL,
  razon_social_emisor varchar(200) NOT NULL,
  direccion_emisor text,
  documento_receptor varchar(20),
  tipo_documento_receptor varchar(10), -- 'DNI', 'RUC', 'CE'
  nombre_receptor varchar(200),
  direccion_receptor text,
  subtotal decimal(10,2) NOT NULL,
  igv decimal(10,2) NOT NULL,
  total decimal(10,2) NOT NULL,
  moneda varchar(10) DEFAULT 'PEN',
  fecha_emision timestamptz DEFAULT now(),
  fecha_vencimiento timestamptz,
  estado_sunat varchar(30), -- 'pendiente', 'aceptado', 'rechazado'
  codigo_hash varchar(255),
  xml_firmado text,
  pdf_url varchar(500),
  observaciones text,
  fecha_envio_sunat timestamptz,
  fecha_respuesta_sunat timestamptz,
  codigo_respuesta_sunat varchar(10),
  descripcion_respuesta_sunat text,
  anulado boolean DEFAULT false,
  fecha_anulacion timestamptz,
  motivo_anulacion text,
  fecha_creacion timestamptz DEFAULT now(),
  UNIQUE(tipo_comprobante, serie, numero)
);

-- Tabla de envíos
CREATE TABLE IF NOT EXISTS envios (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  pedido_id uuid REFERENCES pedidos(id) ON DELETE CASCADE,
  transportista varchar(100),
  numero_guia varchar(100),
  estado varchar(30) DEFAULT 'preparando', -- 'preparando', 'en_camino', 'entregado', 'devuelto'
  fecha_despacho timestamptz,
  fecha_entrega_estimada timestamptz,
  fecha_entrega_real timestamptz,
  costo_envio decimal(10,2),
  peso_total decimal(8,3),
  dimensiones jsonb,
  direccion_origen text,
  direccion_destino text,
  instrucciones_entrega text,
  evidencia_entrega jsonb, -- URLs de fotos, firma digital, etc.
  seguimiento jsonb DEFAULT '[]', -- Array de eventos de seguimiento
  intentos_entrega integer DEFAULT 0,
  max_intentos_entrega integer DEFAULT 3,
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de devoluciones
CREATE TABLE IF NOT EXISTS devoluciones (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  pedido_id uuid REFERENCES pedidos(id) ON DELETE CASCADE,
  numero_devolucion varchar(50) UNIQUE NOT NULL,
  tipo varchar(20) NOT NULL, -- 'devolucion', 'cambio'
  motivo varchar(100) NOT NULL,
  descripcion_detallada text,
  estado varchar(30) DEFAULT 'solicitada', -- 'solicitada', 'aprobada', 'rechazada', 'procesando', 'completada'
  items_devolucion jsonb NOT NULL, -- Array de items con cantidades
  monto_devolucion decimal(10,2),
  metodo_reembolso varchar(50),
  fecha_solicitud timestamptz DEFAULT now(),
  fecha_aprobacion timestamptz,
  fecha_recepcion_items timestamptz,
  fecha_completada timestamptz,
  evidencia_fotos jsonb, -- URLs de fotos del estado del producto
  aprobado_por uuid REFERENCES usuarios(id),
  procesado_por uuid REFERENCES usuarios(id),
  notas_cliente text,
  notas_internas text,
  costo_envio_devolucion decimal(10,2),
  numero_guia_devolucion varchar(100),
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Habilitar RLS
ALTER TABLE direcciones_envio ENABLE ROW LEVEL SECURITY;
ALTER TABLE pedidos ENABLE ROW LEVEL SECURITY;
ALTER TABLE detalle_pedidos ENABLE ROW LEVEL SECURITY;
ALTER TABLE pagos ENABLE ROW LEVEL SECURITY;
ALTER TABLE comprobantes ENABLE ROW LEVEL SECURITY;
ALTER TABLE envios ENABLE ROW LEVEL SECURITY;
ALTER TABLE devoluciones ENABLE ROW LEVEL SECURITY;

-- Políticas de seguridad
CREATE POLICY "Usuarios ven sus direcciones"
  ON direcciones_envio FOR ALL
  TO authenticated
  USING (usuario_id = auth.uid());

CREATE POLICY "Usuarios ven sus pedidos"
  ON pedidos FOR SELECT
  TO authenticated
  USING (usuario_id = auth.uid() OR EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol IN ('Administrador', 'Empleado', 'Cajero')
  ));

-- Índices de optimización
CREATE INDEX IF NOT EXISTS idx_pedidos_usuario ON pedidos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_pedidos_estado ON pedidos(estado);
CREATE INDEX IF NOT EXISTS idx_pedidos_fecha ON pedidos(fecha_creacion);
CREATE INDEX IF NOT EXISTS idx_pedidos_numero ON pedidos(numero_pedido);
CREATE INDEX IF NOT EXISTS idx_detalle_pedidos_pedido ON detalle_pedidos(pedido_id);
CREATE INDEX IF NOT EXISTS idx_pagos_pedido ON pagos(pedido_id);
CREATE INDEX IF NOT EXISTS idx_pagos_estado ON pagos(estado);
CREATE INDEX IF NOT EXISTS idx_comprobantes_pedido ON comprobantes(pedido_id);
CREATE INDEX IF NOT EXISTS idx_comprobantes_numero ON comprobantes(numero_completo);
CREATE INDEX IF NOT EXISTS idx_envios_pedido ON envios(pedido_id);
CREATE INDEX IF NOT EXISTS idx_envios_guia ON envios(numero_guia);
CREATE INDEX IF NOT EXISTS idx_devoluciones_pedido ON devoluciones(pedido_id);

-- Función para generar número de pedido
CREATE OR REPLACE FUNCTION generar_numero_pedido() RETURNS text AS $$
DECLARE
  nuevo_numero text;
  contador integer;
BEGIN
  SELECT COALESCE(MAX(CAST(SUBSTRING(numero_pedido FROM 9) AS integer)), 0) + 1
  INTO contador
  FROM pedidos
  WHERE numero_pedido LIKE 'DPM' || TO_CHAR(NOW(), 'YYYY') || '%';
  
  nuevo_numero := 'DPM' || TO_CHAR(NOW(), 'YYYY') || LPAD(contador::text, 6, '0');
  RETURN nuevo_numero;
END;
$$ LANGUAGE plpgsql;

-- Trigger para asignar número de pedido automáticamente
CREATE OR REPLACE FUNCTION trigger_asignar_numero_pedido() RETURNS trigger AS $$
BEGIN
  IF NEW.numero_pedido IS NULL OR NEW.numero_pedido = '' THEN
    NEW.numero_pedido := generar_numero_pedido();
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_pedidos_numero_automatico
  BEFORE INSERT ON pedidos
  FOR EACH ROW
  EXECUTE FUNCTION trigger_asignar_numero_pedido();