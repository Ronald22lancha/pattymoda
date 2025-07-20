package com.dpattymoda.repository;

import com.dpattymoda.entity.Notificacion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para gestión de notificaciones
 */
@Repository
public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    Page<Notificacion> findByUsuario_IdOrderByFechaCreacionDesc(UUID usuarioId, Pageable pageable);

    @Query("SELECT COUNT(n) FROM Notificacion n WHERE n.usuario.id = :usuarioId AND n.leida = false")
    Integer contarNoLeidasPorUsuario(@Param("usuarioId") UUID usuarioId);

    List<Notificacion> findByUsuario_IdAndLeidaFalseOrderByFechaCreacionDesc(UUID usuarioId);

    @Query("SELECT n FROM Notificacion n WHERE n.enviada = false AND n.intentosEnvio < 3 " +
           "AND (n.expiraEn IS NULL OR n.expiraEn > :ahora)")
    List<Notificacion> findNotificacionesPendientesEnvio(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT n FROM Notificacion n WHERE n.expiraEn < :fecha")
    List<Notificacion> findNotificacionesExpiradas(@Param("fecha") LocalDateTime fecha);

    @Modifying
    @Query("UPDATE Notificacion n SET n.leida = true, n.fechaLectura = :fecha " +
           "WHERE n.usuario.id = :usuarioId AND n.leida = false")
    void marcarTodasComoLeidasPorUsuario(@Param("usuarioId") UUID usuarioId, @Param("fecha") LocalDateTime fecha);

    @Query("SELECT n FROM Notificacion n WHERE n.tipo = :tipo " +
           "AND n.fechaCreacion >= :fechaInicio")
    List<Notificacion> findByTipoYFecha(@Param("tipo") String tipo, @Param("fechaInicio") LocalDateTime fechaInicio);

    void deleteByUsuario_IdAndId(UUID usuarioId, UUID notificacionId);

    @Query("SELECT n.tipo, COUNT(n) FROM Notificacion n " +
           "WHERE n.fechaCreacion >= :fechaInicio " +
           "GROUP BY n.tipo")
    List<Object[]> obtenerEstadisticasPorTipo(@Param("fechaInicio") LocalDateTime fechaInicio);
}