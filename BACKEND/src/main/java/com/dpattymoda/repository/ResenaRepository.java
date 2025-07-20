package com.dpattymoda.repository;

import com.dpattymoda.entity.Resena;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de reseñas
 */
@Repository
public interface ResenaRepository extends JpaRepository<Resena, UUID> {

    Page<Resena> findByProducto_IdAndEstadoModeracionOrderByFechaResenaDesc(
        UUID productoId, String estadoModeracion, Pageable pageable);

    Page<Resena> findByUsuario_IdOrderByFechaResenaDesc(UUID usuarioId, Pageable pageable);

    Page<Resena> findByEstadoModeracionOrderByFechaResenaDesc(String estadoModeracion, Pageable pageable);

    Optional<Resena> findByProducto_IdAndUsuario_Id(UUID productoId, UUID usuarioId);

    @Query("SELECT r FROM Resena r WHERE r.producto.id = :productoId " +
           "AND r.estadoModeracion = 'aprobada' " +
           "ORDER BY r.utilidadPositiva DESC, r.fechaResena DESC")
    List<Resena> findResenasMasUtiles(@Param("productoId") UUID productoId, Pageable pageable);

    @Query("SELECT r FROM Resena r WHERE r.producto.id = :productoId " +
           "AND r.estadoModeracion = 'aprobada' " +
           "AND r.calificacion >= :calificacionMinima " +
           "ORDER BY r.fechaResena DESC")
    List<Resena> findResenasPositivas(@Param("productoId") UUID productoId, 
                                     @Param("calificacionMinima") Integer calificacionMinima);

    @Query("SELECT r FROM Resena r WHERE r.producto.id = :productoId " +
           "AND r.estadoModeracion = 'aprobada' " +
           "AND r.verificada = true " +
           "ORDER BY r.fechaResena DESC")
    List<Resena> findResenasVerificadas(@Param("productoId") UUID productoId);

    @Query("SELECT AVG(r.calificacion), COUNT(r) FROM Resena r " +
           "WHERE r.producto.id = :productoId AND r.estadoModeracion = 'aprobada'")
    Object[] obtenerEstadisticasProducto(@Param("productoId") UUID productoId);

    @Query("SELECT r.calificacion, COUNT(r) FROM Resena r " +
           "WHERE r.producto.id = :productoId AND r.estadoModeracion = 'aprobada' " +
           "GROUP BY r.calificacion ORDER BY r.calificacion")
    List<Object[]> obtenerDistribucionCalificaciones(@Param("productoId") UUID productoId);

    @Query("SELECT COUNT(r) FROM Resena r WHERE r.usuario.id = :usuarioId " +
           "AND r.producto.id = :productoId")
    long contarResenasPorUsuarioYProducto(@Param("usuarioId") UUID usuarioId, 
                                         @Param("productoId") UUID productoId);

    @Query("SELECT r FROM Resena r WHERE r.reportes >= :minimoReportes " +
           "AND r.estadoModeracion = 'aprobada'")
    List<Resena> findResenasConReportes(@Param("minimoReportes") Integer minimoReportes);

    @Query("SELECT r FROM Resena r WHERE r.fechaResena >= :fechaInicio " +
           "AND r.estadoModeracion = 'pendiente'")
    List<Resena> findResenasPendientesModeracin(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT COUNT(r) FROM Resena r WHERE r.estadoModeracion = :estado")
    long contarPorEstadoModeracion(@Param("estado") String estadoModeracion);

    @Query("SELECT r FROM Resena r WHERE r.imagenes IS NOT NULL " +
           "AND r.imagenes != '[]' AND r.estadoModeracion = 'aprobada'")
    List<Resena> findResenasConImagenes();

    @Query("SELECT r FROM Resena r WHERE r.pedido.id = :pedidoId")
    List<Resena> findByPedidoId(@Param("pedidoId") UUID pedidoId);

    @Query("SELECT r.producto.id, AVG(r.calificacion) FROM Resena r " +
           "WHERE r.estadoModeracion = 'aprobada' " +
           "GROUP BY r.producto.id " +
           "HAVING AVG(r.calificacion) >= :calificacionMinima")
    List<Object[]> findProductosMejorCalificados(@Param("calificacionMinima") Double calificacionMinima);

    boolean existsByProducto_IdAndUsuario_Id(UUID productoId, UUID usuarioId);
}