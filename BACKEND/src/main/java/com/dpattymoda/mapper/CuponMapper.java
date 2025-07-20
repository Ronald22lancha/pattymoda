package com.dpattymoda.mapper;

import com.dpattymoda.dto.response.CuponResponse;
import com.dpattymoda.entity.Cupon;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Arrays;
import java.util.List;

/**
 * Mapper para conversión entre Cupon y DTOs
 */
@Mapper(componentModel = "spring")
public interface CuponMapper {

    @Mapping(target = "categoriasIncluidas", expression = "java(arrayToList(cupon.getCategoriasIncluidas()))")
    @Mapping(target = "productosIncluidos", expression = "java(arrayToList(cupon.getProductosIncluidos()))")
    @Mapping(target = "usuariosIncluidos", expression = "java(arrayToList(cupon.getUsuariosIncluidos()))")
    @Mapping(target = "vigente", expression = "java(cupon.estaVigente())")
    @Mapping(target = "puedeUsarse", expression = "java(cupon.puedeUsarse())")
    @Mapping(target = "agotado", expression = "java(cupon.estaAgotado())")
    @Mapping(target = "usosRestantes", expression = "java(cupon.getUsosRestantes())")
    @Mapping(target = "porcentajeUso", expression = "java(cupon.getPorcentajeUso())")
    CuponResponse toResponse(Cupon cupon);

    default <T> List<T> arrayToList(T[] array) {
        return array != null ? Arrays.asList(array) : null;
    }
}