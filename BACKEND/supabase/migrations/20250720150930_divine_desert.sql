/*
  # Sistema de Comunicación y Reseñas

  1. Nuevas Tablas
    - `chats` - Conversaciones entre clientes y empleados
    - `mensajes` - Mensajes individuales de cada chat
    - `reseñas` - Calificaciones y comentarios de productos
    - `notificaciones` - Sistema de notificaciones
    - `moderacion_contenido` - Cola de moderación

  2. Funcionalidades
    - Chat en tiempo real con soporte
    - Sistema de reseñas con moderación
    - Notificaciones por email y push
    - Moderación de contenido automática y manual
    - Calificación promedio automática

  3. Características
    - Estados de mensajes (enviado, leído, respondido)
    - Filtros de palabras prohibidas
    - Sistema de reportes de contenido
    - Historial de moderación
*/

-- Tabla de chats/conversaciones
CREATE TABLE IF NOT EXISTS chats (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id uuid REFERENCES usuarios(id) ON DELETE CASCADE,
  empleado_asignado_id uuid REFERENCES usuarios(id),
  producto_id uuid REFERENCES productos(id), -- Chat sobre producto específico
  pedido_id uuid REFERENCES pedidos(id), -- Chat sobre pedido específico
  asunto varchar(200),
  estado varchar(20) DEFAULT 'abierto', -- 'abierto', 'en_progreso', 'cerrado', 'escalado'
  prioridad varchar(20) DEFAULT 'normal', -- 'baja', 'normal', 'alta', 'urgente'
  categoria varchar(50), -- 'consulta_producto', 'problema_pedido', 'devolucion', 'general'
  etiquetas text[], -- Tags para organización
  satisfaccion_cliente integer, -- 1-5 estrellas al cerrar el chat
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
  archivos_adjuntos jsonb DEFAULT '[]', -- URLs de archivos adjuntos
  mensaje_padre_id uuid REFERENCES mensajes(id), -- Para respuestas específicas
  estado varchar(20) DEFAULT 'enviado', -- 'enviado', 'entregado', 'leido'
  editado boolean DEFAULT false,
  fecha_edicion timestamptz,
  moderado boolean DEFAULT false,
  fecha_moderacion timestamptz,
  moderado_por uuid REFERENCES usuarios(id),
  contenido_original text, -- Antes de moderación
  fecha_lectura timestamptz,
  reacciones jsonb DEFAULT '{}', -- Emojis de reacción
  fecha_envio timestamptz DEFAULT now(),
  fecha_creacion timestamptz DEFAULT now()
);

-- Tabla de reseñas de productos
CREATE TABLE IF NOT EXISTS reseñas (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  producto_id uuid REFERENCES productos(id) ON DELETE CASCADE,
  usuario_id uuid REFERENCES usuarios(id) ON DELETE CASCADE,
  pedido_id uuid REFERENCES pedidos(id), -- Verificar compra real
  calificacion integer NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
  titulo varchar(200),
  comentario text,
  ventajas text[],
  desventajas text[],
  recomendaria boolean,
  verificada boolean DEFAULT false, -- Si el usuario realmente compró el producto
  estado_moderacion varchar(20) DEFAULT 'pendiente', -- 'pendiente', 'aprobada', 'rechazada'
  fecha_moderacion timestamptz,
  moderado_por uuid REFERENCES usuarios(id),
  motivo_rechazo text,
  utilidad_positiva integer DEFAULT 0, -- "Me fue útil" positivos
  utilidad_negativa integer DEFAULT 0, -- "No me fue útil"
  reportes integer DEFAULT 0,
  imagenes jsonb DEFAULT '[]', -- URLs de imágenes subidas por el usuario
  variante_comprada_id uuid REFERENCES variantes_producto(id),
  talla_comprada varchar(20),
  color_comprado varchar(50),
  fecha_compra timestamptz,
  fecha_resena timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now(),
  UNIQUE(producto_id, usuario_id) -- Un usuario solo puede reseñar una vez por producto
);

-- Tabla de notificaciones
CREATE TABLE IF NOT EXISTS notificaciones (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id uuid REFERENCES usuarios(id) ON DELETE CASCADE,
  tipo varchar(50) NOT NULL, -- 'pedido_confirmado', 'mensaje_recibido', 'oferta_especial', etc.
  titulo varchar(200) NOT NULL,
  mensaje text NOT NULL,
  datos jsonb, -- Datos adicionales para la notificación
  canal varchar(20) NOT NULL, -- 'email', 'push', 'sms', 'in_app'
  leida boolean DEFAULT false,
  fecha_lectura timestamptz,
  enviada boolean DEFAULT false,
  fecha_envio timestamptz,
  intentos_envio integer DEFAULT 0,
  error_envio text,
  url_accion varchar(500), -- URL a la que redirigir al hacer clic
  prioridad varchar(20) DEFAULT 'normal',
  expira_en timestamptz,
  fecha_creacion timestamptz DEFAULT now()
);

-- Tabla de moderación de contenido
CREATE TABLE IF NOT EXISTS moderacion_contenido (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  tipo_contenido varchar(50) NOT NULL, -- 'resena', 'mensaje_chat', 'comentario'
  contenido_id uuid NOT NULL,
  contenido_original text NOT NULL,
  usuario_autor_id uuid REFERENCES usuarios(id),
  estado varchar(20) DEFAULT 'pendiente', -- 'pendiente', 'aprobado', 'rechazado', 'editado'
  puntuacion_automatica decimal(3,2), -- Score de IA/filtros automáticos (0-1)
  palabras_prohibidas text[], -- Palabras detectadas
  requiere_revision boolean DEFAULT false,
  moderado_por uuid REFERENCES usuarios(id),
  fecha_moderacion timestamptz,
  observaciones_moderador text,
  accion_tomada varchar(50), -- 'aprobado', 'rechazado', 'editado', 'bloqueado'
  fecha_creacion timestamptz DEFAULT now()
);

-- Habilitar RLS
ALTER TABLE chats ENABLE ROW LEVEL SECURITY;
ALTER TABLE mensajes ENABLE ROW LEVEL SECURITY;
ALTER TABLE reseñas ENABLE ROW LEVEL SECURITY;
ALTER TABLE notificaciones ENABLE ROW LEVEL SECURITY;
ALTER TABLE moderacion_contenido ENABLE ROW LEVEL SECURITY;

-- Políticas de seguridad
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

-- Índices de optimización
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
CREATE INDEX IF NOT EXISTS idx_moderacion_tipo ON moderacion_contenido(tipo_contenido);
CREATE INDEX IF NOT EXISTS idx_moderacion_estado ON moderacion_contenido(estado);

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
  palabras_prohibidas text[] := ARRAY['spam', 'estafa', 'fake', 'horrible', 'pésimo'];
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
    
    INSERT INTO moderacion_contenido (
      tipo_contenido, contenido_id, contenido_original, usuario_autor_id,
      puntuacion_automatica, palabras_prohibidas, requiere_revision
    ) VALUES (
      'resena', NEW.id, COALESCE(NEW.titulo, '') || ' ' || COALESCE(NEW.comentario, ''), NEW.usuario_id,
      (resultado_moderacion->>'puntuacion')::decimal, 
      ARRAY(SELECT jsonb_array_elements_text(resultado_moderacion->'palabras_encontradas')),
      true
    );
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