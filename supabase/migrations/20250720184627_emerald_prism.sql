-- =====================================================
-- SCRIPT COMPLETO MYSQL PARA DPATTY MODA
-- Sistema de Tienda de Ropa con POS y E-commerce
-- Versión: 1.0.0
-- Base de datos: MySQL 8.0+
-- =====================================================

-- Crear base de datos
CREATE DATABASE IF NOT EXISTS dpatty_moda 
CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE dpatty_moda;

-- =====================================================
-- CONFIGURACIÓN INICIAL
-- =====================================================

-- Configurar zona horaria
SET time_zone = '-05:00'; -- Hora de Perú

-- Habilitar eventos programados
SET GLOBAL event_scheduler = ON;

-- =====================================================
-- TABLA DE ROLES
-- =====================================================

CREATE TABLE roles (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    nombre_rol VARCHAR(50) UNIQUE NOT NULL,
    descripcion TEXT,
    permisos JSON DEFAULT ('{}'),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_roles_nombre (nombre_rol),
    INDEX idx_roles_activo (activo)
);

-- =====================================================
-- TABLA DE USUARIOS
-- =====================================================

CREATE TABLE usuarios (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    email VARCHAR(255) UNIQUE NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    nombres VARCHAR(100) NOT NULL,
    apellidos VARCHAR(100) NOT NULL,
    telefono VARCHAR(20),
    dni VARCHAR(20),
    ruc VARCHAR(20),
    direccion TEXT,
    fecha_nacimiento DATE,
    genero VARCHAR(20),
    rol_id CHAR(36),
    activo BOOLEAN DEFAULT TRUE,
    ultimo_acceso TIMESTAMP NULL,
    intentos_fallidos INT DEFAULT 0,
    bloqueado_hasta TIMESTAMP NULL,
    token_verificacion VARCHAR(255),
    email_verificado BOOLEAN DEFAULT FALSE,
    token_recuperacion VARCHAR(255),
    fecha_token_recuperacion TIMESTAMP NULL,
    preferencias JSON DEFAULT ('{}'),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (rol_id) REFERENCES roles(id),
    INDEX idx_usuarios_email (email),
    INDEX idx_usuarios_rol (rol_id),
    INDEX idx_usuarios_dni (dni),
    INDEX idx_usuarios_activo (activo),
    INDEX idx_usuarios_fecha_creacion (fecha_creacion)
);

-- =====================================================
-- TABLA DE SUCURSALES
-- =====================================================

CREATE TABLE sucursales (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    nombre_sucursal VARCHAR(100) NOT NULL,
    direccion TEXT NOT NULL,
    telefono VARCHAR(20),
    email VARCHAR(100),
    horario_atencion JSON,
    coordenadas_gps POINT,
    activa BOOLEAN DEFAULT TRUE,
    es_principal BOOLEAN DEFAULT FALSE,
    configuracion JSON DEFAULT ('{}'),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_sucursales_activa (activa),
    INDEX idx_sucursales_principal (es_principal)
);

-- =====================================================
-- TABLA DE CATEGORÍAS
-- =====================================================

CREATE TABLE categorias (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    nombre_categoria VARCHAR(100) NOT NULL,
    descripcion TEXT,
    categoria_padre_id CHAR(36),
    nivel INT DEFAULT 1,
    orden_visualizacion INT DEFAULT 0,
    imagen_url VARCHAR(500),
    activa BOOLEAN DEFAULT TRUE,
    seo_titulo VARCHAR(200),
    seo_descripcion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (categoria_padre_id) REFERENCES categorias(id),
    INDEX idx_categorias_padre (categoria_padre_id),
    INDEX idx_categorias_activa (activa),
    INDEX idx_categorias_orden (orden_visualizacion)
);

-- =====================================================
-- TABLA DE PRODUCTOS
-- =====================================================

CREATE TABLE productos (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    codigo_producto VARCHAR(50) UNIQUE NOT NULL,
    nombre_producto VARCHAR(200) NOT NULL,
    descripcion TEXT,
    descripcion_corta VARCHAR(500),
    categoria_id CHAR(36),
    marca VARCHAR(100),
    precio_base DECIMAL(10,2) NOT NULL,
    precio_oferta DECIMAL(10,2),
    costo_producto DECIMAL(10,2),
    margen_ganancia DECIMAL(5,2),
    peso DECIMAL(8,3),
    dimensiones JSON,
    caracteristicas JSON,
    imagenes JSON DEFAULT ('[]'),
    tags JSON,
    activo BOOLEAN DEFAULT TRUE,
    destacado BOOLEAN DEFAULT FALSE,
    nuevo BOOLEAN DEFAULT FALSE,
    fecha_lanzamiento DATE,
    calificacion_promedio DECIMAL(3,2) DEFAULT 0,
    total_reseñas INT DEFAULT 0,
    total_ventas INT DEFAULT 0,
    seo_titulo VARCHAR(200),
    seo_descripcion TEXT,
    seo_palabras_clave JSON,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (categoria_id) REFERENCES categorias(id),
    INDEX idx_productos_categoria (categoria_id),
    INDEX idx_productos_activo (activo),
    INDEX idx_productos_destacado (destacado),
    INDEX idx_productos_nuevo (nuevo),
    INDEX idx_productos_marca (marca),
    INDEX idx_productos_precio (precio_base),
    FULLTEXT idx_productos_busqueda (nombre_producto, descripcion, marca)
);

-- =====================================================
-- TABLA DE VARIANTES DE PRODUCTOS
-- =====================================================

CREATE TABLE variantes_producto (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    producto_id CHAR(36) NOT NULL,
    sku VARCHAR(100) UNIQUE NOT NULL,
    talla VARCHAR(20),
    color VARCHAR(50),
    material VARCHAR(100),
    precio_variante DECIMAL(10,2),
    peso_variante DECIMAL(8,3),
    imagen_variante VARCHAR(500),
    codigo_barras VARCHAR(100),
    activo BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    INDEX idx_variantes_producto (producto_id),
    INDEX idx_variantes_sku (sku),
    INDEX idx_variantes_activo (activo)
);

-- =====================================================
-- TABLA DE INVENTARIO
-- =====================================================

CREATE TABLE inventario (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    variante_id CHAR(36) NOT NULL,
    sucursal_id CHAR(36) NOT NULL,
    cantidad_disponible INT DEFAULT 0,
    cantidad_reservada INT DEFAULT 0,
    cantidad_minima INT DEFAULT 5,
    cantidad_maxima INT DEFAULT 1000,
    ubicacion_fisica VARCHAR(100),
    ultimo_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_ingreso TIMESTAMP NULL,
    fecha_ultimo_egreso TIMESTAMP NULL,
    costo_promedio DECIMAL(10,2),
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (variante_id) REFERENCES variantes_producto(id) ON DELETE CASCADE,
    FOREIGN KEY (sucursal_id) REFERENCES sucursales(id),
    UNIQUE KEY uk_inventario_variante_sucursal (variante_id, sucursal_id),
    INDEX idx_inventario_variante (variante_id),
    INDEX idx_inventario_sucursal (sucursal_id),
    INDEX idx_inventario_stock_bajo (cantidad_disponible, cantidad_minima)
);

