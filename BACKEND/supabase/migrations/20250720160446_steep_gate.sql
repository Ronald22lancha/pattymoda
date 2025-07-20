/*
  # Completar Sistema DPattyModa - Módulos Finales

  1. Nuevas Tablas
    - `chats` - Sistema de chat en tiempo real
    - `mensajes` - Mensajes de chat
    - `reseñas` - Calificaciones de productos
    - `notificaciones` - Sistema de notificaciones
    - `devoluciones` - Gestión de devoluciones
    - `envios` - Seguimiento de envíos
    - `comprobantes` - Facturación electrónica

  2. Funcionalidades Avanzadas
    - Chat en tiempo real con WebSocket
    - Sistema de reseñas con moderación
    - Notificaciones multi-canal
    - Devoluciones y cambios
    - Seguimiento de envíos
    - Facturación electrónica SUNAT

  3. Optimizaciones Finales
    - Índices de rendimiento
    - Triggers automáticos
    - Vistas materializadas
    - Funciones de negocio
*/

-- Tabla de chats/conversaciones
CREATE TABLE IF NOT EXISTS chats (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id uuid REFERENCES usuarios(id) ON DELETE CASCADE,
  empleado_asignado_id uuid REFERENCES usuarios(id),
  producto_id uuid REFERENCES productos(id),
  pedido_id uuid REFERENCES pedidos(id),
  asunto varchar(200),
  estado varchar(20) DEFAULT 'abierto', -- 'abierto', 'en_progreso', 'cerrado', 'escalado'
  prioridad varchar(20) DEFAULT 'normal', -- 'baja', 'normal', 'alta', 'urgente'
  categoria varchar(50), -- 'consulta_producto', 'problema_pedido', 'devolucion', 'general'
  etiquetas text[],
  satisfaccion_cliente integer CHECK (satisfaccion_cliente >= 1 AND satisfaccion_cliente <= 5),
  comentario_satisfaccion text,
  fecha_primer_mensaje timestamptz DEFAULT now(),
  fecha_ultimo_mensaje timestamptz DEFAULT now(),
  fecha_cierre timestamptz,
  tiempo_primera_respuesta interval,
  tiempo_resolucion interval,
  cerrado_por uuid REFERENCES usuarios(id),
  motivo_cierre varchar(100),
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de mensajes
CREATE TABLE IF NOT EXISTS mensajes (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  chat_id uuid REFERENCES chats(id) ON DELETE CASCADE,
  remitente_id uuid REFERENCES usuarios(id),
  tipo_remitente varchar(20) NOT NULL, -- 'cliente', 'empleado', 'sistema'
  contenido text NOT NULL,
  tipo_mensaje varchar(20) DEFAULT 'texto', -- 'texto', 'imagen', 'archivo', 'sistema'
  archivos_adjuntos jsonb DEFAULT '[]',
  mensaje_padre_id uuid REFERENCES mensajes(id),
  estado varchar(20) DEFAULT 'enviado', -- 'enviado', 'entregado', 'leido'
  editado boolean DEFAULT false,
  fecha_edicion timestamptz,
  moderado boolean DEFAULT false,
  fecha_moderacion timestamptz,
  moderado_por uuid REFERENCES usuarios(id),
  contenido_original text,
  fecha_lectura timestamptz,
  reacciones jsonb DEFAULT '{}',
  fecha_envio timestamptz DEFAULT now(),
  fecha_creacion timestamptz DEFAULT now()
);

-- Tabla de reseñas de productos
CREATE TABLE IF NOT EXISTS reseñas (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  producto_id uuid REFERENCES productos(id) ON DELETE CASCADE,
  usuario_id uuid REFERENCES usuarios(id) ON DELETE CASCADE,
  pedido_id uuid REFERENCES pedidos(id),
  calificacion integer NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
  titulo varchar(200),
  comentario text,
  ventajas text[],
  desventajas text[],
  recomendaria boolean,
  verificada boolean DEFAULT false,
  estado_moderacion varchar(20) DEFAULT 'pendiente', -- 'pendiente', 'aprobada', 'rechazada'
  fecha_moderacion timestamptz,
  moderado_por uuid REFERENCES usuarios(id),
  motivo_rechazo text,
  utilidad_positiva integer DEFAULT 0,
  utilidad_negativa integer DEFAULT 0,
  reportes integer DEFAULT 0,
  imagenes jsonb DEFAULT '[]',
  variante_comprada_id uuid REFERENCES variantes_producto(id),
  talla_comprada varchar(20),
  color_comprado varchar(50),
  fecha_compra timestamptz,
  fecha_resena timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now(),
  UNIQUE(producto_id, usuario_id)
);

-- Tabla de notificaciones
CREATE TABLE IF NOT EXISTS notificaciones (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id uuid REFERENCES usuarios(id) ON DELETE CASCADE,
  tipo varchar(50) NOT NULL,
  titulo varchar(200) NOT NULL,
  mensaje text NOT NULL,
  datos jsonb,
  canal varchar(20) NOT NULL, -- 'email', 'push', 'sms', 'in_app'
  leida boolean DEFAULT false,
  fecha_lectura timestamptz,
  enviada boolean DEFAULT false,
  fecha_envio timestamptz,
  intentos_envio integer DEFAULT 0,
  error_envio text,
  url_accion varchar(500),
  prioridad varchar(20) DEFAULT 'normal',
  expira_en timestamptz,
  fecha_creacion timestamptz DEFAULT now()
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
  items_devolucion jsonb NOT NULL,
  monto_devolucion decimal(10,2),
  metodo_reembolso varchar(50),
  fecha_solicitud timestamptz DEFAULT now(),
  fecha_aprobacion timestamptz,
  fecha_recepcion_items timestamptz,
  fecha_completada timestamptz,
  evidencia_fotos jsonb,
  aprobado_por uuid REFERENCES usuarios(id),
  procesado_por uuid REFERENCES usuarios(id),
  notas_cliente text,
  notas_internas text,
  costo_envio_devolucion decimal(10,2),
  numero_guia_devolucion varchar(100),
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
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
  evidencia_entrega jsonb,
  seguimiento jsonb DEFAULT '[]',
  intentos_entrega integer DEFAULT 0,
  max_intentos_entrega integer DEFAULT 3,
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

-- Habilitar RLS en todas las nuevas tablas
ALTER TABLE chats ENABLE ROW LEVEL SECURITY;
ALTER TABLE mensajes ENABLE ROW LEVEL SECURITY;
ALTER TABLE reseñas ENABLE ROW LEVEL SECURITY;
ALTER TABLE notificaciones ENABLE ROW LEVEL SECURITY;
ALTER TABLE devoluciones ENABLE ROW LEVEL SECURITY;
ALTER TABLE envios ENABLE ROW LEVEL SECURITY;
ALTER TABLE comprobantes ENABLE ROW LEVEL SECURITY;

-- Políticas de seguridad adicionales
CREATE POLICY "Usuarios ven sus chats"
  ON chats FOR ALL
  TO authenticated
  USING (usuario_id = auth.uid() OR empleado_asignado_id = auth.uid() OR EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol IN ('Administrador', 'Empleado')
  ));

CREATE POLICY "Participantes ven mensajes del chat"
  ON mensajes FOR ALL
  TO authenticated
  USING (EXISTS (
    SELECT 1 FROM chats c
    WHERE c.id = chat_id 
    AND (c.usuario_id = auth.uid() OR c.empleado_asignado_id = auth.uid())
  ) OR EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol IN ('Administrador', 'Empleado')
  ));

