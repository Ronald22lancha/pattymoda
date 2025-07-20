package com.dpattymoda.repository;

import com.dpattymoda.entity.Promocion;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para gestión de promociones
 */
@Repository
public interface PromocionRepository extends JpaRepository<Promocion, UUID> {

    Page<Promocion> findByActivaTrueOrderByPrioridadDescFechaCreacionDesc(Pageable pageable);

    @Query("SELECT p FROM Promocion p WHERE p.activa = true " +
           "AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora " +
           "ORDER BY p.prioridad DESC")
    List<Promocion> findPromocionesVigentes(@Param("ahora") LocalDateTime ahora);

    @Query("SELECT p FROM Promocion p WHERE p.activa = true " +
           "AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora " +
           "AND (p.limiteUsos IS NULL OR p.usosActuales < p.limiteUsos) " +
           "AND (:sucursalId IS NULL OR :sucursalId = ANY(p.sucursalesIncluidas) OR p.sucursalesIncluidas IS NULL) " +
           "ORDER BY p.prioridad DESC")
    List<Promocion> findPromocionesAplicables(@Param("ahora") LocalDateTime ahora, 
                                             @Param("sucursalId") UUID sucursalId);

    @Query("SELECT p FROM Promocion p WHERE p.activa = true " +
           "AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora " +
           "AND (:categoriaId IS NULL OR :categoriaId = ANY(p.categoriasIncluidas) OR p.categoriasIncluidas IS NULL)")
    List<Promocion> findPromocionesPorCategoria(@Param("ahora") LocalDateTime ahora,
                                               @Param("categoriaId") UUID categoriaId);

    @Query("SELECT p FROM Promocion p WHERE p.activa = true " +
           "AND p.fechaInicio <= :ahora AND p.fechaFin >= :ahora " +
           "AND (:productoId IS NULL OR :productoId = ANY(p.productosIncluidos) OR p.productosIncluidos IS NULL)")
    List<Promocion> findPromocionesPorProducto(@Param("ahora") LocalDateTime ahora,
                                              @Param("productoId") UUID productoId);

    @Query("SELECT p FROM Promocion p WHERE p.fechaFin < :fecha")
    List<Promocion> findPromocionesExpiradas(@Param("fecha") LocalDateTime fecha);

    @Query("SELECT p FROM Promocion p WHERE p.limiteUsos IS NOT NULL " +
           "AND p.usosActuales >= p.limiteUsos")
    List<Promocion> findPromocionesAgotadas();

    @Query("SELECT p FROM Promocion p WHERE p.tipoPromocion = :tipo " +
           "AND p.activa = true")
    List<Promocion> findByTipoPromocion(@Param("tipo") String tipoPromocion);

    @Query("SELECT p FROM Promocion p ORDER BY p.usosActuales DESC")
    List<Promocion> findPromocionesMasUsadas(Pageable pageable);

    @Query("SELECT COUNT(p) FROM Promocion p WHERE p.activa = true")
    long contarPromocionesActivas();

    @Query("SELECT p FROM Promocion p WHERE p.activa = true " +
           "AND p.aplicableOnline = :online " +
           "AND p.aplicablePresencial = :presencial")
    List<Promocion> findByTipoVenta(@Param("online") boolean aplicableOnline, 
                                   @Param("presencial") boolean aplicablePresencial);
}