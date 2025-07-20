package com.dpattymoda.repository;

import com.dpattymoda.entity.Cupon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de cupones
 */
@Repository
public interface CuponRepository extends JpaRepository<Cupon, UUID> {

    Optional<Cupon> findByCodigoCuponAndActivoTrue(String codigoCupon);

    Page<Cupon> findByActivoTrueOrderByFechaCreacionDesc(Pageable pageable);

    @Query("SELECT c FROM Cupon c WHERE c.activo = true " +
           "AND c.fechaInicio <= :ahora AND c.fechaFin >= :ahora")
    List<Cupon> findCuponesVigentes(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT c FROM Cupon c WHERE c.activo = true " +
           "AND c.fechaInicio <= :ahora AND c.fechaFin >= :ahora " +
           "AND (c.usosMaximos IS NULL OR c.usosActuales < c.usosMaximos)")
    List<Cupon> findCuponesDisponibles(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT c FROM Cupon c WHERE c.fechaFin < :fecha")
    List<Cupon> findCuponesExpirados(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT c FROM Cupon c WHERE c.usosMaximos IS NOT NULL " +
           "AND c.usosActuales >= c.usosMaximos")
    List<Cupon> findCuponesAgotados();

    @Query("SELECT c FROM Cupon c WHERE :usuarioId = ANY(c.usuariosIncluidos)")
    List<Cupon> findCuponesParaUsuario(@Param("usuarioId") UUID usuarioId);

    @Query("SELECT c FROM Cupon c WHERE c.activo = true " +
           "AND (:categoriaId IS NULL OR :categoriaId = ANY(c.categoriasIncluidas) OR c.categoriasIncluidas IS NULL)")
    List<Cupon> findCuponesPorCategoria(@Param("categoriaId") UUID categoriaId);

    @Query("SELECT c FROM Cupon c WHERE c.activo = true " +
           "AND (:productoId IS NULL OR :productoId = ANY(c.productosIncluidos) OR c.productosIncluidos IS NULL)")
    List<Cupon> findCuponesPorProducto(@Param("productoId") UUID productoId);

    @Query("SELECT c FROM Cupon c ORDER BY c.usosActuales DESC")
    List<Cupon> findCuponesMasUsados(Pageable pageable);

    @Query("SELECT COUNT(c) FROM Cupon c WHERE c.activo = true")
    long contarCuponesActivos();

    @Query("SELECT SUM(uc.montoDescuento) FROM UsoCupon uc " +
           "WHERE uc.fechaUso >= :fechaInicio")
    java.math.BigDecimal calcularDescuentoTotalOtorgado(@Param("fechaInicio") LocalDateTime fechaInicio);

    boolean existsByCodigoCupon(String codigoCupon);
}