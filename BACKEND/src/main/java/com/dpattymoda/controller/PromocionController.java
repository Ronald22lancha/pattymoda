package com.dpattymoda.controller;

import com.dpattymoda.dto.request.PromocionCreateRequest;
import com.dpattymoda.dto.response.PromocionResponse;
import com.dpattymoda.service.PromocionService;
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
 * Controlador para gestión de promociones automáticas
 */
@Tag(name = "Promociones", description = "Gestión de promociones automáticas")
@RestController
@RequestMapping("/api/promociones")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
public class PromocionController {

    private final PromocionService promocionService;

    @Operation(summary = "Crear promoción", description = "Crear nueva promoción automática")
    @PostMapping
    public ResponseEntity<PromocionResponse> crearPromocion(@Valid @RequestBody PromocionCreateRequest request) {
        PromocionResponse response = promocionService.crearPromocion(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar promoción", description = "Actualizar promoción existente")
    @PutMapping("/{promocionId}")
    public ResponseEntity<PromocionResponse> actualizarPromocion(
            @PathVariable UUID promocionId,
            @Valid @RequestBody PromocionCreateRequest request) {
        PromocionResponse response = promocionService.actualizarPromocion(promocionId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener promoción", description = "Consultar información de una promoción")
    @GetMapping("/{promocionId}")
    public ResponseEntity<PromocionResponse> obtenerPromocion(@PathVariable UUID promocionId) {
        PromocionResponse response = promocionService.obtenerPromocion(promocionId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar promociones", description = "Listar todas las promociones")
    @GetMapping
    public ResponseEntity<Page<PromocionResponse>> listarPromociones(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<PromocionResponse> response = promocionService.listarPromociones(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar/desactivar promoción", description = "Cambiar estado de la promoción")
    @PatchMapping("/{promocionId}/estado")
    public ResponseEntity<Void> cambiarEstadoPromocion(
            @PathVariable UUID promocionId,
            @Parameter(description = "Estado activo") @RequestParam boolean activa) {
        promocionService.activarDesactivarPromocion(promocionId, activa);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Promociones aplicables", description = "Obtener promociones aplicables para un carrito")
    @GetMapping("/aplicables")
    public ResponseEntity<List<PromocionResponse>> obtenerPromocionesAplicables(
            @Parameter(description = "IDs de productos") @RequestParam List<UUID> productosCarrito,
            @Parameter(description = "Monto subtotal") @RequestParam BigDecimal montoSubtotal,
            @Parameter(description = "ID de sucursal") @RequestParam(required = false) UUID sucursalId) {
        
        List<PromocionResponse> response = promocionService.obtenerPromocionesAplicables(
            productosCarrito, montoSubtotal, sucursalId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Calcular descuento", description = "Calcular descuento total por promociones")
    @GetMapping("/calcular-descuento")
    public ResponseEntity<BigDecimal> calcularDescuentoPromociones(
            @Parameter(description = "IDs de productos") @RequestParam List<UUID> productosCarrito,
            @Parameter(description = "Monto subtotal") @RequestParam BigDecimal montoSubtotal,
            @Parameter(description = "ID de sucursal") @RequestParam(required = false) UUID sucursalId) {
        
        BigDecimal descuento = promocionService.calcularDescuentoPromociones(
            productosCarrito, montoSubtotal, sucursalId);
        return ResponseEntity.ok(descuento);
    }

    @Operation(summary = "Promociones más efectivas", description = "Obtener promociones más utilizadas")
    @GetMapping("/mas-efectivas")
    public ResponseEntity<List<PromocionResponse>> obtenerPromocionesMasEfectivas(
            @Parameter(description = "Límite de resultados") @RequestParam(defaultValue = "10") int limite) {
        List<PromocionResponse> response = promocionService.obtenerPromocionesMasEfectivas(limite);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Estadísticas de promociones", description = "Obtener estadísticas generales")
    @GetMapping("/estadisticas")
    public ResponseEntity<PromocionService.EstadisticasPromocionesResponse> obtenerEstadisticas() {
        PromocionService.EstadisticasPromocionesResponse response = promocionService.obtenerEstadisticasPromociones();
        return ResponseEntity.ok(response);
    }
}