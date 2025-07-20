package com.dpattymoda.mapper;

import com.dpattymoda.dto.response.PromocionResponse;
import com.dpattymoda.entity.Promocion;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Mapper para conversión entre Promocion y DTOs
 */
@Mapper(componentModel = "spring")
public abstract class PromocionMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(target = "condiciones", expression = "java(jsonToMap(promocion.getCondiciones()))")
    @Mapping(target = "descuento", expression = "java(jsonToMap(promocion.getDescuento()))")
    @Mapping(target = "diasSemana", expression = "java(arrayToList(promocion.getDiasSemana()))")
    @Mapping(target = "sucursalesIncluidas", expression = "java(arrayToList(promocion.getSucursalesIncluidas()))")
    @Mapping(target = "categoriasIncluidas", expression = "java(arrayToList(promocion.getCategoriasIncluidas()))")
    @Mapping(target = "productosIncluidos", expression = "java(arrayToList(promocion.getProductosIncluidos()))")
    @Mapping(target = "vigente", expression = "java(promocion.estaVigente())")
    @Mapping(target = "puedeAplicarse", expression = "java(promocion.puedeAplicarse())")
    @Mapping(target = "agotada", expression = "java(promocion.estaAgotada())")
    @Mapping(target = "usosRestantes", expression = "java(promocion.getUsosRestantes())")
    public abstract PromocionResponse toResponse(Promocion promocion);

    protected <T> List<T> arrayToList(T[] array) {
        return array != null ? Arrays.asList(array) : null;
    }

    @SuppressWarnings("unchecked")
    protected Map<String, Object> jsonToMap(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }
}