-- =====================================================
-- TABLA DE DIRECCIONES DE ENVÍO
-- =====================================================

CREATE TABLE direcciones_envio (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    usuario_id CHAR(36) NOT NULL,
    alias_direccion VARCHAR(100),
    nombres_destinatario VARCHAR(100) NOT NULL,
    apellidos_destinatario VARCHAR(100) NOT NULL,
    telefono_destinatario VARCHAR(20),
    direccion_linea1 TEXT NOT NULL,
    direccion_linea2 TEXT,
    ciudad VARCHAR(100) NOT NULL,
    departamento VARCHAR(100) NOT NULL,
    codigo_postal VARCHAR(20),
    pais VARCHAR(100) DEFAULT 'Perú',
    referencia TEXT,
    coordenadas_gps POINT,
    es_principal BOOLEAN DEFAULT FALSE,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_direcciones_usuario (usuario_id),
    INDEX idx_direcciones_principal (es_principal)
);

-- =====================================================
-- TABLA DE CARRITOS
-- =====================================================

CREATE TABLE carritos (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    usuario_id CHAR(36),
    sesion_id VARCHAR(255),
    estado VARCHAR(20) DEFAULT 'activo',
    subtotal DECIMAL(10,2) DEFAULT 0,
    descuento DECIMAL(10,2) DEFAULT 0,
    impuestos DECIMAL(10,2) DEFAULT 0,
    costo_envio DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) DEFAULT 0,
    cupones_aplicados JSON DEFAULT ('[]'),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    fecha_expiracion TIMESTAMP DEFAULT (DATE_ADD(CURRENT_TIMESTAMP, INTERVAL 30 DAY)),
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_carritos_usuario (usuario_id),
    INDEX idx_carritos_sesion (sesion_id),
    INDEX idx_carritos_estado (estado),
    INDEX idx_carritos_expiracion (fecha_expiracion)
);

-- =====================================================
-- TABLA DE DETALLE DEL CARRITO
-- =====================================================

CREATE TABLE detalle_carrito (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    carrito_id CHAR(36) NOT NULL,
    variante_id CHAR(36) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    descuento_unitario DECIMAL(10,2) DEFAULT 0,
    subtotal DECIMAL(10,2) GENERATED ALWAYS AS (cantidad * (precio_unitario - IFNULL(descuento_unitario, 0))) STORED,
    fecha_agregado TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (carrito_id) REFERENCES carritos(id) ON DELETE CASCADE,
    FOREIGN KEY (variante_id) REFERENCES variantes_producto(id),
    UNIQUE KEY uk_detalle_carrito_variante (carrito_id, variante_id),
    INDEX idx_detalle_carrito_carrito (carrito_id),
    INDEX idx_detalle_carrito_variante (variante_id)
);

-- =====================================================
-- TABLA DE PEDIDOS
-- =====================================================

CREATE TABLE pedidos (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    numero_pedido VARCHAR(50) UNIQUE NOT NULL,
    usuario_id CHAR(36),
    sucursal_id CHAR(36),
    direccion_envio_id CHAR(36),
    tipo_venta VARCHAR(20) NOT NULL DEFAULT 'online',
    estado VARCHAR(30) NOT NULL DEFAULT 'pendiente',
    subtotal DECIMAL(10,2) NOT NULL DEFAULT 0,
    descuento_total DECIMAL(10,2) DEFAULT 0,
    impuestos_total DECIMAL(10,2) DEFAULT 0,
    costo_envio DECIMAL(10,2) DEFAULT 0,
    total DECIMAL(10,2) NOT NULL DEFAULT 0,
    moneda VARCHAR(10) DEFAULT 'PEN',
    metodo_pago VARCHAR(50),
    estado_pago VARCHAR(30) DEFAULT 'pendiente',
    notas_cliente TEXT,
    notas_internas TEXT,
    cupones_aplicados JSON DEFAULT ('[]'),
    datos_cliente JSON,
    fecha_estimada_entrega TIMESTAMP NULL,
    fecha_entrega_real TIMESTAMP NULL,
    vendedor_id CHAR(36),
    caja_id CHAR(36),
    comprobante_requerido BOOLEAN DEFAULT FALSE,
    tipo_comprobante VARCHAR(20),
    datos_facturacion JSON,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (sucursal_id) REFERENCES sucursales(id),
    FOREIGN KEY (direccion_envio_id) REFERENCES direcciones_envio(id),
    FOREIGN KEY (vendedor_id) REFERENCES usuarios(id),
    INDEX idx_pedidos_usuario (usuario_id),
    INDEX idx_pedidos_numero (numero_pedido),
    INDEX idx_pedidos_estado (estado),
    INDEX idx_pedidos_fecha (fecha_creacion),
    INDEX idx_pedidos_tipo_venta (tipo_venta),
    INDEX idx_pedidos_vendedor (vendedor_id)
);

-- =====================================================
-- TABLA DE DETALLE DE PEDIDOS
-- =====================================================

CREATE TABLE detalle_pedidos (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    pedido_id CHAR(36) NOT NULL,
    variante_id CHAR(36) NOT NULL,
    cantidad INT NOT NULL DEFAULT 1,
    precio_unitario DECIMAL(10,2) NOT NULL,
    descuento_unitario DECIMAL(10,2) DEFAULT 0,
    subtotal DECIMAL(10,2) GENERATED ALWAYS AS (cantidad * (precio_unitario - IFNULL(descuento_unitario, 0))) STORED,
    datos_producto JSON,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (variante_id) REFERENCES variantes_producto(id),
    INDEX idx_detalle_pedidos_pedido (pedido_id),
    INDEX idx_detalle_pedidos_variante (variante_id)
);

-- =====================================================
-- TABLA DE PAGOS
-- =====================================================

CREATE TABLE pagos (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    pedido_id CHAR(36) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    moneda VARCHAR(10) DEFAULT 'PEN',
    estado VARCHAR(30) NOT NULL DEFAULT 'pendiente',
    referencia_externa VARCHAR(255),
    datos_transaccion JSON,
    fecha_procesamiento TIMESTAMP NULL,
    fecha_vencimiento TIMESTAMP NULL,
    intentos_procesamiento INT DEFAULT 0,
    comision DECIMAL(10,2) DEFAULT 0,
    monto_neto DECIMAL(10,2),
    notas TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    INDEX idx_pagos_pedido (pedido_id),
    INDEX idx_pagos_estado (estado),
    INDEX idx_pagos_referencia (referencia_externa),
    INDEX idx_pagos_metodo (metodo_pago)
);

