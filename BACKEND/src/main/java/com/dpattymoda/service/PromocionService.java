package com.dpattymoda.service;

import com.dpattymoda.dto.request.CuponCreateRequest;
import com.dpattymoda.dto.request.PromocionCreateRequest;
import com.dpattymoda.dto.response.CuponResponse;
import com.dpattymoda.dto.response.PromocionResponse;
import com.dpattymoda.dto.response.ValidacionCuponResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de promociones y cupones
 */
public interface PromocionService {

    // Gestión de Cupones
    CuponResponse crearCupon(CuponCreateRequest request);
    CuponResponse actualizarCupon(UUID cuponId, CuponCreateRequest request);
    CuponResponse obtenerCupon(UUID cuponId);
    Page<CuponResponse> listarCupones(Pageable pageable);
    void activarDesactivarCupon(UUID cuponId, boolean activo);
    void eliminarCupon(UUID cuponId);

    // Validación y Uso de Cupones
    ValidacionCuponResponse validarCupon(String codigoCupon, UUID usuarioId, BigDecimal montoCompra, List<UUID> productosCarrito);
    void aplicarCupon(UUID carritoId, String codigoCupon);
    void removerCupon(UUID carritoId, String codigoCupon);
    void usarCupon(UUID cuponId, UUID usuarioId, UUID pedidoId, BigDecimal montoDescuento);

    // Gestión de Promociones
    PromocionResponse crearPromocion(PromocionCreateRequest request);
    PromocionResponse actualizarPromocion(UUID promocionId, PromocionCreateRequest request);
    PromocionResponse obtenerPromocion(UUID promocionId);
    Page<PromocionResponse> listarPromociones(Pageable pageable);
    void activarDesactivarPromocion(UUID promocionId, boolean activa);

    // Aplicación Automática de Promociones
    List<PromocionResponse> obtenerPromocionesAplicables(List<UUID> productosCarrito, BigDecimal montoSubtotal, UUID sucursalId);
    BigDecimal calcularDescuentoPromociones(List<UUID> productosCarrito, BigDecimal montoSubtotal, UUID sucursalId);

    // Reportes y Estadísticas
    EstadisticasPromocionesResponse obtenerEstadisticasPromociones();
    List<CuponResponse> obtenerCuponesMasUsados(int limite);
    List<PromocionResponse> obtenerPromocionesMasEfectivas(int limite);

    // DTO para estadísticas
    record EstadisticasPromocionesResponse(
        Integer totalCuponesActivos,
        Integer totalPromocionesActivas,
        BigDecimal descuentoTotalOtorgado,
        Integer usosCuponesHoy,
        BigDecimal ahorroPromedioCliente
    ) {}
}