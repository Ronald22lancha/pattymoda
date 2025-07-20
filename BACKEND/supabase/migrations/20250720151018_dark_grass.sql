/*
  # Sistema de Auditoría y Configuración

  1. Nuevas Tablas
    - `auditoria` - Registro de todas las acciones críticas
    - `configuracion_sistema` - Configuraciones globales
    - `log_errores` - Registro de errores del sistema
    - `metricas_sistema` - Métricas y estadísticas
    - `backup_programado` - Control de backups automáticos

  2. Funcionalidades
    - Auditoría completa de acciones de usuarios
    - Configuración centralizada del sistema
    - Logging de errores y excepciones
    - Métricas de rendimiento y uso
    - Sistema de backup automático

  3. Monitoreo
    - Alertas de seguridad
    - Reportes de actividad
    - Análisis de uso del sistema
    - Control de integridad de datos
*/

-- Tabla de auditoría del sistema
CREATE TABLE IF NOT EXISTS auditoria (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id uuid REFERENCES usuarios(id),
  sesion_id varchar(255),
  accion varchar(100) NOT NULL,
  tabla_afectada varchar(100),
  registro_id uuid,
  datos_anteriores jsonb,
  datos_nuevos jsonb,
  ip_address varchar(45),
  user_agent text,
  modulo varchar(50), -- 'auth', 'productos', 'ventas', 'inventario', etc.
  severidad varchar(20) DEFAULT 'info', -- 'info', 'warning', 'error', 'critical'
  descripcion text,
  metadatos jsonb DEFAULT '{}',
  fecha_accion timestamptz DEFAULT now()
);

-- Tabla de configuración del sistema
CREATE TABLE IF NOT EXISTS configuracion_sistema (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  clave varchar(100) UNIQUE NOT NULL,
  valor text,
  tipo_dato varchar(20) DEFAULT 'string', -- 'string', 'number', 'boolean', 'json'
  categoria varchar(50), -- 'general', 'pagos', 'envios', 'notificaciones', etc.
  descripcion text,
  valor_por_defecto text,
  requerido boolean DEFAULT false,
  editable boolean DEFAULT true,
  validacion jsonb, -- Reglas de validación para el valor
  fecha_actualizacion timestamptz DEFAULT now(),
  actualizado_por uuid REFERENCES usuarios(id)
);

-- Tabla de log de errores
CREATE TABLE IF NOT EXISTS log_errores (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  nivel varchar(20) NOT NULL, -- 'ERROR', 'WARN', 'INFO', 'DEBUG'
  mensaje text NOT NULL,
  clase_origen varchar(200),
  metodo_origen varchar(100),
  linea_codigo integer,
  stack_trace text,
  usuario_id uuid REFERENCES usuarios(id),
  sesion_id varchar(255),
  url_solicitada varchar(500),
  parametros_request jsonb,
  headers_request jsonb,
  ip_address varchar(45),
  user_agent text,
  datos_contexto jsonb DEFAULT '{}',
  resuelto boolean DEFAULT false,
  fecha_resolucion timestamptz,
  notas_resolucion text,
  fecha_error timestamptz DEFAULT now()
);

-- Tabla de métricas del sistema
CREATE TABLE IF NOT EXISTS metricas_sistema (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre_metrica varchar(100) NOT NULL,
  valor decimal(15,4) NOT NULL,
  unidad varchar(50), -- 'segundos', 'bytes', 'cantidad', 'porcentaje'
  categoria varchar(50), -- 'rendimiento', 'uso', 'negocio', 'seguridad'
  etiquetas jsonb DEFAULT '{}', -- Tags adicionales para filtrado
  fecha_metrica timestamptz DEFAULT now(),
  fecha_creacion timestamptz DEFAULT now()
);

-- Tabla de control de backups
CREATE TABLE IF NOT EXISTS backup_programado (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre_backup varchar(100) NOT NULL,
  tipo_backup varchar(50) NOT NULL, -- 'completo', 'incremental', 'diferencial'
  estado varchar(30) DEFAULT 'programado', -- 'programado', 'ejecutando', 'completado', 'fallido'
  fecha_programada timestamptz NOT NULL,
  fecha_inicio timestamptz,
  fecha_fin timestamptz,
  duracion interval,
  tamaño_archivo bigint, -- En bytes
  ruta_archivo text,
  checksum varchar(255),
  tablas_incluidas text[],
  configuracion jsonb DEFAULT '{}',
  logs_ejecucion text,
  error_detalle text,
  retencion_dias integer DEFAULT 30,
  automatico boolean DEFAULT true,
  creado_por uuid REFERENCES usuarios(id),
  fecha_creacion timestamptz DEFAULT now()
);

