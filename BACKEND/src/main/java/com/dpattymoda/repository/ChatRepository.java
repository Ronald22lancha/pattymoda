package com.dpattymoda.repository;

import com.dpattymoda.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para gestión de chats
 */
@Repository
public interface ChatRepository extends JpaRepository<Chat, UUID> {

    Page<Chat> findByUsuario_IdOrderByFechaUltimoMensajeDesc(UUID usuarioId, Pageable pageable);

    Page<Chat> findByEmpleadoAsignado_IdOrderByFechaUltimoMensajeDesc(UUID empleadoId, Pageable pageable);

    List<Chat> findByEstadoAndEmpleadoAsignadoIsNullOrderByFechaPrimerMensajeAsc(String estado);

    @Query("SELECT c FROM Chat c WHERE c.estado = 'abierto' " +
           "AND c.fechaUltimoMensaje < :fechaLimite")
    List<Chat> findChatsInactivos(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT COUNT(c) FROM Chat c WHERE c.empleadoAsignado.id = :empleadoId " +
           "AND c.estado IN ('abierto', 'en_progreso')")
    long contarChatsActivosPorEmpleado(@Param("empleadoId") UUID empleadoId);

    @Query("SELECT c FROM Chat c WHERE c.estado IN ('abierto', 'en_progreso') " +
           "ORDER BY c.prioridad DESC, c.fechaPrimerMensaje ASC")
    List<Chat> findChatsActivos();

    @Query("SELECT c.categoria, COUNT(c) FROM Chat c " +
           "WHERE c.fechaPrimerMensaje >= :fechaInicio " +
           "GROUP BY c.categoria")
    List<Object[]> obtenerEstadisticasPorCategoria(@Param("fechaInicio") LocalDateTime fechaInicio);
}