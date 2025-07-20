package com.dpattymoda.repository;

import com.dpattymoda.entity.Comprobante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de comprobantes electrónicos
 */
@Repository
public interface ComprobanteRepository extends JpaRepository<Comprobante, UUID> {

    Optional<Comprobante> findByTipoComprobanteAndSerieAndNumero(String tipoComprobante, String serie, Integer numero);

    Optional<Comprobante> findByPedido_Id(UUID pedidoId);

    List<Comprobante> findByEstadoSunatOrderByFechaCreacionDesc(String estadoSunat);

    @Query("SELECT c FROM Comprobante c WHERE DATE(c.fechaEmision) = :fecha " +
           "ORDER BY c.tipoComprobante, c.serie, c.numero")
    List<Comprobante> findByFechaEmision(@Param("fecha") LocalDate fecha);

    @Query("SELECT c FROM Comprobante c WHERE c.estadoSunat = 'pendiente' " +
           "AND c.fechaCreacion < :fechaLimite")
    List<Comprobante> findComprobantesPendientesSunat(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT MAX(c.numero) FROM Comprobante c WHERE c.tipoComprobante = :tipo AND c.serie = :serie")
    Integer findMaxNumeroByTipoAndSerie(@Param("tipo") String tipoComprobante, @Param("serie") String serie);

    @Query("SELECT c FROM Comprobante c WHERE c.anulado = false " +
           "AND DATE(c.fechaEmision) = :fecha " +
           "ORDER BY c.fechaCreacion DESC")
    List<Comprobante> findComprobantesValidosPorFecha(@Param("fecha") LocalDate fecha);

    @Query("SELECT c.tipoComprobante, COUNT(c), SUM(c.total) FROM Comprobante c " +
           "WHERE c.fechaEmision BETWEEN :fechaInicio AND :fechaFin " +
           "AND c.anulado = false " +
           "GROUP BY c.tipoComprobante")
    List<Object[]> obtenerEstadisticasPorTipo(@Param("fechaInicio") LocalDateTime fechaInicio,
                                             @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(c) FROM Comprobante c WHERE c.estadoSunat = 'aceptado' " +
           "AND c.fechaEmision >= :fechaInicio")
    long contarComprobantesAceptados(@Param("fechaInicio") LocalDateTime fechaInicio);

    boolean existsByTipoComprobanteAndSerieAndNumero(String tipoComprobante, String serie, Integer numero);
}