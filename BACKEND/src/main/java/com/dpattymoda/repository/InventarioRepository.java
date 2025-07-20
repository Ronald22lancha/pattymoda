package com.dpattymoda.repository;

import com.dpattymoda.entity.Inventario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de inventario
 */
@Repository
public interface InventarioRepository extends JpaRepository<Inventario, UUID> {

    Optional<Inventario> findByVariante_IdAndSucursal_Id(UUID varianteId, UUID sucursalId);

    List<Inventario> findByVariante_Id(UUID varianteId);

    List<Inventario> findBySucursal_Id(UUID sucursalId);

    @Query("SELECT i FROM Inventario i WHERE i.cantidadDisponible <= i.cantidadMinima")
    List<Inventario> findInventarioConStockBajo();

    @Query("SELECT SUM(i.cantidadDisponible) FROM Inventario i WHERE i.variante.id = :varianteId")
    Integer obtenerStockTotalPorVariante(@Param("varianteId") UUID varianteId);

    @Query("SELECT SUM(i.cantidadDisponible - i.cantidadReservada) FROM Inventario i " +
           "WHERE i.variante.id = :varianteId")
    Integer obtenerStockDisponiblePorVariante(@Param("varianteId") UUID varianteId);

    @Query("SELECT i FROM Inventario i WHERE i.sucursal.id = :sucursalId " +
           "AND i.cantidadDisponible <= i.cantidadMinima")
    List<Inventario> findStockBajoPorSucursal(@Param("sucursalId") UUID sucursalId);
}