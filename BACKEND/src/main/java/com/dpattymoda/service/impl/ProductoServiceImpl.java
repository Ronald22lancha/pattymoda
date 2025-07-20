package com.dpattymoda.service.impl;

import com.dpattymoda.dto.request.ProductoCreateRequest;
import com.dpattymoda.dto.request.ProductoUpdateRequest;
import com.dpattymoda.dto.response.ProductoResponse;
import com.dpattymoda.dto.response.ProductoDetalleResponse;
import com.dpattymoda.entity.*;
import com.dpattymoda.exception.BusinessException;
import com.dpattymoda.exception.ResourceNotFoundException;
import com.dpattymoda.repository.*;
import com.dpattymoda.service.AuditoriaService;
import com.dpattymoda.service.ProductoService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Implementación del servicio de productos
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final VarianteProductoRepository varianteProductoRepository;
    private final InventarioRepository inventarioRepository;
    private final SucursalRepository sucursalRepository;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;

    @Override
    public ProductoResponse crearProducto(ProductoCreateRequest request) {
        log.info("Creando nuevo producto: {}", request.getCodigoProducto());

        // Validar código único
        if (productoRepository.existsByCodigoProducto(request.getCodigoProducto())) {
            throw new BusinessException("Ya existe un producto con el código: " + request.getCodigoProducto());
        }

        // Obtener categoría
        Categoria categoria = categoriaRepository.findById(request.getCategoriaId())
            .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));

        // Crear producto
        Producto producto = Producto.builder()
            .codigoProducto(request.getCodigoProducto())
            .nombreProducto(request.getNombreProducto())
            .descripcion(request.getDescripcion())
            .descripcionCorta(request.getDescripcionCorta())
            .categoria(categoria)
            .marca(request.getMarca())
            .precioBase(request.getPrecioBase())
            .precioOferta(request.getPrecioOferta())
            .costoProducto(request.getCostoProducto())
            .peso(request.getPeso())
            .dimensiones(convertirAJson(request.getDimensiones()))
            .caracteristicas(convertirAJson(request.getCaracteristicas()))
            .imagenes(convertirAJson(request.getImagenes()))
            .tags(request.getTags() != null ? request.getTags().toArray(new String[0]) : null)
            .destacado(request.getDestacado())
            .nuevo(request.getNuevo())
            .fechaLanzamiento(request.getFechaLanzamiento())
            .seoTitulo(request.getSeoTitulo())
            .seoDescripcion(request.getSeoDescripcion())
            .seoPalabrasClave(request.getSeoPalabrasClave() != null ? 
                request.getSeoPalabrasClave().toArray(new String[0]) : null)
            .activo(true)
            .build();

        // Calcular margen de ganancia si hay costo
        if (request.getCostoProducto() != null && request.getCostoProducto().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = request.getPrecioBase().subtract(request.getCostoProducto())
                .divide(request.getPrecioBase(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            producto.setMargenGanancia(margen);
        }

        producto = productoRepository.save(producto);

        // Crear variantes si se proporcionan
        if (request.getVariantes() != null && !request.getVariantes().isEmpty()) {
            crearVariantesProducto(producto, request.getVariantes());
        }

        // Auditar creación
        auditoriaService.registrarAccion("CREAR_PRODUCTO", "productos", producto.getId(),
            null, convertirAJson(producto), "Producto creado: " + producto.getNombreProducto());

        log.info("Producto creado exitosamente: {}", producto.getCodigoProducto());
        return convertirAProductoResponse(producto);
    }

    @Override
    public ProductoResponse actualizarProducto(UUID id, ProductoUpdateRequest request) {
        log.info("Actualizando producto ID: {}", id);

        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        Producto productoAnterior = clonarProducto(producto);

        // Obtener categoría si cambió
        if (!producto.getCategoria().getId().equals(request.getCategoriaId())) {
            Categoria nuevaCategoria = categoriaRepository.findById(request.getCategoriaId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada"));
            producto.setCategoria(nuevaCategoria);
        }

        // Actualizar campos
        producto.setNombreProducto(request.getNombreProducto());
        producto.setDescripcion(request.getDescripcion());
        producto.setDescripcionCorta(request.getDescripcionCorta());
        producto.setMarca(request.getMarca());
        producto.setPrecioBase(request.getPrecioBase());
        producto.setPrecioOferta(request.getPrecioOferta());
        producto.setCostoProducto(request.getCostoProducto());
        producto.setPeso(request.getPeso());
        producto.setDimensiones(convertirAJson(request.getDimensiones()));
        producto.setCaracteristicas(convertirAJson(request.getCaracteristicas()));
        producto.setImagenes(convertirAJson(request.getImagenes()));
        producto.setTags(request.getTags() != null ? request.getTags().toArray(new String[0]) : null);
        
        if (request.getDestacado() != null) producto.setDestacado(request.getDestacado());
        if (request.getNuevo() != null) producto.setNuevo(request.getNuevo());
        
        producto.setFechaLanzamiento(request.getFechaLanzamiento());
        producto.setSeoTitulo(request.getSeoTitulo());
        producto.setSeoDescripcion(request.getSeoDescripcion());
        producto.setSeoPalabrasClave(request.getSeoPalabrasClave() != null ? 
            request.getSeoPalabrasClave().toArray(new String[0]) : null);

        // Recalcular margen de ganancia
        if (request.getCostoProducto() != null && request.getCostoProducto().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal margen = request.getPrecioBase().subtract(request.getCostoProducto())
                .divide(request.getPrecioBase(), 4, BigDecimal.ROUND_HALF_UP)
                .multiply(BigDecimal.valueOf(100));
            producto.setMargenGanancia(margen);
        }

        producto = productoRepository.save(producto);

        // Auditar actualización
        auditoriaService.registrarAccion("ACTUALIZAR_PRODUCTO", "productos", producto.getId(),
            convertirAJson(productoAnterior), convertirAJson(producto), 
            "Producto actualizado: " + producto.getNombreProducto());

        log.info("Producto actualizado exitosamente: {}", producto.getCodigoProducto());
        return convertirAProductoResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoDetalleResponse obtenerProductoDetalle(UUID id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        return convertirAProductoDetalleResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerProducto(UUID id) {
        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        return convertirAProductoResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductoResponse obtenerProductoPorCodigo(String codigo) {
        Producto producto = productoRepository.findByCodigoProductoAndActivoTrue(codigo)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con código: " + codigo));

        return convertirAProductoResponse(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> listarProductos(Pageable pageable) {
        Page<Producto> productos = productoRepository.findByActivoTrue(pageable);
        List<ProductoResponse> productosResponse = productos.getContent().stream()
            .map(this::convertirAProductoResponse)
            .collect(Collectors.toList());

        return new PageImpl<>(productosResponse, pageable, productos.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> buscarProductos(String termino, Pageable pageable) {
        Page<Producto> productos = productoRepository.buscarProductos(termino, pageable);
        List<ProductoResponse> productosResponse = productos.getContent().stream()
            .map(this::convertirAProductoResponse)
            .collect(Collectors.toList());

        return new PageImpl<>(productosResponse, pageable, productos.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> buscarConFiltros(UUID categoriaId, String marca, 
                                                  BigDecimal precioMin, BigDecimal precioMax,
                                                  Boolean destacado, Boolean nuevo, 
                                                  Pageable pageable) {
        Page<Producto> productos = productoRepository.buscarConFiltros(
            categoriaId, marca, precioMin, precioMax, destacado, nuevo, pageable);
        
        List<ProductoResponse> productosResponse = productos.getContent().stream()
            .map(this::convertirAProductoResponse)
            .collect(Collectors.toList());

        return new PageImpl<>(productosResponse, pageable, productos.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductoResponse> obtenerProductosPorCategoria(UUID categoriaId, Pageable pageable) {
        Page<Producto> productos = productoRepository.findByCategoria_IdAndActivoTrue(categoriaId, pageable);
        List<ProductoResponse> productosResponse = productos.getContent().stream()
            .map(this::convertirAProductoResponse)
            .collect(Collectors.toList());

        return new PageImpl<>(productosResponse, pageable, productos.getTotalElements());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosDestacados() {
        return productoRepository.findByDestacadoTrueAndActivoTrueOrderByTotalVentasDesc()
            .stream()
            .map(this::convertirAProductoResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosNuevos() {
        return productoRepository.findByNuevoTrueAndActivoTrueOrderByFechaCreacionDesc()
            .stream()
            .map(this::convertirAProductoResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosMasVendidos(int limite) {
        return productoRepository.findMasVendidos(Pageable.ofSize(limite))
            .stream()
            .map(this::convertirAProductoResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosMejorCalificados(int limite) {
        return productoRepository.findMejorCalificados(Pageable.ofSize(limite))
            .stream()
            .map(this::convertirAProductoResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> obtenerMarcasDisponibles() {
        return productoRepository.findMarcasDisponibles();
    }

    @Override
    public void cambiarEstadoProducto(UUID id, boolean activo) {
        log.info("Cambiando estado del producto ID: {} a {}", id, activo);

        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        boolean estadoAnterior = producto.getActivo();
        producto.setActivo(activo);
        productoRepository.save(producto);

        // Auditar cambio de estado
        auditoriaService.registrarAccion(
            activo ? "ACTIVAR_PRODUCTO" : "DESACTIVAR_PRODUCTO",
            "productos", producto.getId(),
            "{\"activo\":" + estadoAnterior + "}",
            "{\"activo\":" + activo + "}",
            "Estado de producto cambiado: " + producto.getNombreProducto()
        );

        log.info("Estado del producto {} cambiado a: {}", producto.getNombreProducto(), activo);
    }

    @Override
    public void marcarComoDestacado(UUID id, boolean destacado) {
        log.info("Marcando producto ID: {} como destacado: {}", id, destacado);

        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.setDestacado(destacado);
        productoRepository.save(producto);

        // Auditar cambio
        auditoriaService.registrarAccion("MARCAR_DESTACADO", "productos", producto.getId(),
            null, null, "Producto marcado como destacado: " + destacado);

        log.info("Producto {} marcado como destacado: {}", producto.getNombreProducto(), destacado);
    }

    @Override
    public void actualizarCalificacionPromedio(UUID id) {
        // Esta funcionalidad se maneja automáticamente con triggers en la base de datos
        log.info("Calificación promedio actualizada automáticamente para producto ID: {}", id);
    }

    @Override
    public void incrementarContadorVentas(UUID id, int cantidad) {
        log.info("Incrementando contador de ventas para producto ID: {} en {}", id, cantidad);

        Producto producto = productoRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        producto.setTotalVentas(producto.getTotalVentas() + cantidad);
        productoRepository.save(producto);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductoResponse> obtenerProductosConStockBajo() {
        return productoRepository.findProductosConStockBajo()
            .stream()
            .map(this::convertirAProductoResponse)
            .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public long contarProductosActivos() {
        return productoRepository.contarProductosActivos();
    }

    @Override
    @Transactional(readOnly = true)
    public BigDecimal obtenerPrecioPromedio() {
        return productoRepository.obtenerPrecioPromedio();
    }

    // Métodos privados de utilidad

    private void crearVariantesProducto(Producto producto, List<ProductoCreateRequest.VarianteCreateRequest> variantes) {
        for (ProductoCreateRequest.VarianteCreateRequest varianteRequest : variantes) {
            // Validar SKU único
            if (varianteProductoRepository.existsBySku(varianteRequest.getSku())) {
                throw new BusinessException("Ya existe una variante con el SKU: " + varianteRequest.getSku());
            }

            VarianteProducto variante = VarianteProducto.builder()
                .producto(producto)
                .sku(varianteRequest.getSku())
                .talla(varianteRequest.getTalla())
                .color(varianteRequest.getColor())
                .material(varianteRequest.getMaterial())
                .precioVariante(varianteRequest.getPrecioVariante())
                .pesoVariante(varianteRequest.getPesoVariante())
                .imagenVariante(varianteRequest.getImagenVariante())
                .codigoBarras(varianteRequest.getCodigoBarras())
                .activo(true)
                .build();

            variante = varianteProductoRepository.save(variante);

            // Crear inventario inicial si se proporciona
            if (varianteRequest.getStockInicial() != null) {
                crearInventarioInicial(variante, varianteRequest.getStockInicial());
            }
        }
    }

    private void crearInventarioInicial(VarianteProducto variante, 
                                      List<ProductoCreateRequest.StockInicialRequest> stockInicial) {
        for (ProductoCreateRequest.StockInicialRequest stock : stockInicial) {
            Sucursal sucursal = sucursalRepository.findById(stock.getSucursalId())
                .orElseThrow(() -> new ResourceNotFoundException("Sucursal no encontrada"));

            Inventario inventario = Inventario.builder()
                .variante(variante)
                .sucursal(sucursal)
                .cantidadDisponible(stock.getCantidad())
                .cantidadMinima(stock.getCantidadMinima())
                .ubicacionFisica(stock.getUbicacionFisica())
                .build();

            inventarioRepository.save(inventario);
        }
    }

    private ProductoResponse convertirAProductoResponse(Producto producto) {
        // Obtener stock total
        Integer stockTotal = inventarioRepository.obtenerStockTotalPorVariante(producto.getId());
        Integer stockDisponible = inventarioRepository.obtenerStockDisponiblePorVariante(producto.getId());

        // Obtener variantes
        List<VarianteProducto> variantes = varianteProductoRepository.findByProducto_IdAndActivoTrue(producto.getId());
        
        return ProductoResponse.builder()
            .id(producto.getId())
            .codigoProducto(producto.getCodigoProducto())
            .nombreProducto(producto.getNombreProducto())
            .descripcion(producto.getDescripcion())
            .descripcionCorta(producto.getDescripcionCorta())
            .categoria(ProductoResponse.CategoriaBasicaResponse.builder()
                .id(producto.getCategoria().getId())
                .nombreCategoria(producto.getCategoria().getNombreCategoria())
                .rutaCompleta(producto.getCategoria().getRutaCompleta())
                .build())
            .marca(producto.getMarca())
            .precioBase(producto.getPrecioBase())
            .precioOferta(producto.getPrecioOferta())
            .precioVenta(producto.getPrecioVenta())
            .porcentajeDescuento(producto.getPorcentajeDescuento())
            .costoProducto(producto.getCostoProducto())
            .peso(producto.getPeso())
            .dimensiones(producto.getDimensiones())
            .caracteristicas(producto.getCaracteristicas())
            .imagenes(convertirDeJson(producto.getImagenes(), List.class))
            .tags(producto.getTags() != null ? List.of(producto.getTags()) : null)
            .activo(producto.getActivo())
            .destacado(producto.getDestacado())
            .nuevo(producto.getNuevo())
            .fechaLanzamiento(producto.getFechaLanzamiento())
            .calificacionPromedio(producto.getCalificacionPromedio())
            .totalReseñas(producto.getTotalReseñas())
            .totalVentas(producto.getTotalVentas())
            .stockTotal(stockTotal != null ? stockTotal : 0)
            .stockDisponible(stockDisponible != null ? stockDisponible : 0)
            .tieneStock(stockDisponible != null && stockDisponible > 0)
            .tallasDisponibles(varianteProductoRepository.obtenerTallasDisponibles(producto.getId()))
            .coloresDisponibles(varianteProductoRepository.obtenerColoresDisponibles(producto.getId()))
            .seoTitulo(producto.getSeoTitulo())
            .seoDescripcion(producto.getSeoDescripcion())
            .seoPalabrasClave(producto.getSeoPalabrasClave() != null ? 
                List.of(producto.getSeoPalabrasClave()) : null)
            .fechaCreacion(producto.getFechaCreacion())
            .fechaActualizacion(producto.getFechaActualizacion())
            .build();
    }

    private ProductoDetalleResponse convertirAProductoDetalleResponse(Producto producto) {
        // Obtener variantes con stock
        List<VarianteProducto> variantes = varianteProductoRepository.findByProducto_IdAndActivoTrue(producto.getId());
        
        return ProductoDetalleResponse.builder()
            .id(producto.getId())
            .codigoProducto(producto.getCodigoProducto())
            .nombreProducto(producto.getNombreProducto())
            .descripcion(producto.getDescripcion())
            .descripcionCorta(producto.getDescripcionCorta())
            .categoria(ProductoDetalleResponse.CategoriaDetalleResponse.builder()
                .id(producto.getCategoria().getId())
                .nombreCategoria(producto.getCategoria().getNombreCategoria())
                .descripcion(producto.getCategoria().getDescripcion())
                .rutaCompleta(producto.getCategoria().getRutaCompleta())
                .imagenUrl(producto.getCategoria().getImagenUrl())
                .build())
            .marca(producto.getMarca())
            .precioBase(producto.getPrecioBase())
            .precioOferta(producto.getPrecioOferta())
            .precioVenta(producto.getPrecioVenta())
            .porcentajeDescuento(producto.getPorcentajeDescuento())
            .tieneOferta(producto.tieneOferta())
            .peso(producto.getPeso())
            .dimensiones(producto.getDimensiones())
            .caracteristicas(producto.getCaracteristicas())
            .imagenes(convertirDeJson(producto.getImagenes(), List.class))
            .tags(producto.getTags() != null ? List.of(producto.getTags()) : null)
            .activo(producto.getActivo())
            .destacado(producto.getDestacado())
            .nuevo(producto.getNuevo())
            .fechaLanzamiento(producto.getFechaLanzamiento())
            .calificacionPromedio(producto.getCalificacionPromedio())
            .totalReseñas(producto.getTotalReseñas())
            .totalVentas(producto.getTotalVentas())
            .tallasDisponibles(varianteProductoRepository.obtenerTallasDisponibles(producto.getId()))
            .coloresDisponibles(varianteProductoRepository.obtenerColoresDisponibles(producto.getId()))
            .seoTitulo(producto.getSeoTitulo())
            .seoDescripcion(producto.getSeoDescripcion())
            .seoPalabrasClave(producto.getSeoPalabrasClave() != null ? 
                List.of(producto.getSeoPalabrasClave()) : null)
            .fechaCreacion(producto.getFechaCreacion())
            .fechaActualizacion(producto.getFechaActualizacion())
            .build();
    }

    private Producto clonarProducto(Producto producto) {
        return Producto.builder()
            .id(producto.getId())
            .codigoProducto(producto.getCodigoProducto())
            .nombreProducto(producto.getNombreProducto())
            .descripcion(producto.getDescripcion())
            .precioBase(producto.getPrecioBase())
            .precioOferta(producto.getPrecioOferta())
            .build();
    }

    private String convertirAJson(Object objeto) {
        if (objeto == null) return null;
        try {
            return objectMapper.writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            log.warn("Error al convertir objeto a JSON: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private <T> T convertirDeJson(String json, Class<T> clazz) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, clazz);
        } catch (JsonProcessingException e) {
            log.warn("Error al convertir JSON a objeto: {}", e.getMessage());
            return null;
        }
    }
}