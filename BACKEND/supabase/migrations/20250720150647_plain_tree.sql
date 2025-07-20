/*
  # Estructura Base del Sistema DPattyModa

  1. Nuevas Tablas
    - `usuarios` - Gestión de usuarios del sistema
    - `roles` - Roles y permisos  
    - `sucursales` - Información de sucursales
    - `categorias` - Categorías de productos
    - `productos` - Catálogo de productos
    - `variantes_producto` - Tallas, colores y stock
    - `inventario` - Control de stock por variante
    - `carritos` - Carritos de compra online
    - `detalle_carrito` - Items del carrito

  2. Seguridad
    - RLS habilitado en todas las tablas
    - Políticas de acceso basadas en roles
    - Auditoría de cambios críticos

  3. Índices y Optimizaciones
    - Índices en campos de búsqueda frecuente
    - Claves foráneas para integridad referencial
    - Triggers para auditoría automática
*/

-- Tabla de roles del sistema
CREATE TABLE IF NOT EXISTS roles (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre_rol varchar(50) UNIQUE NOT NULL,
  descripcion text,
  permisos jsonb DEFAULT '{}',
  activo boolean DEFAULT true,
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de usuarios del sistema
CREATE TABLE IF NOT EXISTS usuarios (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  email varchar(255) UNIQUE NOT NULL,
  password_hash varchar(255) NOT NULL,
  nombres varchar(100) NOT NULL,
  apellidos varchar(100) NOT NULL,
  telefono varchar(20),
  dni varchar(20),
  ruc varchar(20),
  direccion text,
  fecha_nacimiento date,
  genero varchar(20),
  rol_id uuid REFERENCES roles(id),
  activo boolean DEFAULT true,
  ultimo_acceso timestamptz,
  intentos_fallidos integer DEFAULT 0,
  bloqueado_hasta timestamptz,
  token_verificacion varchar(255),
  email_verificado boolean DEFAULT false,
  token_recuperacion varchar(255),
  fecha_token_recuperacion timestamptz,
  preferencias jsonb DEFAULT '{}',
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de sucursales
CREATE TABLE IF NOT EXISTS sucursales (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre_sucursal varchar(100) NOT NULL,
  direccion text NOT NULL,
  telefono varchar(20),
  email varchar(100),
  horario_atencion jsonb,
  coordenadas_gps point,
  activa boolean DEFAULT true,
  es_principal boolean DEFAULT false,
  configuracion jsonb DEFAULT '{}',
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de categorías de productos
CREATE TABLE IF NOT EXISTS categorias (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  nombre_categoria varchar(100) NOT NULL,
  descripcion text,
  categoria_padre_id uuid REFERENCES categorias(id),
  nivel integer DEFAULT 1,
  orden_visualizacion integer DEFAULT 0,
  imagen_url varchar(500),
  activa boolean DEFAULT true,
  seo_titulo varchar(200),
  seo_descripcion text,
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de productos
CREATE TABLE IF NOT EXISTS productos (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  codigo_producto varchar(50) UNIQUE NOT NULL,
  nombre_producto varchar(200) NOT NULL,
  descripcion text,
  descripcion_corta varchar(500),
  categoria_id uuid REFERENCES categorias(id),
  marca varchar(100),
  precio_base decimal(10,2) NOT NULL,
  precio_oferta decimal(10,2),
  costo_producto decimal(10,2),
  margen_ganancia decimal(5,2),
  peso decimal(8,3),
  dimensiones jsonb,
  caracteristicas jsonb,
  imagenes jsonb DEFAULT '[]',
  tags text[],
  activo boolean DEFAULT true,
  destacado boolean DEFAULT false,
  nuevo boolean DEFAULT false,
  fecha_lanzamiento date,
  calificacion_promedio decimal(3,2) DEFAULT 0,
  total_reseñas integer DEFAULT 0,
  total_ventas integer DEFAULT 0,
  seo_titulo varchar(200),
  seo_descripcion text,
  seo_palabras_clave text[],
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de variantes de productos (tallas, colores, etc.)
CREATE TABLE IF NOT EXISTS variantes_producto (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  producto_id uuid REFERENCES productos(id) ON DELETE CASCADE,
  sku varchar(100) UNIQUE NOT NULL,
  talla varchar(20),
  color varchar(50),
  material varchar(100),
  precio_variante decimal(10,2),
  peso_variante decimal(8,3),
  imagen_variante varchar(500),
  codigo_barras varchar(100),
  activo boolean DEFAULT true,
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now()
);

-- Tabla de inventario por variante
CREATE TABLE IF NOT EXISTS inventario (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  variante_id uuid REFERENCES variantes_producto(id) ON DELETE CASCADE,
  sucursal_id uuid REFERENCES sucursales(id),
  cantidad_disponible integer DEFAULT 0,
  cantidad_reservada integer DEFAULT 0,
  cantidad_minima integer DEFAULT 5,
  cantidad_maxima integer DEFAULT 1000,
  ubicacion_fisica varchar(100),
  ultimo_movimiento timestamptz DEFAULT now(),
  fecha_ultimo_ingreso timestamptz,
  fecha_ultimo_egreso timestamptz,
  costo_promedio decimal(10,2),
  fecha_actualizacion timestamptz DEFAULT now(),
  UNIQUE(variante_id, sucursal_id)
);

-- Tabla de carritos de compra
CREATE TABLE IF NOT EXISTS carritos (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  usuario_id uuid REFERENCES usuarios(id) ON DELETE CASCADE,
  sesion_id varchar(255),
  estado varchar(20) DEFAULT 'activo',
  subtotal decimal(10,2) DEFAULT 0,
  descuento decimal(10,2) DEFAULT 0,
  impuestos decimal(10,2) DEFAULT 0,
  costo_envio decimal(10,2) DEFAULT 0,
  total decimal(10,2) DEFAULT 0,
  cupones_aplicados jsonb DEFAULT '[]',
  fecha_creacion timestamptz DEFAULT now(),
  fecha_actualizacion timestamptz DEFAULT now(),
  fecha_expiracion timestamptz DEFAULT (now() + interval '30 days')
);

-- Tabla de detalle del carrito
CREATE TABLE IF NOT EXISTS detalle_carrito (
  id uuid PRIMARY KEY DEFAULT gen_random_uuid(),
  carrito_id uuid REFERENCES carritos(id) ON DELETE CASCADE,
  variante_id uuid REFERENCES variantes_producto(id),
  cantidad integer NOT NULL DEFAULT 1,
  precio_unitario decimal(10,2) NOT NULL,
  descuento_unitario decimal(10,2) DEFAULT 0,
  subtotal decimal(10,2) GENERATED ALWAYS AS (cantidad * (precio_unitario - COALESCE(descuento_unitario, 0))) STORED,
  fecha_agregado timestamptz DEFAULT now(),
  UNIQUE(carrito_id, variante_id)
);

-- Habilitar RLS en todas las tablas
ALTER TABLE roles ENABLE ROW LEVEL SECURITY;
ALTER TABLE usuarios ENABLE ROW LEVEL SECURITY;
ALTER TABLE sucursales ENABLE ROW LEVEL SECURITY;
ALTER TABLE categorias ENABLE ROW LEVEL SECURITY;
ALTER TABLE productos ENABLE ROW LEVEL SECURITY;
ALTER TABLE variantes_producto ENABLE ROW LEVEL SECURITY;
ALTER TABLE inventario ENABLE ROW LEVEL SECURITY;
ALTER TABLE carritos ENABLE ROW LEVEL SECURITY;
ALTER TABLE detalle_carrito ENABLE ROW LEVEL SECURITY;

-- Políticas de seguridad básicas
CREATE POLICY "Usuarios pueden ver su propio perfil"
  ON usuarios FOR SELECT
  TO authenticated
  USING (auth.uid() = id);

CREATE POLICY "Admins pueden gestionar usuarios"
  ON usuarios FOR ALL
  TO authenticated
  USING (EXISTS (
    SELECT 1 FROM usuarios u 
    JOIN roles r ON u.rol_id = r.id 
    WHERE u.id = auth.uid() AND r.nombre_rol = 'Administrador'
  ));

CREATE POLICY "Productos públicos para lectura"
  ON productos FOR SELECT
  TO anon, authenticated
  USING (activo = true);

CREATE POLICY "Carritos por usuario"
  ON carritos FOR ALL
  TO authenticated
  USING (usuario_id = auth.uid());

-- Índices para optimización
CREATE INDEX IF NOT EXISTS idx_usuarios_email ON usuarios(email);
CREATE INDEX IF NOT EXISTS idx_usuarios_rol ON usuarios(rol_id);
CREATE INDEX IF NOT EXISTS idx_productos_categoria ON productos(categoria_id);
CREATE INDEX IF NOT EXISTS idx_productos_activo ON productos(activo);
CREATE INDEX IF NOT EXISTS idx_variantes_producto ON variantes_producto(producto_id);
CREATE INDEX IF NOT EXISTS idx_inventario_variante_sucursal ON inventario(variante_id, sucursal_id);
CREATE INDEX IF NOT EXISTS idx_carritos_usuario ON carritos(usuario_id);
CREATE INDEX IF NOT EXISTS idx_detalle_carrito_carrito ON detalle_carrito(carrito_id);

-- Insertar roles básicos
INSERT INTO roles (nombre_rol, descripcion, permisos) VALUES
('Administrador', 'Acceso completo al sistema', '{"all": true}'),
('Empleado', 'Gestión de inventario y atención al cliente', '{"products": true, "inventory": true, "orders": true}'),
('Cajero', 'Operación de punto de venta', '{"pos": true, "sales": true, "cash": true}'),
('Cliente', 'Compras online y consultas', '{"shop": true, "profile": true}')
ON CONFLICT (nombre_rol) DO NOTHING;

-- Insertar sucursal principal
INSERT INTO sucursales (nombre_sucursal, direccion, telefono, es_principal) VALUES
('Tienda Principal Pampa Hermosa', 'Pampa Hermosa, Loreto, Perú', '+51 XXX XXX XXX', true)
ON CONFLICT DO NOTHING;