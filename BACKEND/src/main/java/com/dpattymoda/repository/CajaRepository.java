package com.dpattymoda.repository;

import com.dpattymoda.entity.Caja;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de cajas registradoras
 */
@Repository
public interface CajaRepository extends JpaRepository<Caja, UUID> {

    List<Caja> findBySucursal_IdAndActivaTrue(UUID sucursalId);

    Optional<Caja> findByNumeroCajaAndSucursal_Id(String numeroCaja, UUID sucursalId);

    @Query("SELECT c FROM Caja c WHERE c.activa = true " +
           "AND EXISTS (SELECT 1 FROM TurnoCaja t WHERE t.caja = c AND t.estado = 'abierto')")
    List<Caja> findCajasConTurnoAbierto();

    @Query("SELECT c FROM Caja c WHERE c.sucursal.id = :sucursalId " +
           "AND c.activa = true " +
           "AND NOT EXISTS (SELECT 1 FROM TurnoCaja t WHERE t.caja = c AND t.estado = 'abierto')")
    List<Caja> findCajasDisponibles(@Param("sucursalId") UUID sucursalId);

    boolean existsByNumeroCajaAndSucursal_Id(String numeroCaja, UUID sucursalId);
}