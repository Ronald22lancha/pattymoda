package com.dpattymoda.repository;

import com.dpattymoda.entity.Sucursal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de sucursales
 */
@Repository
public interface SucursalRepository extends JpaRepository<Sucursal, UUID> {

    List<Sucursal> findByActivaTrueOrderByNombreSucursal();

    Optional<Sucursal> findByEsPrincipalTrueAndActivaTrue();

    @Query("SELECT s FROM Sucursal s WHERE s.activa = true " +
           "AND LOWER(s.nombreSucursal) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Sucursal> buscarSucursalesActivas(String termino);

    boolean existsByNombreSucursal(String nombreSucursal);

    @Query("SELECT COUNT(s) FROM Sucursal s WHERE s.activa = true")
    long contarSucursalesActivas();
}