CREATE POLICY "Usuarios ven reseñas aprobadas"
  ON reseñas FOR SELECT
  TO anon, authenticated
  USING (estado_moderacion = 'aprobada');

CREATE POLICY "Usuarios gestionan sus reseñas"
  ON reseñas FOR INSERT, UPDATE
  TO authenticated
  USING (usuario_id = auth.uid());

CREATE POLICY "Usuarios ven sus notificaciones"
  ON notificaciones FOR ALL
  TO authenticated
  USING (usuario_id = auth.uid());

CREATE POLICY "Usuarios ven sus devoluciones"
  ON devoluciones FOR SELECT
  TO authenticated
  USING (EXISTS (
    SELECT 1 FROM pedidos p 
    WHERE p.id = pedido_id AND p.usuario_id = auth.uid()
  ) OR EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol IN ('Administrador', 'Empleado')
  ));

-- Índices de optimización para las nuevas tablas
CREATE INDEX IF NOT EXISTS idx_chats_usuario ON chats(usuario_id);
CREATE INDEX IF NOT EXISTS idx_chats_empleado ON chats(empleado_asignado_id);
CREATE INDEX IF NOT EXISTS idx_chats_estado ON chats(estado);
CREATE INDEX IF NOT EXISTS idx_mensajes_chat ON mensajes(chat_id);
CREATE INDEX IF NOT EXISTS idx_mensajes_remitente ON mensajes(remitente_id);
CREATE INDEX IF NOT EXISTS idx_mensajes_fecha ON mensajes(fecha_envio);
CREATE INDEX IF NOT EXISTS idx_reseñas_producto ON reseñas(producto_id);
CREATE INDEX IF NOT EXISTS idx_reseñas_usuario ON reseñas(usuario_id);
CREATE INDEX IF NOT EXISTS idx_reseñas_estado ON reseñas(estado_moderacion);
CREATE INDEX IF NOT EXISTS idx_notificaciones_usuario ON notificaciones(usuario_id);
CREATE INDEX IF NOT EXISTS idx_notificaciones_leida ON notificaciones(leida);
CREATE INDEX IF NOT EXISTS idx_devoluciones_pedido ON devoluciones(pedido_id);
CREATE INDEX IF NOT EXISTS idx_devoluciones_estado ON devoluciones(estado);
CREATE INDEX IF NOT EXISTS idx_envios_pedido ON envios(pedido_id);
CREATE INDEX IF NOT EXISTS idx_envios_guia ON envios(numero_guia);
CREATE INDEX IF NOT EXISTS idx_comprobantes_pedido ON comprobantes(pedido_id);
CREATE INDEX IF NOT EXISTS idx_comprobantes_numero ON comprobantes(numero_completo);

