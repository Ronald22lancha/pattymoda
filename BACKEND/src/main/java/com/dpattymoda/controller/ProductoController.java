package com.dpattymoda.controller;

import com.dpattymoda.dto.request.ProductoCreateRequest;
import com.dpattymoda.dto.request.ProductoUpdateRequest;
import com.dpattymoda.dto.response.ProductoResponse;
import com.dpattymoda.dto.response.ProductoDetalleResponse;
import com.dpattymoda.service.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para gestión de productos
 */
@Tag(name = "Productos", description = "Gestión del catálogo de productos")
@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductoController {

    private final ProductoService productoService;

    @Operation(summary = "Crear producto", description = "Crear un nuevo producto en el catálogo")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ProductoResponse> crearProducto(@Valid @RequestBody ProductoCreateRequest request) {
        ProductoResponse response = productoService.crearProducto(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar producto", description = "Actualizar información de un producto existente")
    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<ProductoResponse> actualizarProducto(
            @PathVariable UUID id,
            @Valid @RequestBody ProductoUpdateRequest request) {
        ProductoResponse response = productoService.actualizarProducto(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener producto detallado", description = "Obtener información completa de un producto")
    @GetMapping("/{id}")
    public ResponseEntity<ProductoDetalleResponse> obtenerProductoDetalle(@PathVariable UUID id) {
        ProductoDetalleResponse response = productoService.obtenerProductoDetalle(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener producto por código", description = "Buscar producto por su código único")
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<ProductoResponse> obtenerProductoPorCodigo(@PathVariable String codigo) {
        ProductoResponse response = productoService.obtenerProductoPorCodigo(codigo);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar productos", description = "Obtener lista paginada de productos activos")
    @GetMapping
    public ResponseEntity<Page<ProductoResponse>> listarProductos(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductoResponse> response = productoService.listarProductos(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar productos", description = "Buscar productos por término de búsqueda")
    @GetMapping("/buscar")
    public ResponseEntity<Page<ProductoResponse>> buscarProductos(
            @Parameter(description = "Término de búsqueda") @RequestParam String q,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductoResponse> response = productoService.buscarProductos(q, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Buscar con filtros", description = "Buscar productos con filtros avanzados")
    @GetMapping("/filtrar")
    public ResponseEntity<Page<ProductoResponse>> buscarConFiltros(
            @Parameter(description = "ID de categoría") @RequestParam(required = false) UUID categoriaId,
            @Parameter(description = "Marca") @RequestParam(required = false) String marca,
            @Parameter(description = "Precio mínimo") @RequestParam(required = false) BigDecimal precioMin,
            @Parameter(description = "Precio máximo") @RequestParam(required = false) BigDecimal precioMax,
            @Parameter(description = "Solo destacados") @RequestParam(required = false) Boolean destacado,
            @Parameter(description = "Solo nuevos") @RequestParam(required = false) Boolean nuevo,
            @PageableDefault(size = 20) Pageable pageable) {
        
        Page<ProductoResponse> response = productoService.buscarConFiltros(
            categoriaId, marca, precioMin, precioMax, destacado, nuevo, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Productos por categoría", description = "Obtener productos de una categoría específica")
    @GetMapping("/categoria/{categoriaId}")
    public ResponseEntity<Page<ProductoResponse>> obtenerProductosPorCategoria(
            @PathVariable UUID categoriaId,
            @PageableDefault(size = 20) Pageable pageable) {
        Page<ProductoResponse> response = productoService.obtenerProductosPorCategoria(categoriaId, pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Productos destacados", description = "Obtener lista de productos destacados")
    @GetMapping("/destacados")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosDestacados() {
        List<ProductoResponse> response = productoService.obtenerProductosDestacados();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Productos nuevos", description = "Obtener lista de productos nuevos")
    @GetMapping("/nuevos")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosNuevos() {
        List<ProductoResponse> response = productoService.obtenerProductosNuevos();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Más vendidos", description = "Obtener productos más vendidos")
    @GetMapping("/mas-vendidos")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosMasVendidos(
            @Parameter(description = "Límite de resultados") @RequestParam(defaultValue = "10") int limite) {
        List<ProductoResponse> response = productoService.obtenerProductosMasVendidos(limite);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Mejor calificados", description = "Obtener productos mejor calificados")
    @GetMapping("/mejor-calificados")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosMejorCalificados(
            @Parameter(description = "Límite de resultados") @RequestParam(defaultValue = "10") int limite) {
        List<ProductoResponse> response = productoService.obtenerProductosMejorCalificados(limite);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Marcas disponibles", description = "Obtener lista de marcas disponibles")
    @GetMapping("/marcas")
    public ResponseEntity<List<String>> obtenerMarcasDisponibles() {
        List<String> response = productoService.obtenerMarcasDisponibles();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cambiar estado", description = "Activar o desactivar un producto")
    @PatchMapping("/{id}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Void> cambiarEstadoProducto(
            @PathVariable UUID id,
            @Parameter(description = "Estado activo") @RequestParam boolean activo) {
        productoService.cambiarEstadoProducto(id, activo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Marcar como destacado", description = "Marcar o desmarcar producto como destacado")
    @PatchMapping("/{id}/destacado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Void> marcarComoDestacado(
            @PathVariable UUID id,
            @Parameter(description = "Destacado") @RequestParam boolean destacado) {
        productoService.marcarComoDestacado(id, destacado);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Productos con stock bajo", description = "Obtener productos con stock bajo")
    @GetMapping("/stock-bajo")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<List<ProductoResponse>> obtenerProductosConStockBajo() {
        List<ProductoResponse> response = productoService.obtenerProductosConStockBajo();
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Estadísticas de productos", description = "Obtener estadísticas generales")
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<EstadisticasProductosResponse> obtenerEstadisticas() {
        long totalProductos = productoService.contarProductosActivos();
        BigDecimal precioPromedio = productoService.obtenerPrecioPromedio();
        
        EstadisticasProductosResponse response = EstadisticasProductosResponse.builder()
            .totalProductosActivos(totalProductos)
            .precioPromedio(precioPromedio)
            .build();
            
        return ResponseEntity.ok(response);
    }

    // DTO para estadísticas
    @lombok.Data
    @lombok.Builder
    public static class EstadisticasProductosResponse {
        private long totalProductosActivos;
        private BigDecimal precioPromedio;
    }
}