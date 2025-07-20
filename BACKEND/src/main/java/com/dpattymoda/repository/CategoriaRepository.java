package com.dpattymoda.repository;

import com.dpattymoda.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de categorías
 */
@Repository
public interface CategoriaRepository extends JpaRepository<Categoria, UUID> {

    List<Categoria> findByActivaTrueOrderByOrdenVisualizacion();

    List<Categoria> findByCategoriaPadreIsNullAndActivaTrueOrderByOrdenVisualizacion();

    List<Categoria> findByCategoriaPadre_IdAndActivaTrueOrderByOrdenVisualizacion(UUID categoriaPadreId);

    @Query("SELECT c FROM Categoria c WHERE c.activa = true " +
           "AND LOWER(c.nombreCategoria) LIKE LOWER(CONCAT('%', :termino, '%'))")
    List<Categoria> buscarCategorias(@Param("termino") String termino);

    Optional<Categoria> findByNombreCategoriaAndActivaTrue(String nombreCategoria);

    boolean existsByNombreCategoria(String nombreCategoria);
}