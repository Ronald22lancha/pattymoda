package com.dpattymoda.service;

import com.dpattymoda.dto.request.AgregarAlCarritoRequest;
import com.dpattymoda.dto.request.ActualizarCarritoRequest;
import com.dpattymoda.dto.response.CarritoResponse;

import java.util.UUID;

/**
 * Servicio para gestión del carrito de compras
 */
public interface CarritoService {

    /**
     * Obtener carrito activo del usuario
     */
    CarritoResponse obtenerCarritoUsuario(UUID usuarioId);

    /**
     * Obtener carrito por sesión (usuarios no registrados)
     */
    CarritoResponse obtenerCarritoPorSesion(String sesionId);

    /**
     * Agregar producto al carrito
     */
    CarritoResponse agregarAlCarrito(UUID usuarioId, String sesionId, AgregarAlCarritoRequest request);

    /**
     * Actualizar cantidad de producto en carrito
     */
    CarritoResponse actualizarCarrito(UUID carritoId, ActualizarCarritoRequest request);

    /**
     * Eliminar producto del carrito
     */
    CarritoResponse eliminarDelCarrito(UUID carritoId, UUID varianteId);

    /**
     * Limpiar carrito completo
     */
    void limpiarCarrito(UUID carritoId);

    /**
     * Aplicar cupón de descuento
     */
    CarritoResponse aplicarCupon(UUID carritoId, String codigoCupon);

    /**
     * Remover cupón del carrito
     */
    CarritoResponse removerCupon(UUID carritoId, String codigoCupon);

    /**
     * Calcular costo de envío
     */
    CarritoResponse calcularEnvio(UUID carritoId, UUID direccionEnvioId);

    /**
     * Convertir carrito a pedido
     */
    UUID convertirAPedido(UUID carritoId, UUID direccionEnvioId, String metodoPago);

    /**
     * Limpiar carritos expirados
     */
    void limpiarCarritosExpirados();

    /**
     * Obtener carritos abandonados para remarketing
     */
    void procesarCarritosAbandonados();
}