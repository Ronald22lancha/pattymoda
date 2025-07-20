package com.dpattymoda.repository;

import com.dpattymoda.entity.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de roles
 */
@Repository
public interface RolRepository extends JpaRepository<Rol, UUID> {

    Optional<Rol> findByNombreRolAndActivoTrue(String nombreRol);

    List<Rol> findByActivoTrueOrderByNombreRol();

    boolean existsByNombreRol(String nombreRol);
}