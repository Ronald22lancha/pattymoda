package com.dpattymoda.repository;

import com.dpattymoda.entity.Pago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de pagos
 */
@Repository
public interface PagoRepository extends JpaRepository<Pago, UUID> {

    List<Pago> findByPedido_Id(UUID pedidoId);

    Optional<Pago> findByReferenciaExterna(String referenciaExterna);

    List<Pago> findByEstadoAndFechaVencimientoBefore(String estado, LocalDateTime fecha);

    @Query("SELECT p FROM Pago p WHERE p.estado = 'pendiente' " +
           "AND p.fechaVencimiento < :fecha")
    List<Pago> findPagosExpirados(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT p.metodoPago, COUNT(p), SUM(p.monto) FROM Pago p " +
           "WHERE p.estado = 'procesado' " +
           "AND p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
           "GROUP BY p.metodoPago")
    List<Object[]> obtenerEstadisticasPorMetodoPago(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                   @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT SUM(p.monto) FROM Pago p WHERE p.estado = 'procesado' " +
           "AND p.fechaCreacion >= :fechaInicio")
    BigDecimal calcularTotalPagosDesde(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT COUNT(p) FROM Pago p WHERE p.estado = 'fallido' " +
           "AND p.fechaCreacion >= :fechaInicio")
    long contarPagosFallidosDesde(@Param("fechaInicio") LocalDateTime fechaInicio);

    boolean existsByReferenciaExterna(String referenciaExterna);
}