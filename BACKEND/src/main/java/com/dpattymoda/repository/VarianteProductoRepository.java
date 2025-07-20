package com.dpattymoda.repository;

import com.dpattymoda.entity.VarianteProducto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de variantes de productos
 */
@Repository
public interface VarianteProductoRepository extends JpaRepository<VarianteProducto, UUID> {

    List<VarianteProducto> findByProducto_IdAndActivoTrue(UUID productoId);

    Optional<VarianteProducto> findBySkuAndActivoTrue(String sku);

    @Query("SELECT v FROM VarianteProducto v WHERE v.producto.id = :productoId " +
           "AND v.activo = true " +
           "AND (:talla IS NULL OR v.talla = :talla) " +
           "AND (:color IS NULL OR v.color = :color)")
    List<VarianteProducto> buscarVariantesPorFiltros(@Param("productoId") UUID productoId,
                                                    @Param("talla") String talla,
                                                    @Param("color") String color);

    @Query("SELECT DISTINCT v.talla FROM VarianteProducto v WHERE v.producto.id = :productoId " +
           "AND v.activo = true AND v.talla IS NOT NULL ORDER BY v.talla")
    List<String> obtenerTallasDisponibles(@Param("productoId") UUID productoId);

    @Query("SELECT DISTINCT v.color FROM VarianteProducto v WHERE v.producto.id = :productoId " +
           "AND v.activo = true AND v.color IS NOT NULL ORDER BY v.color")
    List<String> obtenerColoresDisponibles(@Param("productoId") UUID productoId);

    boolean existsBySku(String sku);
}