-- =====================================================
-- TABLA DE CAJAS REGISTRADORAS
-- =====================================================

CREATE TABLE cajas (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    sucursal_id CHAR(36) NOT NULL,
    numero_caja VARCHAR(20) NOT NULL,
    nombre_caja VARCHAR(100) NOT NULL,
    terminal_pos VARCHAR(100),
    ip_address VARCHAR(45),
    activa BOOLEAN DEFAULT TRUE,
    configuracion JSON DEFAULT ('{}'),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (sucursal_id) REFERENCES sucursales(id) ON DELETE CASCADE,
    UNIQUE KEY uk_cajas_numero_sucursal (sucursal_id, numero_caja),
    INDEX idx_cajas_sucursal (sucursal_id),
    INDEX idx_cajas_activa (activa)
);

-- =====================================================
-- TABLA DE TURNOS DE CAJA
-- =====================================================

CREATE TABLE turnos_caja (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    caja_id CHAR(36) NOT NULL,
    cajero_id CHAR(36) NOT NULL,
    fecha_apertura TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP NULL,
    monto_inicial DECIMAL(10,2) NOT NULL DEFAULT 0,
    monto_final DECIMAL(10,2),
    monto_esperado DECIMAL(10,2),
    diferencia DECIMAL(10,2),
    total_ventas_efectivo DECIMAL(10,2) DEFAULT 0,
    total_ventas_tarjeta DECIMAL(10,2) DEFAULT 0,
    total_ventas_digital DECIMAL(10,2) DEFAULT 0,
    total_egresos DECIMAL(10,2) DEFAULT 0,
    numero_transacciones INT DEFAULT 0,
    estado VARCHAR(20) DEFAULT 'abierto',
    observaciones TEXT,
    supervisor_id CHAR(36),
    fecha_supervision TIMESTAMP NULL,
    arqueo_detalle JSON,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (caja_id) REFERENCES cajas(id) ON DELETE CASCADE,
    FOREIGN KEY (cajero_id) REFERENCES usuarios(id),
    FOREIGN KEY (supervisor_id) REFERENCES usuarios(id),
    INDEX idx_turnos_caja (caja_id),
    INDEX idx_turnos_cajero (cajero_id),
    INDEX idx_turnos_estado (estado),
    INDEX idx_turnos_fecha (fecha_apertura)
);

-- =====================================================
-- TABLA DE MOVIMIENTOS DE CAJA
-- =====================================================

CREATE TABLE movimientos_caja (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    turno_caja_id CHAR(36) NOT NULL,
    pedido_id CHAR(36),
    tipo_movimiento VARCHAR(30) NOT NULL,
    concepto VARCHAR(200) NOT NULL,
    monto DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50),
    referencia VARCHAR(100),
    autorizado_por CHAR(36),
    comprobante_url VARCHAR(500),
    observaciones TEXT,
    fecha_movimiento TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (turno_caja_id) REFERENCES turnos_caja(id) ON DELETE CASCADE,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (autorizado_por) REFERENCES usuarios(id),
    INDEX idx_movimientos_turno (turno_caja_id),
    INDEX idx_movimientos_tipo (tipo_movimiento),
    INDEX idx_movimientos_fecha (fecha_movimiento)
);

-- =====================================================
-- TABLA DE CUPONES
-- =====================================================