-- Habilitar RLS
ALTER TABLE auditoria ENABLE ROW LEVEL SECURITY;
ALTER TABLE configuracion_sistema ENABLE ROW LEVEL SECURITY;
ALTER TABLE log_errores ENABLE ROW LEVEL SECURITY;
ALTER TABLE metricas_sistema ENABLE ROW LEVEL SECURITY;
ALTER TABLE backup_programado ENABLE ROW LEVEL SECURITY;

-- Políticas de seguridad (solo administradores)
CREATE POLICY "Solo admins acceden a auditoría"
  ON auditoria FOR ALL
  TO authenticated
  USING (EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol = 'Administrador'
  ));

CREATE POLICY "Solo admins gestionan configuración"
  ON configuracion_sistema FOR ALL
  TO authenticated
  USING (EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol = 'Administrador'
  ));

CREATE POLICY "Solo admins ven logs de errores"
  ON log_errores FOR ALL
  TO authenticated
  USING (EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol = 'Administrador'
  ));

-- Índices de optimización
CREATE INDEX IF NOT EXISTS idx_auditoria_usuario ON auditoria(usuario_id);
CREATE INDEX IF NOT EXISTS idx_auditoria_fecha ON auditoria(fecha_accion);
CREATE INDEX IF NOT EXISTS idx_auditoria_accion ON auditoria(accion);
CREATE INDEX IF NOT EXISTS idx_auditoria_tabla ON auditoria(tabla_afectada);
CREATE INDEX IF NOT EXISTS idx_configuracion_categoria ON configuracion_sistema(categoria);
CREATE INDEX IF NOT EXISTS idx_log_errores_nivel ON log_errores(nivel);
CREATE INDEX IF NOT EXISTS idx_log_errores_fecha ON log_errores(fecha_error);
CREATE INDEX IF NOT EXISTS idx_metricas_nombre ON metricas_sistema(nombre_metrica);
CREATE INDEX IF NOT EXISTS idx_metricas_fecha ON metricas_sistema(fecha_metrica);
CREATE INDEX IF NOT EXISTS idx_backup_estado ON backup_programado(estado);

-- Función para registrar auditoría automática
CREATE OR REPLACE FUNCTION registrar_auditoria(
  p_usuario_id uuid,
  p_accion varchar(100),
  p_tabla varchar(100),
  p_registro_id uuid,
  p_datos_anteriores jsonb DEFAULT NULL,
  p_datos_nuevos jsonb DEFAULT NULL,
  p_descripcion text DEFAULT NULL
) RETURNS void AS $$
BEGIN
  INSERT INTO auditoria (
    usuario_id, accion, tabla_afectada, registro_id,
    datos_anteriores, datos_nuevos, descripcion
  ) VALUES (
    p_usuario_id, p_accion, p_tabla, p_registro_id,
    p_datos_anteriores, p_datos_nuevos, p_descripcion
  );
END;
$$ LANGUAGE plpgsql;

-- Función para registrar métricas del sistema
CREATE OR REPLACE FUNCTION registrar_metrica(
  p_nombre varchar(100),
  p_valor decimal(15,4),
  p_unidad varchar(50) DEFAULT NULL,
  p_categoria varchar(50) DEFAULT 'general',
  p_etiquetas jsonb DEFAULT '{}'
) RETURNS void AS $$
BEGIN
  INSERT INTO metricas_sistema (
    nombre_metrica, valor, unidad, categoria, etiquetas
  ) VALUES (
    p_nombre, p_valor, p_unidad, p_categoria, p_etiquetas
  );
END;
$$ LANGUAGE plpgsql;

-- Insertar configuraciones por defecto del sistema
INSERT INTO configuracion_sistema (clave, valor, tipo_dato, categoria, descripcion) VALUES
('nombre_empresa', 'DPattyModa', 'string', 'general', 'Nombre de la empresa'),
('ruc_empresa', '20123456789', 'string', 'general', 'RUC de la empresa'),
('direccion_empresa', 'Pampa Hermosa, Loreto, Perú', 'string', 'general', 'Dirección principal'),
('telefono_empresa', '+51 XXX XXX XXX', 'string', 'general', 'Teléfono de contacto'),
('email_empresa', 'contacto@dpattymoda.com', 'string', 'general', 'Email de contacto'),
('moneda_principal', 'PEN', 'string', 'general', 'Moneda principal del sistema'),
('igv_porcentaje', '18', 'number', 'general', 'Porcentaje de IGV aplicable'),
('costo_envio_local', '10.00', 'number', 'envios', 'Costo de envío en Pampa Hermosa'),
('costo_envio_nacional', '25.00', 'number', 'envios', 'Costo de envío nacional'),
('stock_minimo_alerta', '5', 'number', 'inventario', 'Cantidad mínima para alerta de stock'),
('dias_retencion_carrito', '30', 'number', 'general', 'Días antes de limpiar carritos abandonados'),
('max_intentos_login', '5', 'number', 'seguridad', 'Máximo intentos de login antes de bloqueo'),
('tiempo_bloqueo_minutos', '30', 'number', 'seguridad', 'Minutos de bloqueo tras intentos fallidos'),
('email_notificaciones', 'true', 'boolean', 'notificaciones', 'Enviar notificaciones por email'),
('modo_mantenimiento', 'false', 'boolean', 'general', 'Modo de mantenimiento del sistema'),
('version_sistema', '1.0.0', 'string', 'general', 'Versión actual del sistema'),
('backup_automatico', 'true', 'boolean', 'backup', 'Realizar backups automáticos'),
('frecuencia_backup_horas', '24', 'number', 'backup', 'Frecuencia de backup en horas')
ON CONFLICT (clave) DO NOTHING;

