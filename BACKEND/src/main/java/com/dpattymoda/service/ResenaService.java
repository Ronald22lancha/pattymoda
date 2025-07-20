package com.dpattymoda.service;

import com.dpattymoda.dto.request.ResenaCreateRequest;
import com.dpattymoda.dto.request.ResenaUpdateRequest;
import com.dpattymoda.dto.response.ResenaResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de reseñas de productos
 */
public interface ResenaService {

    /**
     * Crear nueva reseña
     */
    ResenaResponse crearResena(UUID usuarioId, ResenaCreateRequest request);

    /**
     * Actualizar reseña existente
     */
    ResenaResponse actualizarResena(UUID resenaId, UUID usuarioId, ResenaUpdateRequest request);

    /**
     * Obtener reseña por ID
     */
    ResenaResponse obtenerResena(UUID resenaId);

    /**
     * Listar reseñas de un producto
     */
    Page<ResenaResponse> listarResenasPorProducto(UUID productoId, Pageable pageable);

    /**
     * Listar reseñas del usuario
     */
    Page<ResenaResponse> listarResenasUsuario(UUID usuarioId, Pageable pageable);

    /**
     * Listar reseñas pendientes de moderación
     */
    Page<ResenaResponse> listarResenasPendientes(Pageable pageable);

    /**
     * Moderar reseña (aprobar/rechazar)
     */
    ResenaResponse moderarResena(UUID resenaId, String decision, String motivo);

    /**
     * Marcar reseña como útil
     */
    void marcarComoUtil(UUID resenaId, UUID usuarioId, boolean util);

    /**
     * Reportar reseña inapropiada
     */
    void reportarResena(UUID resenaId, UUID usuarioId, String motivo);

    /**
     * Eliminar reseña
     */
    void eliminarResena(UUID resenaId, UUID usuarioId);

    /**
     * Obtener estadísticas de reseñas por producto
     */
    EstadisticasResenasResponse obtenerEstadisticasProducto(UUID productoId);

    /**
     * Verificar si usuario puede reseñar producto
     */
    boolean puedeResenarProducto(UUID usuarioId, UUID productoId);

    // DTO para estadísticas
    record EstadisticasResenasResponse(
        Double calificacionPromedio,
        Integer totalResenas,
        Integer[] distribucionCalificaciones,
        Integer resenasVerificadas,
        Integer resenasConImagenes
    ) {}
}