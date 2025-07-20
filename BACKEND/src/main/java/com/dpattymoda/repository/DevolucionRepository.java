package com.dpattymoda.repository;

import com.dpattymoda.entity.Devolucion;
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
 * Repositorio para gestión de devoluciones
 */
@Repository
public interface DevolucionRepository extends JpaRepository<Devolucion, UUID> {

    Optional<Devolucion> findByNumeroDevolucion(String numeroDevolucion);

    Page<Devolucion> findByPedido_Usuario_IdOrderByFechaSolicitudDesc(UUID usuarioId, Pageable pageable);

    Page<Devolucion> findByEstadoOrderByFechaSolicitudDesc(String estado, Pageable pageable);

    Page<Devolucion> findAllByOrderByFechaSolicitudDesc(Pageable pageable);

    List<Devolucion> findByEstadoAndFechaSolicitudBefore(String estado, LocalDateTime fechaLimite);

    @Query("SELECT d FROM Devolucion d WHERE d.pedido.id = :pedidoId")
    List<Devolucion> findByPedidoId(@Param("pedidoId") UUID pedidoId);

    @Query("SELECT COUNT(d) FROM Devolucion d WHERE d.estado = :estado")
    long contarPorEstado(@Param("estado") String estado);

    @Query("SELECT d.motivo, COUNT(d) FROM Devolucion d " +
           "WHERE d.fechaSolicitud >= :fechaInicio " +
           "GROUP BY d.motivo ORDER BY COUNT(d) DESC")
    List<Object[]> obtenerMotivosMasFrecuentes(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT COUNT(d) * 100.0 / (SELECT COUNT(p) FROM Pedido p WHERE p.fechaCreacion >= :fechaInicio) " +
           "FROM Devolucion d WHERE d.fechaSolicitud >= :fechaInicio")
    Double calcularPorcentajeDevolucion(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT d FROM Devolucion d WHERE d.aprobadoPor.id = :empleadoId " +
           "ORDER BY d.fechaAprobacion DESC")
    List<Devolucion> findDevolucionesAprobadasPorEmpleado(@Param("empleadoId") UUID empleadoId);

    boolean existsByNumeroDevolucion(String numeroDevolucion);

    @Query("SELECT d FROM Devolucion d WHERE d.estado IN ('aprobada', 'procesando') " +
           "AND d.fechaAprobacion < :fechaLimite")
    List<Devolucion> findDevolucionesPendientesProceso(@Param("fechaLimite") LocalDateTime fechaLimite);
}