-- Vista para métricas de negocio
CREATE OR REPLACE VIEW vista_metricas_negocio AS
SELECT 
  DATE(fecha_creacion) as fecha,
  COUNT(*) as total_pedidos,
  SUM(total) as ventas_totales,
  AVG(total) as ticket_promedio,
  COUNT(DISTINCT usuario_id) as clientes_unicos,
  SUM(CASE WHEN tipo_venta = 'online' THEN 1 ELSE 0 END) as ventas_online,
  SUM(CASE WHEN tipo_venta = 'presencial' THEN 1 ELSE 0 END) as ventas_presenciales,
  SUM(CASE WHEN estado = 'cancelado' THEN 1 ELSE 0 END) as pedidos_cancelados
FROM pedidos
WHERE estado NOT IN ('cancelado')
GROUP BY DATE(fecha_creacion)
ORDER BY fecha DESC;

-- Vista para productos más vendidos
CREATE OR REPLACE VIEW vista_productos_mas_vendidos AS
SELECT 
  p.id,
  p.nombre_producto,
  p.codigo_producto,
  c.nombre_categoria,
  SUM(dp.cantidad) as total_vendido,
  SUM(dp.subtotal) as total_ingresos,
  COUNT(DISTINCT dp.pedido_id) as pedidos_diferentes,
  AVG(p.calificacion_promedio) as calificacion_promedio
FROM productos p
JOIN detalle_pedidos dp ON p.id = (
  SELECT vp.producto_id FROM variantes_producto vp WHERE vp.id = dp.variante_id
)
JOIN categorias c ON p.categoria_id = c.id
JOIN pedidos pe ON dp.pedido_id = pe.id
WHERE pe.estado IN ('confirmado', 'entregado')
GROUP BY p.id, p.nombre_producto, p.codigo_producto, c.nombre_categoria
ORDER BY total_vendido DESC;

-- Trigger genérico para auditoría de cambios críticos
CREATE OR REPLACE FUNCTION trigger_auditoria_generica() RETURNS trigger AS $$
DECLARE
  usuario_actual uuid;
  accion_realizada varchar(20);
BEGIN
  usuario_actual := auth.uid();
  
  IF TG_OP = 'INSERT' THEN
    accion_realizada := 'CREAR';
    PERFORM registrar_auditoria(
      usuario_actual, accion_realizada, TG_TABLE_NAME, NEW.id, NULL, to_jsonb(NEW)
    );
    RETURN NEW;
  ELSIF TG_OP = 'UPDATE' THEN
    accion_realizada := 'ACTUALIZAR';
    PERFORM registrar_auditoria(
      usuario_actual, accion_realizada, TG_TABLE_NAME, NEW.id, to_jsonb(OLD), to_jsonb(NEW)
    );
    RETURN NEW;
  ELSIF TG_OP = 'DELETE' THEN
    accion_realizada := 'ELIMINAR';
    PERFORM registrar_auditoria(
      usuario_actual, accion_realizada, TG_TABLE_NAME, OLD.id, to_jsonb(OLD), NULL
    );
    RETURN OLD;
  END IF;
  
  RETURN NULL;
END;
$$ LANGUAGE plpgsql;

-- Aplicar triggers de auditoría a tablas críticas
CREATE TRIGGER trigger_auditoria_productos
  AFTER INSERT OR UPDATE OR DELETE ON productos
  FOR EACH ROW EXECUTE FUNCTION trigger_auditoria_generica();

CREATE TRIGGER trigger_auditoria_usuarios
  AFTER INSERT OR UPDATE OR DELETE ON usuarios
  FOR EACH ROW EXECUTE FUNCTION trigger_auditoria_generica();

CREATE TRIGGER trigger_auditoria_pedidos
  AFTER INSERT OR UPDATE OR DELETE ON pedidos
  FOR EACH ROW EXECUTE FUNCTION trigger_auditoria_generica();

CREATE TRIGGER trigger_auditoria_inventario
  AFTER INSERT OR UPDATE OR DELETE ON inventario
  FOR EACH ROW EXECUTE FUNCTION trigger_auditoria_generica();