-- Función para generar número de devolución
CREATE OR REPLACE FUNCTION generar_numero_devolucion() RETURNS text AS $$
DECLARE
  nuevo_numero text;
  contador integer;
BEGIN
  SELECT COALESCE(MAX(CAST(SUBSTRING(numero_devolucion FROM 9) AS integer)), 0) + 1
  INTO contador
  FROM devoluciones
  WHERE numero_devolucion LIKE 'DEV' || TO_CHAR(NOW(), 'YYYY') || '%';
  
  nuevo_numero := 'DEV' || TO_CHAR(NOW(), 'YYYY') || LPAD(contador::text, 6, '0');
  RETURN nuevo_numero;
END;
$$ LANGUAGE plpgsql;

-- Trigger para asignar número de devolución automáticamente
CREATE OR REPLACE FUNCTION trigger_asignar_numero_devolucion() RETURNS trigger AS $$
BEGIN
  IF NEW.numero_devolucion IS NULL OR NEW.numero_devolucion = '' THEN
    NEW.numero_devolucion := generar_numero_devolucion();
  END IF;
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_devoluciones_numero_automatico
  BEFORE INSERT ON devoluciones
  FOR EACH ROW
  EXECUTE FUNCTION trigger_asignar_numero_devolucion();

-- Función para actualizar calificación promedio del producto
CREATE OR REPLACE FUNCTION actualizar_calificacion_producto() RETURNS trigger AS $$
DECLARE
  producto_id_actualizar uuid;
  nueva_calificacion decimal(3,2);
  total_reseñas_producto integer;
BEGIN
  producto_id_actualizar := COALESCE(NEW.producto_id, OLD.producto_id);
  
  SELECT 
    ROUND(AVG(calificacion)::numeric, 2),
    COUNT(*)
  INTO nueva_calificacion, total_reseñas_producto
  FROM reseñas 
  WHERE producto_id = producto_id_actualizar 
  AND estado_moderacion = 'aprobada';
  
  UPDATE productos SET
    calificacion_promedio = COALESCE(nueva_calificacion, 0),
    total_reseñas = COALESCE(total_reseñas_producto, 0),
    fecha_actualizacion = now()
  WHERE id = producto_id_actualizar;
  
  RETURN COALESCE(NEW, OLD);
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_actualizar_calificacion_producto
  AFTER INSERT OR UPDATE OR DELETE ON reseñas
  FOR EACH ROW
  EXECUTE FUNCTION actualizar_calificacion_producto();

-- Función para detectar contenido inapropiado
CREATE OR REPLACE FUNCTION detectar_contenido_inapropiado(texto text) RETURNS jsonb AS $$
DECLARE
  palabras_prohibidas text[] := ARRAY['spam', 'estafa', 'fake', 'horrible', 'pésimo', 'basura', 'robo'];
  palabra text;
  puntuacion decimal := 0;
  palabras_encontradas text[] := '{}';
BEGIN
  FOREACH palabra IN ARRAY palabras_prohibidas
  LOOP
    IF lower(texto) LIKE '%' || lower(palabra) || '%' THEN
      palabras_encontradas := array_append(palabras_encontradas, palabra);
      puntuacion := puntuacion + 0.3;
    END IF;
  END LOOP;
  
  RETURN jsonb_build_object(
    'puntuacion', LEAST(puntuacion, 1.0),
    'palabras_encontradas', palabras_encontradas,
    'requiere_revision', puntuacion > 0.5
  );
END;
$$ LANGUAGE plpgsql;

-- Trigger para moderación automática de reseñas
CREATE OR REPLACE FUNCTION trigger_moderacion_automatica_resena() RETURNS trigger AS $$
DECLARE
  resultado_moderacion jsonb;
