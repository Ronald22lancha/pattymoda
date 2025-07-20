package com.dpattymoda.repository;

import com.dpattymoda.entity.MovimientoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para gestión de movimientos de caja
 */
@Repository
public interface MovimientoCajaRepository extends JpaRepository<MovimientoCaja, UUID> {

    List<MovimientoCaja> findByTurnoCaja_IdOrderByFechaMovimientoDesc(UUID turnoId);

    List<MovimientoCaja> findByTurnoCaja_IdAndTipoMovimiento(UUID turnoId, String tipoMovimiento);

    @Query("SELECT m FROM MovimientoCaja m WHERE m.turnoCaja.caja.sucursal.id = :sucursalId " +
           "AND m.fechaMovimiento BETWEEN :fechaInicio AND :fechaFin " +
           "ORDER BY m.fechaMovimiento DESC")
    List<MovimientoCaja> findMovimientosPorSucursalYPeriodo(@Param("sucursalId") UUID sucursalId,
                                                           @Param("fechaInicio") LocalDateTime fechaInicio,
                                                           @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT SUM(m.monto) FROM MovimientoCaja m WHERE m.turnoCaja.id = :turnoId " +
           "AND m.tipoMovimiento = :tipoMovimiento")
    BigDecimal calcularTotalPorTipoMovimiento(@Param("turnoId") UUID turnoId,
                                             @Param("tipoMovimiento") String tipoMovimiento);

    @Query("SELECT m.metodoPago, SUM(m.monto) FROM MovimientoCaja m " +
           "WHERE m.turnoCaja.id = :turnoId AND m.tipoMovimiento = 'venta' " +
           "GROUP BY m.metodoPago")
    List<Object[]> obtenerVentasPorMetodoPago(@Param("turnoId") UUID turnoId);
}