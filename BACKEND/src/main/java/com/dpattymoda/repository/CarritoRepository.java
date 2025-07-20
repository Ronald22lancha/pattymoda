package com.dpattymoda.repository;

import com.dpattymoda.entity.Carrito;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de carritos
 */
@Repository
public interface CarritoRepository extends JpaRepository<Carrito, UUID> {

    Optional<Carrito> findByUsuario_IdAndEstado(UUID usuarioId, String estado);

    Optional<Carrito> findBySesionIdAndEstado(String sesionId, String estado);

    @Query("SELECT c FROM Carrito c WHERE c.fechaExpiracion < :fecha AND c.estado = 'activo'")
    List<Carrito> findCarritosExpirados(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT c FROM Carrito c WHERE c.fechaActualizacion < :fechaLimite " +
           "AND c.estado = 'activo'")
    List<Carrito> findCarritosAbandonados(@Param("fechaLimite") LocalDateTime fechaLimite);
}