BEGIN
  resultado_moderacion := detectar_contenido_inapropiado(COALESCE(NEW.titulo, '') || ' ' || COALESCE(NEW.comentario, ''));
  
  IF (resultado_moderacion->>'requiere_revision')::boolean THEN
    NEW.estado_moderacion := 'pendiente';
  ELSE
    NEW.estado_moderacion := 'aprobada';
  END IF;
  
  RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trigger_moderacion_resena
  BEFORE INSERT ON reseñas
  FOR EACH ROW
  EXECUTE FUNCTION trigger_moderacion_automatica_resena();

-- Vista materializada para métricas de negocio
CREATE MATERIALIZED VIEW IF NOT EXISTS vista_metricas_diarias AS
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

-- Índice para la vista materializada
CREATE UNIQUE INDEX IF NOT EXISTS idx_vista_metricas_fecha ON vista_metricas_diarias(fecha);

-- Función para refrescar métricas diarias
CREATE OR REPLACE FUNCTION refrescar_metricas_diarias() RETURNS void AS $$
BEGIN
  REFRESH MATERIALIZED VIEW CONCURRENTLY vista_metricas_diarias;
END;
$$ LANGUAGE plpgsql;

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
  p.calificacion_promedio
FROM productos p
JOIN variantes_producto vp ON p.id = vp.producto_id
JOIN detalle_pedidos dp ON vp.id = dp.variante_id
JOIN categorias c ON p.categoria_id = c.id
JOIN pedidos pe ON dp.pedido_id = pe.id
WHERE pe.estado IN ('confirmado', 'entregado')
AND pe.fecha_creacion >= NOW() - INTERVAL '30 days'
GROUP BY p.id, p.nombre_producto, p.codigo_producto, c.nombre_categoria, p.calificacion_promedio
ORDER BY total_vendido DESC;

-- Función para limpiar datos antiguos
CREATE OR REPLACE FUNCTION limpiar_datos_antiguos() RETURNS void AS $$
BEGIN
  -- Limpiar carritos abandonados (más de 30 días)
  DELETE FROM carritos 
  WHERE estado = 'abandonado' 
  AND fecha_actualizacion < NOW() - INTERVAL '30 days';
  
  -- Limpiar notificaciones expiradas
  DELETE FROM notificaciones 
  WHERE expira_en < NOW() - INTERVAL '7 days';
  
  -- Limpiar sesiones POS expiradas
  DELETE FROM sesiones_pos 
  WHERE fecha_expiracion < NOW() - INTERVAL '1 day';
  
  -- Marcar carritos como abandonados
  UPDATE carritos 
  SET estado = 'abandonado' 
  WHERE estado = 'activo' 
  AND fecha_actualizacion < NOW() - INTERVAL '7 days';
  
  RAISE NOTICE 'Limpieza de datos antiguos completada';
END;
$$ LANGUAGE plpgsql;

-- Configuraciones adicionales del sistema
INSERT INTO configuracion_sistema (clave, valor, tipo_dato, categoria, descripcion) VALUES
('chat_tiempo_respuesta_max', '30', 'number', 'chat', 'Tiempo máximo de respuesta en minutos'),
('reseñas_moderacion_automatica', 'true', 'boolean', 'moderacion', 'Activar moderación automática de reseñas'),
('notificaciones_email_activas', 'true', 'boolean', 'notificaciones', 'Envío de notificaciones por email'),
('devoluciones_dias_limite', '30', 'number', 'devoluciones', 'Días límite para solicitar devolución'),
('envios_seguimiento_automatico', 'true', 'boolean', 'envios', 'Seguimiento automático con transportistas'),
('facturacion_sunat_activa', 'false', 'boolean', 'facturacion', 'Integración activa con SUNAT'),
('backup_automatico_hora', '02:00', 'string', 'backup', 'Hora para backup automático diario'),
('metricas_actualizacion_minutos', '15', 'number', 'metricas', 'Frecuencia de actualización de métricas'),
('carrito_abandono_dias', '7', 'number', 'carrito', 'Días para marcar carrito como abandonado'),
('stock_alerta_porcentaje', '20', 'number', 'inventario', 'Porcentaje de stock para alerta')
ON CONFLICT (clave) DO NOTHING;

-- Insertar datos de ejemplo para transportistas
INSERT INTO configuracion_sistema (clave, valor, tipo_dato, categoria, descripcion) VALUES
('transportistas_disponibles', '[
  {"codigo": "olva", "nombre": "Olva Courier", "costo_base": 15.00, "tiempo_dias": 3},
  {"codigo": "shalom", "nombre": "Shalom", "costo_base": 12.00, "tiempo_dias": 5},
  {"codigo": "cruz_del_sur", "nombre": "Cruz del Sur Cargo", "costo_base": 18.00, "tiempo_dias": 2}
]', 'json', 'envios', 'Transportistas disponibles para envíos')
ON CONFLICT (clave) DO NOTHING;