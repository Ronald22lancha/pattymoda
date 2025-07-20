package com.dpattymoda.repository;

import com.dpattymoda.entity.TurnoCaja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de turnos de caja
 */
@Repository
public interface TurnoCajaRepository extends JpaRepository<TurnoCaja, UUID> {

    Optional<TurnoCaja> findByCaja_IdAndEstado(UUID cajaId, String estado);

    List<TurnoCaja> findByCajero_IdOrderByFechaAperturaDesc(UUID cajeroId);

    List<TurnoCaja> findByCaja_IdOrderByFechaAperturaDesc(UUID cajaId);

    @Query("SELECT t FROM TurnoCaja t WHERE t.caja.sucursal.id = :sucursalId " +
           "AND t.fechaApertura >= :fechaInicio " +
           "ORDER BY t.fechaApertura DESC")
    List<TurnoCaja> findTurnosPorSucursalYFecha(@Param("sucursalId") UUID sucursalId,
                                               @Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT t FROM TurnoCaja t WHERE t.estado = 'abierto'")
    List<TurnoCaja> findTurnosAbiertos();

    @Query("SELECT t FROM TurnoCaja t WHERE t.diferencia IS NOT NULL " +
           "AND ABS(t.diferencia) > :montoMinimo")
    List<TurnoCaja> findTurnosConDescuadre(@Param("montoMinimo") java.math.BigDecimal montoMinimo);

    @Query("SELECT COUNT(t) FROM TurnoCaja t WHERE t.cajero.id = :cajeroId " +
           "AND t.fechaApertura >= :fechaInicio")
    long contarTurnosPorCajero(@Param("cajeroId") UUID cajeroId, 
                              @Param("fechaInicio") LocalDateTime fechaInicio);
}