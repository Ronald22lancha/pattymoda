package com.dpattymoda.repository;

import com.dpattymoda.entity.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de envíos
 */
@Repository
public interface EnvioRepository extends JpaRepository<Envio, UUID> {

    Optional<Envio> findByNumeroGuia(String numeroGuia);

    Optional<Envio> findByPedido_Id(UUID pedidoId);

    List<Envio> findByEstadoOrderByFechaCreacionDesc(String estado);

    @Query("SELECT e FROM Envio e WHERE e.estado IN ('preparando', 'en_camino') " +
           "ORDER BY e.fechaCreacion DESC")
    List<Envio> findEnviosPendientes();

    @Query("SELECT e FROM Envio e WHERE e.fechaEntregaEstimada < :fecha " +
           "AND e.estado = 'en_camino'")
    List<Envio> findEnviosRetrasados(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT e FROM Envio e WHERE e.transportista = :transportista " +
           "AND e.fechaCreacion BETWEEN :fechaInicio AND :fechaFin")
    List<Envio> findByTransportistaYPeriodo(@Param("transportista") String transportista,
                                           @Param("fechaInicio") LocalDateTime fechaInicio,
                                           @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT e.transportista, COUNT(e), AVG(EXTRACT(EPOCH FROM (e.fechaEntregaReal - e.fechaDespacho))/86400) " +
           "FROM Envio e WHERE e.estado = 'entregado' " +
           "AND e.fechaEntregaReal >= :fechaInicio " +
           "GROUP BY e.transportista")
    List<Object[]> obtenerEstadisticasTransportistas(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT COUNT(e) FROM Envio e WHERE e.estado = 'entregado' " +
           "AND e.fechaEntregaReal <= e.fechaEntregaEstimada")
    long contarEntregasAPuntual();

    @Query("SELECT e FROM Envio e WHERE e.intentosEntrega >= e.maxIntentosEntrega " +
           "AND e.estado != 'devuelto'")
    List<Envio> findEnviosConMaximosIntentos();

    boolean existsByNumeroGuia(String numeroGuia);
}