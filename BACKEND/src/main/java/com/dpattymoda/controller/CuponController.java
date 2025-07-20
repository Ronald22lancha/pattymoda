package com.dpattymoda.controller;

import com.dpattymoda.dto.request.CuponCreateRequest;
import com.dpattymoda.dto.response.CuponResponse;
import com.dpattymoda.dto.response.ValidacionCuponResponse;
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
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controlador para gestión de cupones de descuento
 */
@Tag(name = "Cupones", description = "Gestión de cupones de descuento")
@RestController
@RequestMapping("/api/cupones")
@RequiredArgsConstructor
public class CuponController {

    private final PromocionService promocionService;

    @Operation(summary = "Crear cupón", description = "Crear nuevo cupón de descuento")
    @PostMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<CuponResponse> crearCupon(@Valid @RequestBody CuponCreateRequest request) {
        CuponResponse response = promocionService.crearCupon(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Actualizar cupón", description = "Actualizar cupón existente")
    @PutMapping("/{cuponId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<CuponResponse> actualizarCupon(
            @PathVariable UUID cuponId,
            @Valid @RequestBody CuponCreateRequest request) {
        CuponResponse response = promocionService.actualizarCupon(cuponId, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Obtener cupón", description = "Consultar información de un cupón")
    @GetMapping("/{cuponId}")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<CuponResponse> obtenerCupon(@PathVariable UUID cuponId) {
        CuponResponse response = promocionService.obtenerCupon(cuponId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Listar cupones", description = "Listar todos los cupones")
    @GetMapping
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Page<CuponResponse>> listarCupones(
            @PageableDefault(size = 20) Pageable pageable) {
        Page<CuponResponse> response = promocionService.listarCupones(pageable);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Activar/desactivar cupón", description = "Cambiar estado del cupón")
    @PatchMapping("/{cuponId}/estado")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<Void> cambiarEstadoCupon(
            @PathVariable UUID cuponId,
            @Parameter(description = "Estado activo") @RequestParam boolean activo) {
        promocionService.activarDesactivarCupon(cuponId, activo);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Eliminar cupón", description = "Eliminar cupón del sistema")
    @DeleteMapping("/{cuponId}")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> eliminarCupon(@PathVariable UUID cuponId) {
        promocionService.eliminarCupon(cuponId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Validar cupón", description = "Validar si un cupón puede ser usado")
    @PostMapping("/validar")
    public ResponseEntity<ValidacionCuponResponse> validarCupon(
            @Parameter(description = "Código del cupón") @RequestParam String codigoCupon,
            Authentication authentication,
            @Parameter(description = "Monto de la compra") @RequestParam BigDecimal montoCompra,
            @Parameter(description = "IDs de productos en el carrito") @RequestParam List<UUID> productosCarrito) {
        
        UUID usuarioId = obtenerUsuarioId(authentication);
        ValidacionCuponResponse response = promocionService.validarCupon(codigoCupon, usuarioId, montoCompra, productosCarrito);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Aplicar cupón al carrito", description = "Aplicar cupón de descuento al carrito")
    @PostMapping("/aplicar/{carritoId}")
    public ResponseEntity<Void> aplicarCupon(
            @PathVariable UUID carritoId,
            @Parameter(description = "Código del cupón") @RequestParam String codigoCupon) {
        promocionService.aplicarCupon(carritoId, codigoCupon);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remover cupón del carrito", description = "Quitar cupón aplicado del carrito")
    @DeleteMapping("/remover/{carritoId}")
    public ResponseEntity<Void> removerCupon(
            @PathVariable UUID carritoId,
            @Parameter(description = "Código del cupón") @RequestParam String codigoCupon) {
        promocionService.removerCupon(carritoId, codigoCupon);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Cupones más usados", description = "Obtener cupones más utilizados")
    @GetMapping("/mas-usados")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<List<CuponResponse>> obtenerCuponesMasUsados(
            @Parameter(description = "Límite de resultados") @RequestParam(defaultValue = "10") int limite) {
        List<CuponResponse> response = promocionService.obtenerCuponesMasUsados(limite);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Estadísticas de cupones", description = "Obtener estadísticas generales")
    @GetMapping("/estadisticas")
    @PreAuthorize("hasRole('ADMINISTRADOR') or hasRole('EMPLEADO')")
    public ResponseEntity<PromocionService.EstadisticasPromocionesResponse> obtenerEstadisticas() {
        PromocionService.EstadisticasPromocionesResponse response = promocionService.obtenerEstadisticasPromociones();
        return ResponseEntity.ok(response);
    }

    // Método utilitario
    private UUID obtenerUsuarioId(Authentication authentication) {
        return UUID.randomUUID(); // Placeholder
    }
}