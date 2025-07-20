package com.dpattymoda.repository;

import com.dpattymoda.entity.UsoCupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para historial de uso de cupones
 */
@Repository
public interface UsoCuponRepository extends JpaRepository<UsoCupon, UUID> {

    List<UsoCupon> findByCupon_IdOrderByFechaUsoDesc(UUID cuponId);

    List<UsoCupon> findByUsuario_IdOrderByFechaUsoDesc(UUID usuarioId);

    List<UsoCupon> findByPedido_Id(UUID pedidoId);

    @Query("SELECT COUNT(uc) FROM UsoCupon uc WHERE uc.cupon.id = :cuponId " +
           "AND uc.usuario.id = :usuarioId")
    long contarUsosPorUsuario(@Param("cuponId") UUID cuponId, @Param("usuarioId") UUID usuarioId);

    @Query("SELECT uc FROM UsoCupon uc WHERE uc.fechaUso BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY uc.fechaUso DESC")
    List<UsoCupon> findUsosPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                                     @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT uc.cupon.codigoCupon, COUNT(uc), SUM(uc.montoDescuento) FROM UsoCupon uc " +
           "WHERE uc.fechaUso >= :fechaInicio " +
           "GROUP BY uc.cupon.codigoCupon " +
           "ORDER BY COUNT(uc) DESC")
    List<Object[]> obtenerEstadisticasCupones(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT SUM(uc.montoDescuento) FROM UsoCupon uc " +
           "WHERE uc.fechaUso >= :fechaInicio")
    java.math.BigDecimal calcularDescuentoTotalPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT AVG(uc.montoDescuento) FROM UsoCupon uc " +
           "WHERE uc.fechaUso >= :fechaInicio")
    java.math.BigDecimal calcularDescuentoPromedio(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT COUNT(uc) FROM UsoCupon uc WHERE uc.fechaUso >= :fechaInicio")
    long contarUsosDesde(@Param("fechaInicio") LocalDateTime fechaInicio);
}