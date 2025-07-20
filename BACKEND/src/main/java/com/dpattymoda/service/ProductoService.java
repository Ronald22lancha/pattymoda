package com.dpattymoda.service;

import com.dpattymoda.dto.request.ProductoCreateRequest;
import com.dpattymoda.dto.request.ProductoUpdateRequest;
import com.dpattymoda.dto.response.ProductoResponse;
import com.dpattymoda.dto.response.ProductoDetalleResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de productos del catálogo
 */
public interface ProductoService {

    /**
     * Crear un nuevo producto
     */
    ProductoResponse crearProducto(ProductoCreateRequest request);

    /**
     * Actualizar un producto existente
     */
    ProductoResponse actualizarProducto(UUID id, ProductoUpdateRequest request);

    /**
     * Obtener producto por ID con detalles completos
     */
    ProductoDetalleResponse obtenerProductoDetalle(UUID id);

    /**
     * Obtener producto básico por ID
     */
    ProductoResponse obtenerProducto(UUID id);

    /**
     * Obtener producto por código
     */
    ProductoResponse obtenerProductoPorCodigo(String codigo);

    /**
     * Listar productos activos
     */
    Page<ProductoResponse> listarProductos(Pageable pageable);

    /**
     * Buscar productos por término
     */
    Page<ProductoResponse> buscarProductos(String termino, Pageable pageable);

    /**
     * Buscar productos con filtros avanzados
     */
    Page<ProductoResponse> buscarConFiltros(UUID categoriaId, String marca, 
                                          BigDecimal precioMin, BigDecimal precioMax,
                                          Boolean destacado, Boolean nuevo, 
                                          Pageable pageable);

    /**
     * Obtener productos por categoría
     */
    Page<ProductoResponse> obtenerProductosPorCategoria(UUID categoriaId, Pageable pageable);

    /**
     * Obtener productos destacados
     */
    List<ProductoResponse> obtenerProductosDestacados();

    /**
     * Obtener productos nuevos
     */
    List<ProductoResponse> obtenerProductosNuevos();

    /**
     * Obtener productos más vendidos
     */
    List<ProductoResponse> obtenerProductosMasVendidos(int limite);

    /**
     * Obtener productos mejor calificados
     */
    List<ProductoResponse> obtenerProductosMejorCalificados(int limite);

    /**
     * Obtener marcas disponibles
     */
    List<String> obtenerMarcasDisponibles();

    /**
     * Activar/desactivar producto
     */
    void cambiarEstadoProducto(UUID id, boolean activo);

    /**
     * Marcar producto como destacado
     */
    void marcarComoDestacado(UUID id, boolean destacado);

    /**
     * Actualizar calificación promedio del producto
     */
    void actualizarCalificacionPromedio(UUID id);

    /**
     * Incrementar contador de ventas
     */
    void incrementarContadorVentas(UUID id, int cantidad);

    /**
     * Obtener productos con stock bajo
     */
    List<ProductoResponse> obtenerProductosConStockBajo();

    /**
     * Obtener estadísticas de productos
     */
    long contarProductosActivos();

    BigDecimal obtenerPrecioPromedio();
}