CREATE TABLE cupones (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    codigo_cupon VARCHAR(50) UNIQUE NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_descuento VARCHAR(20) NOT NULL,
    valor_descuento DECIMAL(10,2) NOT NULL,
    monto_minimo_compra DECIMAL(10,2),
    monto_maximo_descuento DECIMAL(10,2),
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    usos_maximos INT,
    usos_por_usuario INT DEFAULT 1,
    usos_actuales INT DEFAULT 0,
    solo_primera_compra BOOLEAN DEFAULT FALSE,
    aplicable_envio BOOLEAN DEFAULT FALSE,
    categorias_incluidas JSON,
    productos_incluidos JSON,
    usuarios_incluidos JSON,
    activo BOOLEAN DEFAULT TRUE,
    codigo_promocional VARCHAR(100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_cupones_codigo (codigo_cupon),
    INDEX idx_cupones_activo (activo),
    INDEX idx_cupones_fecha (fecha_inicio, fecha_fin)
);

-- =====================================================
-- TABLA DE PROMOCIONES
-- =====================================================

CREATE TABLE promociones (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    nombre_promocion VARCHAR(100) NOT NULL,
    descripcion TEXT,
    tipo_promocion VARCHAR(30) NOT NULL,
    condiciones JSON NOT NULL,
    descuento JSON NOT NULL,
    prioridad INT DEFAULT 1,
    fecha_inicio TIMESTAMP NOT NULL,
    fecha_fin TIMESTAMP NOT NULL,
    dias_semana JSON DEFAULT ('[1,2,3,4,5,6,7]'),
    horas_inicio TIME,
    horas_fin TIME,
    sucursales_incluidas JSON,
    categorias_incluidas JSON,
    productos_incluidos JSON,
    aplicable_online BOOLEAN DEFAULT TRUE,
    aplicable_presencial BOOLEAN DEFAULT TRUE,
    limite_usos INT,
    usos_actuales INT DEFAULT 0,
    activa BOOLEAN DEFAULT TRUE,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    INDEX idx_promociones_activa (activa),
    INDEX idx_promociones_fecha (fecha_inicio, fecha_fin),
    INDEX idx_promociones_prioridad (prioridad)
);

-- =====================================================
-- TABLA DE USO DE CUPONES
-- =====================================================

CREATE TABLE uso_cupones (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    cupon_id CHAR(36) NOT NULL,
    usuario_id CHAR(36),
    pedido_id CHAR(36),
    monto_descuento DECIMAL(10,2) NOT NULL,
    monto_original DECIMAL(10,2),
    ip_address VARCHAR(45),
    user_agent TEXT,
    fecha_uso TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (cupon_id) REFERENCES cupones(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    INDEX idx_uso_cupones_cupon (cupon_id),
    INDEX idx_uso_cupones_usuario (usuario_id),
    INDEX idx_uso_cupones_fecha (fecha_uso)
);

-- =====================================================
-- TABLA DE CHATS
-- =====================================================

CREATE TABLE chats (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    usuario_id CHAR(36) NOT NULL,
    empleado_asignado_id CHAR(36),
    producto_id CHAR(36),
    pedido_id CHAR(36),
    asunto VARCHAR(200),
    estado VARCHAR(20) DEFAULT 'abierto',
    prioridad VARCHAR(20) DEFAULT 'normal',
    categoria VARCHAR(50),
    etiquetas JSON,
    satisfaccion_cliente INT CHECK (satisfaccion_cliente >= 1 AND satisfaccion_cliente <= 5),
    comentario_satisfaccion TEXT,
    fecha_primer_mensaje TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_ultimo_mensaje TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_cierre TIMESTAMP NULL,
    tiempo_primera_respuesta TIME,
    tiempo_resolucion TIME,
    cerrado_por CHAR(36),
    motivo_cierre VARCHAR(100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (empleado_asignado_id) REFERENCES usuarios(id),
    FOREIGN KEY (producto_id) REFERENCES productos(id),
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (cerrado_por) REFERENCES usuarios(id),
    INDEX idx_chats_usuario (usuario_id),
    INDEX idx_chats_empleado (empleado_asignado_id),
    INDEX idx_chats_estado (estado),
    INDEX idx_chats_fecha (fecha_creacion)
);

-- =====================================================
-- TABLA DE MENSAJES
-- =====================================================

CREATE TABLE mensajes (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    chat_id CHAR(36) NOT NULL,
    remitente_id CHAR(36) NOT NULL,
    tipo_remitente VARCHAR(20) NOT NULL,
    contenido TEXT NOT NULL,
    tipo_mensaje VARCHAR(20) DEFAULT 'texto',
    archivos_adjuntos JSON DEFAULT ('[]'),
    mensaje_padre_id CHAR(36),
    estado VARCHAR(20) DEFAULT 'enviado',
    editado BOOLEAN DEFAULT FALSE,
    fecha_edicion TIMESTAMP NULL,
    moderado BOOLEAN DEFAULT FALSE,
    fecha_moderacion TIMESTAMP NULL,
    moderado_por CHAR(36),
    contenido_original TEXT,
    fecha_lectura TIMESTAMP NULL,
    reacciones JSON DEFAULT ('{}'),
    fecha_envio TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (chat_id) REFERENCES chats(id) ON DELETE CASCADE,
    FOREIGN KEY (remitente_id) REFERENCES usuarios(id),
    FOREIGN KEY (mensaje_padre_id) REFERENCES mensajes(id),
    FOREIGN KEY (moderado_por) REFERENCES usuarios(id),
    INDEX idx_mensajes_chat (chat_id),
    INDEX idx_mensajes_remitente (remitente_id),
    INDEX idx_mensajes_fecha (fecha_envio)
);

-- =====================================================
-- TABLA DE RESEÑAS
-- =====================================================

CREATE TABLE reseñas (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    producto_id CHAR(36) NOT NULL,
    usuario_id CHAR(36) NOT NULL,
    pedido_id CHAR(36),
    calificacion INT NOT NULL CHECK (calificacion >= 1 AND calificacion <= 5),
    titulo VARCHAR(200),
    comentario TEXT,
    ventajas JSON,
    desventajas JSON,
    recomendaria BOOLEAN,
    verificada BOOLEAN DEFAULT FALSE,
    estado_moderacion VARCHAR(20) DEFAULT 'pendiente',
    fecha_moderacion TIMESTAMP NULL,
    moderado_por CHAR(36),
    motivo_rechazo TEXT,
    utilidad_positiva INT DEFAULT 0,
    utilidad_negativa INT DEFAULT 0,
    reportes INT DEFAULT 0,
    imagenes JSON DEFAULT ('[]'),
    variante_comprada_id CHAR(36),
    talla_comprada VARCHAR(20),
    color_comprado VARCHAR(50),
    fecha_compra TIMESTAMP NULL,
    fecha_resena TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (producto_id) REFERENCES productos(id) ON DELETE CASCADE,
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id),
    FOREIGN KEY (moderado_por) REFERENCES usuarios(id),
    FOREIGN KEY (variante_comprada_id) REFERENCES variantes_producto(id),
    UNIQUE KEY uk_reseñas_producto_usuario (producto_id, usuario_id),
    INDEX idx_reseñas_producto (producto_id),
    INDEX idx_reseñas_usuario (usuario_id),
    INDEX idx_reseñas_estado (estado_moderacion),
    INDEX idx_reseñas_calificacion (calificacion)
);

-- =====================================================
-- TABLA DE NOTIFICACIONES
-- =====================================================

CREATE TABLE notificaciones (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    usuario_id CHAR(36) NOT NULL,
    tipo VARCHAR(50) NOT NULL,
    titulo VARCHAR(200) NOT NULL,
    mensaje TEXT NOT NULL,
    datos JSON,
    canal VARCHAR(20) NOT NULL,
    leida BOOLEAN DEFAULT FALSE,
    fecha_lectura TIMESTAMP NULL,
    enviada BOOLEAN DEFAULT FALSE,
    fecha_envio TIMESTAMP NULL,
    intentos_envio INT DEFAULT 0,
    error_envio TEXT,
    url_accion VARCHAR(500),
    prioridad VARCHAR(20) DEFAULT 'normal',
    expira_en TIMESTAMP NULL,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id) ON DELETE CASCADE,
    INDEX idx_notificaciones_usuario (usuario_id),
    INDEX idx_notificaciones_leida (leida),
    INDEX idx_notificaciones_tipo (tipo),
    INDEX idx_notificaciones_fecha (fecha_creacion)
);

-- =====================================================
-- TABLA DE DEVOLUCIONES
-- =====================================================

CREATE TABLE devoluciones (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    pedido_id CHAR(36) NOT NULL,
    numero_devolucion VARCHAR(50) UNIQUE NOT NULL,
    tipo VARCHAR(20) NOT NULL,
    motivo VARCHAR(100) NOT NULL,
    descripcion_detallada TEXT,
    estado VARCHAR(30) DEFAULT 'solicitada',
    items_devolucion JSON NOT NULL,
    monto_devolucion DECIMAL(10,2),
    metodo_reembolso VARCHAR(50),
    fecha_solicitud TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_aprobacion TIMESTAMP NULL,
    fecha_recepcion_items TIMESTAMP NULL,
    fecha_completada TIMESTAMP NULL,
    evidencia_fotos JSON,
    aprobado_por CHAR(36),
    procesado_por CHAR(36),
    notas_cliente TEXT,
    notas_internas TEXT,
    costo_envio_devolucion DECIMAL(10,2),
    numero_guia_devolucion VARCHAR(100),
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    FOREIGN KEY (aprobado_por) REFERENCES usuarios(id),
    FOREIGN KEY (procesado_por) REFERENCES usuarios(id),
    INDEX idx_devoluciones_pedido (pedido_id),
    INDEX idx_devoluciones_numero (numero_devolucion),
    INDEX idx_devoluciones_estado (estado),
    INDEX idx_devoluciones_fecha (fecha_solicitud)
);

-- =====================================================
-- TABLA DE ENVÍOS
-- =====================================================

CREATE TABLE envios (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    pedido_id CHAR(36) NOT NULL,
    transportista VARCHAR(100),
    numero_guia VARCHAR(100),
    estado VARCHAR(30) DEFAULT 'preparando',
    fecha_despacho TIMESTAMP NULL,
    fecha_entrega_estimada TIMESTAMP NULL,
    fecha_entrega_real TIMESTAMP NULL,
    costo_envio DECIMAL(10,2),
    peso_total DECIMAL(8,3),
    dimensiones JSON,
    direccion_origen TEXT,
    direccion_destino TEXT,
    instrucciones_entrega TEXT,
    evidencia_entrega JSON,
    seguimiento JSON DEFAULT ('[]'),
    intentos_entrega INT DEFAULT 0,
    max_intentos_entrega INT DEFAULT 3,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    INDEX idx_envios_pedido (pedido_id),
    INDEX idx_envios_guia (numero_guia),
    INDEX idx_envios_estado (estado),
    INDEX idx_envios_transportista (transportista)
);

-- =====================================================
-- TABLA DE COMPROBANTES
-- =====================================================

CREATE TABLE comprobantes (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    pedido_id CHAR(36) NOT NULL,
    tipo_comprobante VARCHAR(20) NOT NULL,
    serie VARCHAR(10) NOT NULL,
    numero INT NOT NULL,
    numero_completo VARCHAR(50) GENERATED ALWAYS AS (CONCAT(serie, '-', LPAD(numero, 8, '0'))) STORED,
    ruc_emisor VARCHAR(20) NOT NULL,
    razon_social_emisor VARCHAR(200) NOT NULL,
    direccion_emisor TEXT,
    documento_receptor VARCHAR(20),
    tipo_documento_receptor VARCHAR(10),
    nombre_receptor VARCHAR(200),
    direccion_receptor TEXT,
    subtotal DECIMAL(10,2) NOT NULL,
    igv DECIMAL(10,2) NOT NULL,
    total DECIMAL(10,2) NOT NULL,
    moneda VARCHAR(10) DEFAULT 'PEN',
    fecha_emision TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    fecha_vencimiento TIMESTAMP NULL,
    estado_sunat VARCHAR(30),
    codigo_hash VARCHAR(255),
    xml_firmado TEXT,
    pdf_url VARCHAR(500),
    observaciones TEXT,
    fecha_envio_sunat TIMESTAMP NULL,
    fecha_respuesta_sunat TIMESTAMP NULL,
    codigo_respuesta_sunat VARCHAR(10),
    descripcion_respuesta_sunat TEXT,
    anulado BOOLEAN DEFAULT FALSE,
    fecha_anulacion TIMESTAMP NULL,
    motivo_anulacion TEXT,
    fecha_creacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (pedido_id) REFERENCES pedidos(id) ON DELETE CASCADE,
    UNIQUE KEY uk_comprobantes_tipo_serie_numero (tipo_comprobante, serie, numero),
    INDEX idx_comprobantes_pedido (pedido_id),
    INDEX idx_comprobantes_numero (numero_completo),
    INDEX idx_comprobantes_estado (estado_sunat),
    INDEX idx_comprobantes_fecha (fecha_emision)
);

-- =====================================================
-- TABLA DE AUDITORÍA
-- =====================================================

CREATE TABLE auditoria (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    usuario_id CHAR(36),
    sesion_id VARCHAR(255),
    accion VARCHAR(100) NOT NULL,
    tabla_afectada VARCHAR(100),
    registro_id CHAR(36),
    datos_anteriores JSON,
    datos_nuevos JSON,
    ip_address VARCHAR(45),
    user_agent TEXT,
    modulo VARCHAR(50),
    severidad VARCHAR(20) DEFAULT 'info',
    descripcion TEXT,
    metadatos JSON DEFAULT ('{}'),
    fecha_accion TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    FOREIGN KEY (usuario_id) REFERENCES usuarios(id),
    INDEX idx_auditoria_usuario (usuario_id),
    INDEX idx_auditoria_fecha (fecha_accion),
    INDEX idx_auditoria_accion (accion),
    INDEX idx_auditoria_tabla (tabla_afectada),
    INDEX idx_auditoria_severidad (severidad)
);

-- =====================================================
-- TABLA DE CONFIGURACIÓN DEL SISTEMA
-- =====================================================

CREATE TABLE configuracion_sistema (
    id CHAR(36) PRIMARY KEY DEFAULT (UUID()),
    clave VARCHAR(100) UNIQUE NOT NULL,
    valor TEXT,
    tipo_dato VARCHAR(20) DEFAULT 'string',
    categoria VARCHAR(50),
    descripcion TEXT,
    valor_por_defecto TEXT,
    requerido BOOLEAN DEFAULT FALSE,
    editable BOOLEAN DEFAULT TRUE,
    validacion JSON,
    fecha_actualizacion TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    actualizado_por CHAR(36),
    
    FOREIGN KEY (actualizado_por) REFERENCES usuarios(id),
    INDEX idx_configuracion_categoria (categoria),
    INDEX idx_configuracion_clave (clave)
);

-- =====================================================
-- FUNCIONES Y PROCEDIMIENTOS ALMACENADOS
-- =====================================================

DELIMITER //

-- Función para generar número de pedido
CREATE FUNCTION generar_numero_pedido() RETURNS VARCHAR(50)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE nuevo_numero VARCHAR(50);
    DECLARE contador INT;
    
    SELECT IFNULL(MAX(CAST(SUBSTRING(numero_pedido, 9) AS UNSIGNED)), 0) + 1
    INTO contador
    FROM pedidos
    WHERE numero_pedido LIKE CONCAT('DPM', YEAR(NOW()), '%');
    
    SET nuevo_numero = CONCAT('DPM', YEAR(NOW()), LPAD(contador, 6, '0'));
    RETURN nuevo_numero;
END//

-- Función para generar número de devolución
CREATE FUNCTION generar_numero_devolucion() RETURNS VARCHAR(50)
READS SQL DATA
DETERMINISTIC
BEGIN
    DECLARE nuevo_numero VARCHAR(50);
    DECLARE contador INT;
    
    SELECT IFNULL(MAX(CAST(SUBSTRING(numero_devolucion, 9) AS UNSIGNED)), 0) + 1
    INTO contador
    FROM devoluciones
    WHERE numero_devolucion LIKE CONCAT('DEV', YEAR(NOW()), '%');
    
    SET nuevo_numero = CONCAT('DEV', YEAR(NOW()), LPAD(contador, 6, '0'));
    RETURN nuevo_numero;
END//

-- Procedimiento para calcular totales del carrito
CREATE PROCEDURE calcular_totales_carrito(IN carrito_id_param CHAR(36))
BEGIN
    DECLARE subtotal_calc DECIMAL(10,2) DEFAULT 0;
    DECLARE descuento_calc DECIMAL(10,2) DEFAULT 0;
    DECLARE impuestos_calc DECIMAL(10,2) DEFAULT 0;
    DECLARE total_calc DECIMAL(10,2) DEFAULT 0;
    
    -- Calcular subtotal
    SELECT IFNULL(SUM(subtotal), 0) INTO subtotal_calc
    FROM detalle_carrito
    WHERE carrito_id = carrito_id_param;
    
    -- Obtener descuento actual
    SELECT IFNULL(descuento, 0) INTO descuento_calc
    FROM carritos
    WHERE id = carrito_id_param;
    
    -- Calcular IGV (18%)
    SET impuestos_calc = (subtotal_calc - descuento_calc) * 0.18;
    
    -- Calcular total
    SET total_calc = subtotal_calc - descuento_calc + impuestos_calc;
    
    -- Actualizar carrito
    UPDATE carritos SET
        subtotal = subtotal_calc,
        impuestos = impuestos_calc,
        total = total_calc,
        fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE id = carrito_id_param;
END//

-- Procedimiento para actualizar stock
CREATE PROCEDURE actualizar_stock_variante(
    IN variante_id_param CHAR(36),
    IN sucursal_id_param CHAR(36),
    IN cantidad_cambio INT,
    IN tipo_movimiento VARCHAR(20)
)
BEGIN
    DECLARE stock_actual INT DEFAULT 0;
    
    -- Obtener stock actual
    SELECT cantidad_disponible INTO stock_actual
    FROM inventario
    WHERE variante_id = variante_id_param AND sucursal_id = sucursal_id_param;
    
    -- Actualizar según tipo de movimiento
    IF tipo_movimiento = 'ingreso' THEN
        UPDATE inventario SET
            cantidad_disponible = cantidad_disponible + cantidad_cambio,
            fecha_ultimo_ingreso = CURRENT_TIMESTAMP,
            ultimo_movimiento = CURRENT_TIMESTAMP
        WHERE variante_id = variante_id_param AND sucursal_id = sucursal_id_param;
    ELSEIF tipo_movimiento = 'egreso' THEN
        UPDATE inventario SET
            cantidad_disponible = GREATEST(cantidad_disponible - cantidad_cambio, 0),
            fecha_ultimo_egreso = CURRENT_TIMESTAMP,
            ultimo_movimiento = CURRENT_TIMESTAMP
        WHERE variante_id = variante_id_param AND sucursal_id = sucursal_id_param;
    ELSEIF tipo_movimiento = 'reserva' THEN
        UPDATE inventario SET
            cantidad_reservada = cantidad_reservada + cantidad_cambio,
            ultimo_movimiento = CURRENT_TIMESTAMP
        WHERE variante_id = variante_id_param AND sucursal_id = sucursal_id_param;
    ELSEIF tipo_movimiento = 'liberar' THEN
        UPDATE inventario SET
            cantidad_reservada = GREATEST(cantidad_reservada - cantidad_cambio, 0),
            ultimo_movimiento = CURRENT_TIMESTAMP
        WHERE variante_id = variante_id_param AND sucursal_id = sucursal_id_param;
    END IF;
END//

DELIMITER ;

-- =====================================================
-- TRIGGERS
-- =====================================================

DELIMITER //

-- Trigger para asignar número de pedido automáticamente
CREATE TRIGGER trigger_pedidos_numero_automatico
    BEFORE INSERT ON pedidos
    FOR EACH ROW
BEGIN
    IF NEW.numero_pedido IS NULL OR NEW.numero_pedido = '' THEN
        SET NEW.numero_pedido = generar_numero_pedido();
    END IF;
END//

-- Trigger para asignar número de devolución automáticamente
CREATE TRIGGER trigger_devoluciones_numero_automatico
    BEFORE INSERT ON devoluciones
    FOR EACH ROW
BEGIN
    IF NEW.numero_devolucion IS NULL OR NEW.numero_devolucion = '' THEN
        SET NEW.numero_devolucion = generar_numero_devolucion();
    END IF;
END//

-- Trigger para actualizar calificación promedio del producto
CREATE TRIGGER trigger_actualizar_calificacion_producto_insert
    AFTER INSERT ON reseñas
    FOR EACH ROW
BEGIN
    UPDATE productos p SET
        calificacion_promedio = (
            SELECT ROUND(AVG(calificacion), 2)
            FROM reseñas r
            WHERE r.producto_id = NEW.producto_id
            AND r.estado_moderacion = 'aprobada'
        ),
        total_reseñas = (
            SELECT COUNT(*)
            FROM reseñas r
            WHERE r.producto_id = NEW.producto_id
            AND r.estado_moderacion = 'aprobada'
        ),
        fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE p.id = NEW.producto_id;
END//

CREATE TRIGGER trigger_actualizar_calificacion_producto_update
    AFTER UPDATE ON reseñas
    FOR EACH ROW
BEGIN
    UPDATE productos p SET
        calificacion_promedio = (
            SELECT ROUND(AVG(calificacion), 2)
            FROM reseñas r
            WHERE r.producto_id = NEW.producto_id
            AND r.estado_moderacion = 'aprobada'
        ),
        total_reseñas = (
            SELECT COUNT(*)
            FROM reseñas r
            WHERE r.producto_id = NEW.producto_id
            AND r.estado_moderacion = 'aprobada'
        ),
        fecha_actualizacion = CURRENT_TIMESTAMP
    WHERE p.id = NEW.producto_id;
END//

-- Trigger para actualizar totales del carrito
CREATE TRIGGER trigger_actualizar_carrito_insert
    AFTER INSERT ON detalle_carrito
    FOR EACH ROW
BEGIN
    CALL calcular_totales_carrito(NEW.carrito_id);
END//

CREATE TRIGGER trigger_actualizar_carrito_update
    AFTER UPDATE ON detalle_carrito
    FOR EACH ROW
BEGIN
    CALL calcular_totales_carrito(NEW.carrito_id);
END//

CREATE TRIGGER trigger_actualizar_carrito_delete
    AFTER DELETE ON detalle_carrito
    FOR EACH ROW
BEGIN
    CALL calcular_totales_carrito(OLD.carrito_id);
END//

DELIMITER ;

-- =====================================================
-- DATOS INICIALES
-- =====================================================

-- Insertar roles básicos
INSERT INTO roles (nombre_rol, descripcion, permisos) VALUES
('Administrador', 'Acceso completo al sistema', '{"all": true}'),
('Empleado', 'Gestión de inventario y atención al cliente', '{"products": true, "inventory": true, "orders": true}'),
('Cajero', 'Operación de punto de venta', '{"pos": true, "sales": true, "cash": true}'),
('Cliente', 'Compras online y consultas', '{"shop": true, "profile": true}');

-- Insertar sucursal principal
INSERT INTO sucursales (nombre_sucursal, direccion, telefono, es_principal) VALUES
('Tienda Principal Pampa Hermosa', 'Pampa Hermosa, Loreto, Perú', '+51 XXX XXX XXX', TRUE);

-- Insertar categorías principales
INSERT INTO categorias (nombre_categoria, descripcion, nivel, orden_visualizacion) VALUES
('Ropa Femenina', 'Prendas de vestir para mujeres', 1, 1),
('Ropa Masculina', 'Prendas de vestir para hombres', 1, 2),
('Accesorios', 'Complementos y accesorios', 1, 3),
('Calzado', 'Zapatos y sandalias', 1, 4);

-- Insertar subcategorías
INSERT INTO categorias (nombre_categoria, descripcion, categoria_padre_id, nivel, orden_visualizacion) VALUES
('Blusas', 'Blusas y camisas femeninas', (SELECT id FROM categorias WHERE nombre_categoria = 'Ropa Femenina'), 2, 1),
('Pantalones', 'Pantalones y jeans femeninos', (SELECT id FROM categorias WHERE nombre_categoria = 'Ropa Femenina'), 2, 2),
('Vestidos', 'Vestidos casuales y elegantes', (SELECT id FROM categorias WHERE nombre_categoria = 'Ropa Femenina'), 2, 3),
('Camisas', 'Camisas masculinas', (SELECT id FROM categorias WHERE nombre_categoria = 'Ropa Masculina'), 2, 1),
('Pantalones Hombre', 'Pantalones y jeans masculinos', (SELECT id FROM categorias WHERE nombre_categoria = 'Ropa Masculina'), 2, 2);

-- Insertar caja principal
INSERT INTO cajas (sucursal_id, numero_caja, nombre_caja)
SELECT id, 'CAJA001', 'Caja Principal'
FROM sucursales WHERE es_principal = TRUE;

-- Insertar cupones de ejemplo
INSERT INTO cupones (codigo_cupon, nombre, descripcion, tipo_descuento, valor_descuento, monto_minimo_compra, fecha_inicio, fecha_fin, usos_maximos) VALUES
('BIENVENIDO10', 'Descuento de Bienvenida', '10% de descuento en tu primera compra', 'porcentaje', 10.00, 50.00, NOW(), DATE_ADD(NOW(), INTERVAL 30 DAY), 1000),
('VERANO25', 'Descuento de Verano', 'S/ 25 de descuento en compras mayores a S/ 150', 'monto_fijo', 25.00, 150.00, NOW(), DATE_ADD(NOW(), INTERVAL 60 DAY), 500);

-- Insertar promociones de ejemplo
INSERT INTO promociones (nombre_promocion, descripcion, tipo_promocion, condiciones, descuento, fecha_inicio, fecha_fin) VALUES
('Descuento por Cantidad', '15% descuento comprando 3 o más prendas', 'descuento_cantidad', '{"cantidad_minima": 3}', '{"tipo": "porcentaje", "valor": 15}', NOW(), DATE_ADD(NOW(), INTERVAL 90 DAY)),
('Oferta Fines de Semana', '20% descuento todos los fines de semana', 'descuento_categoria', '{"categorias": []}', '{"tipo": "porcentaje", "valor": 20}', NOW(), DATE_ADD(NOW(), INTERVAL 180 DAY));

-- Insertar configuraciones del sistema
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
('version_sistema', '1.0.0', 'string', 'general', 'Versión actual del sistema');

-- =====================================================
-- VISTAS ÚTILES
-- =====================================================

-- Vista de productos con stock
CREATE VIEW vista_productos_con_stock AS
SELECT 
    p.id,
    p.codigo_producto,
    p.nombre_producto,
    p.precio_base,
    p.precio_oferta,
    c.nombre_categoria,
    SUM(i.cantidad_disponible) as stock_total,
    SUM(i.cantidad_disponible - i.cantidad_reservada) as stock_disponible,
    p.calificacion_promedio,
    p.total_reseñas,
    p.activo
FROM productos p
LEFT JOIN categorias c ON p.categoria_id = c.id
LEFT JOIN variantes_producto vp ON p.id = vp.producto_id
LEFT JOIN inventario i ON vp.id = i.variante_id
WHERE p.activo = TRUE
GROUP BY p.id, p.codigo_producto, p.nombre_producto, p.precio_base, p.precio_oferta, c.nombre_categoria, p.calificacion_promedio, p.total_reseñas, p.activo;

-- Vista de ventas diarias
CREATE VIEW vista_ventas_diarias AS
SELECT 
    DATE(fecha_creacion) as fecha,
    COUNT(*) as total_pedidos,
    SUM(total) as ventas_totales,
    AVG(total) as ticket_promedio,
    COUNT(DISTINCT usuario_id) as clientes_unicos,
    SUM(CASE WHEN tipo_venta = 'online' THEN 1 ELSE 0 END) as ventas_online,
    SUM(CASE WHEN tipo_venta = 'presencial' THEN 1 ELSE 0 END) as ventas_presenciales
FROM pedidos
WHERE estado NOT IN ('cancelado')
GROUP BY DATE(fecha_creacion)
ORDER BY fecha DESC;

-- Vista de productos más vendidos
CREATE VIEW vista_productos_mas_vendidos AS
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
AND pe.fecha_creacion >= DATE_SUB(NOW(), INTERVAL 30 DAY)
GROUP BY p.id, p.nombre_producto, p.codigo_producto, c.nombre_categoria, p.calificacion_promedio
ORDER BY total_vendido DESC;

-- =====================================================
-- EVENTOS PROGRAMADOS
-- =====================================================

DELIMITER //

-- Evento para limpiar carritos abandonados
CREATE EVENT IF NOT EXISTS evento_limpiar_carritos_abandonados
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    -- Marcar carritos como abandonados
    UPDATE carritos 
    SET estado = 'abandonado' 
    WHERE estado = 'activo' 
    AND fecha_actualizacion < DATE_SUB(NOW(), INTERVAL 7 DAY);
    
    -- Eliminar carritos muy antiguos
    DELETE FROM carritos 
    WHERE estado = 'abandonado' 
    AND fecha_actualizacion < DATE_SUB(NOW(), INTERVAL 30 DAY);
END//

-- Evento para limpiar notificaciones expiradas
CREATE EVENT IF NOT EXISTS evento_limpiar_notificaciones_expiradas
ON SCHEDULE EVERY 1 DAY
STARTS CURRENT_TIMESTAMP
DO
BEGIN
    DELETE FROM notificaciones 
    WHERE expira_en IS NOT NULL 
    AND expira_en < DATE_SUB(NOW(), INTERVAL 7 DAY);
END//

DELIMITER ;

-- =====================================================
-- USUARIO ADMINISTRADOR POR DEFECTO
-- =====================================================

-- Insertar usuario administrador por defecto
-- Contraseña: admin123 (debe cambiarse en producción)
INSERT INTO usuarios (
    email, 
    password_hash, 
    nombres, 
    apellidos, 
    rol_id, 
    activo, 
    email_verificado
) VALUES (
    'admin@dpattymoda.com',
    '$2a$12$LQv3c1yqBw2fyuDjVkHdXeNsQ8oxYXQsHdlbgJbrydHlfvI3c/EiC', -- admin123
    'Administrador',
    'Sistema',
    (SELECT id FROM roles WHERE nombre_rol = 'Administrador'),
    TRUE,
    TRUE
);

-- =====================================================
-- PRODUCTOS DE EJEMPLO
-- =====================================================

-- Insertar algunos productos de ejemplo
INSERT INTO productos (
    codigo_producto,
    nombre_producto,
    descripcion,
    descripcion_corta,
    categoria_id,
    marca,
    precio_base,
    precio_oferta,
    costo_producto,
    peso,
    imagenes,
    activo,
    destacado,
    nuevo
) VALUES 
(
    'BLU001',
    'Blusa Casual Manga Larga',
    'Blusa casual perfecta para el día a día, confeccionada en algodón suave y transpirable.',
    'Blusa casual de algodón, cómoda y versátil',
    (SELECT id FROM categorias WHERE nombre_categoria = 'Blusas'),
    'DPattyModa',
    75.00,
    67.50,
    45.00,
    0.250,
    '["https://images.pexels.com/photos/1536619/pexels-photo-1536619.jpeg"]',
    TRUE,
    TRUE,
    TRUE
),
(
    'PAN001',
    'Pantalón Jean Clásico',
    'Pantalón jean de corte clásico, ideal para cualquier ocasión. Confeccionado en denim de alta calidad.',
    'Jean clásico de denim premium',
    (SELECT id FROM categorias WHERE nombre_categoria = 'Pantalones'),
    'DPattyModa',
    120.00,
    108.00,
    80.00,
    0.600,
    '["https://images.pexels.com/photos/1598505/pexels-photo-1598505.jpeg"]',
    TRUE,
    FALSE,
    FALSE
),
(
    'VES001',
    'Vestido Floral Verano',
    'Hermoso vestido con estampado floral, perfecto para la temporada de verano. Tela fresca y ligera.',
    'Vestido floral de verano, fresco y elegante',
    (SELECT id FROM categorias WHERE nombre_categoria = 'Vestidos'),
    'DPattyModa',
    95.00,
    85.50,
    60.00,
    0.300,
    '["https://images.pexels.com/photos/1536619/pexels-photo-1536619.jpeg"]',
    TRUE,
    TRUE,
    TRUE
);

-- Insertar variantes para los productos
INSERT INTO variantes_producto (producto_id, sku, talla, color, precio_variante) VALUES
-- Variantes para Blusa Casual
((SELECT id FROM productos WHERE codigo_producto = 'BLU001'), 'BLU001-S-BLANCO', 'S', 'Blanco', 75.00),
((SELECT id FROM productos WHERE codigo_producto = 'BLU001'), 'BLU001-M-BLANCO', 'M', 'Blanco', 75.00),
((SELECT id FROM productos WHERE codigo_producto = 'BLU001'), 'BLU001-L-BLANCO', 'L', 'Blanco', 75.00),
((SELECT id FROM productos WHERE codigo_producto = 'BLU001'), 'BLU001-S-AZUL', 'S', 'Azul', 75.00),
((SELECT id FROM productos WHERE codigo_producto = 'BLU001'), 'BLU001-M-AZUL', 'M', 'Azul', 75.00),

-- Variantes para Pantalón Jean
((SELECT id FROM productos WHERE codigo_producto = 'PAN001'), 'PAN001-28-AZUL', '28', 'Azul Oscuro', 120.00),
((SELECT id FROM productos WHERE codigo_producto = 'PAN001'), 'PAN001-30-AZUL', '30', 'Azul Oscuro', 120.00),
((SELECT id FROM productos WHERE codigo_producto = 'PAN001'), 'PAN001-32-AZUL', '32', 'Azul Oscuro', 120.00),
((SELECT id FROM productos WHERE codigo_producto = 'PAN001'), 'PAN001-34-AZUL', '34', 'Azul Oscuro', 120.00),

-- Variantes para Vestido Floral
((SELECT id FROM productos WHERE codigo_producto = 'VES001'), 'VES001-S-FLORAL', 'S', 'Floral Rosa', 95.00),
((SELECT id FROM productos WHERE codigo_producto = 'VES001'), 'VES001-M-FLORAL', 'M', 'Floral Rosa', 95.00),
((SELECT id FROM productos WHERE codigo_producto = 'VES001'), 'VES001-L-FLORAL', 'L', 'Floral Rosa', 95.00);

-- Insertar inventario inicial para las variantes
INSERT INTO inventario (variante_id, sucursal_id, cantidad_disponible, cantidad_minima, ubicacion_fisica) 
SELECT 
    vp.id,
    s.id,
    FLOOR(RAND() * 20) + 10, -- Stock aleatorio entre 10 y 30
    5,
    CONCAT('A', FLOOR(RAND() * 5) + 1, '-B', FLOOR(RAND() * 10) + 1)
FROM variantes_producto vp
CROSS JOIN sucursales s
WHERE s.es_principal = TRUE;

-- =====================================================
-- FINALIZACIÓN
-- =====================================================

-- Mensaje de confirmación
SELECT 'Base de datos DPattyModa creada exitosamente' AS mensaje;
SELECT COUNT(*) AS total_tablas FROM information_schema.tables WHERE table_schema = 'dpatty_moda';
SELECT 'Sistema listo para usar' AS estado;

-- Mostrar información importante
SELECT 
    'admin@dpattymoda.com' AS usuario_admin,
    'admin123' AS password_temporal,
    'CAMBIAR CONTRASEÑA EN PRODUCCIÓN' AS importante;