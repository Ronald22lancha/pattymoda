package com.dpattymoda.repository;

import com.dpattymoda.entity.Producto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de productos
 */
@Repository
public interface ProductoRepository extends JpaRepository<Producto, UUID> {

    Optional<Producto> findByCodigoProductoAndActivoTrue(String codigoProducto);

    Page<Producto> findByActivoTrue(Pageable pageable);

    Page<Producto> findByCategoria_IdAndActivoTrue(UUID categoriaId, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (LOWER(p.nombreProducto) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.descripcion) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(p.marca) LIKE LOWER(CONCAT('%', :termino, '%')))")
    Page<Producto> buscarProductos(@Param("termino") String termino, Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "AND (:categoriaId IS NULL OR p.categoria.id = :categoriaId) " +
           "AND (:marca IS NULL OR LOWER(p.marca) = LOWER(:marca)) " +
           "AND (:precioMin IS NULL OR p.precioBase >= :precioMin) " +
           "AND (:precioMax IS NULL OR p.precioBase <= :precioMax) " +
           "AND (:destacado IS NULL OR p.destacado = :destacado) " +
           "AND (:nuevo IS NULL OR p.nuevo = :nuevo)")
    Page<Producto> buscarConFiltros(@Param("categoriaId") UUID categoriaId,
                                   @Param("marca") String marca,
                                   @Param("precioMin") BigDecimal precioMin,
                                   @Param("precioMax") BigDecimal precioMax,
                                   @Param("destacado") Boolean destacado,
                                   @Param("nuevo") Boolean nuevo,
                                   Pageable pageable);

    List<Producto> findByDestacadoTrueAndActivoTrueOrderByTotalVentasDesc();

    List<Producto> findByNuevoTrueAndActivoTrueOrderByFechaCreacionDesc();

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "ORDER BY p.totalVentas DESC")
    List<Producto> findMasVendidos(Pageable pageable);

    @Query("SELECT p FROM Producto p WHERE p.activo = true " +
           "ORDER BY p.calificacionPromedio DESC, p.totalReseñas DESC")
    List<Producto> findMejorCalificados(Pageable pageable);

    @Query("SELECT DISTINCT p.marca FROM Producto p WHERE p.activo = true ORDER BY p.marca")
    List<String> findMarcasDisponibles();

    @Query("SELECT p FROM Producto p JOIN p.variantes v JOIN v.inventarios i " +
           "WHERE p.activo = true AND i.cantidadDisponible <= i.cantidadMinima")
    List<Producto> findProductosConStockBajo();

    @Query("SELECT COUNT(p) FROM Producto p WHERE p.activo = true")
    long contarProductosActivos();

    @Query("SELECT AVG(p.precioBase) FROM Producto p WHERE p.activo = true")
    BigDecimal obtenerPrecioPromedio();

    boolean existsByCodigoProducto(String codigoProducto);
}