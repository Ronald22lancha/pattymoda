package com.dpattymoda.mapper;

import com.dpattymoda.dto.response.ResenaResponse;
import com.dpattymoda.entity.Resena;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

/**
 * Mapper para conversión entre Resena y DTOs
 */
@Mapper(componentModel = "spring", uses = {ProductoMapper.class})
public abstract class ResenaMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(target = "producto", source = "producto")
    @Mapping(target = "usuario", expression = "java(mapUsuario(resena))")
    @Mapping(target = "ventajas", expression = "java(arrayToList(resena.getVentajas()))")
    @Mapping(target = "desventajas", expression = "java(arrayToList(resena.getDesventajas()))")
    @Mapping(target = "imagenes", expression = "java(jsonToList(resena.getImagenes()))")
    @Mapping(target = "varianteComprada", expression = "java(mapVarianteComprada(resena))")
    public abstract ResenaResponse toResponse(Resena resena);

    protected List<String> arrayToList(String[] array) {
        return array != null ? Arrays.asList(array) : null;
    }

    @SuppressWarnings("unchecked")
    protected List<String> jsonToList(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    protected ResenaResponse.UsuarioResenaResponse mapUsuario(Resena resena) {
        if (resena.getUsuario() == null) return null;
        
        String nombreCompleto = resena.getUsuario().getNombreCompleto();
        String iniciales = obtenerIniciales(nombreCompleto);
        
        return ResenaResponse.UsuarioResenaResponse.builder()
            .nombreCompleto(nombreCompleto)
            .iniciales(iniciales)
            .verificado(resena.estaVerificada())
            .totalResenas(0) // Se podría calcular con una consulta adicional
            .build();
    }

    protected ResenaResponse.VarianteCompradaResponse mapVarianteComprada(Resena resena) {
        if (resena.getVarianteComprada() == null) return null;
        
        return ResenaResponse.VarianteCompradaResponse.builder()
            .id(resena.getVarianteComprada().getId())
            .sku(resena.getVarianteComprada().getSku())
            .talla(resena.getTallaComprada())
            .color(resena.getColorComprado())
            .build();
    }

    private String obtenerIniciales(String nombreCompleto) {
        if (nombreCompleto == null || nombreCompleto.isEmpty()) return "";
        
        String[] partes = nombreCompleto.split(" ");
        StringBuilder iniciales = new StringBuilder();
        
        for (String parte : partes) {
            if (!parte.isEmpty()) {
                iniciales.append(parte.charAt(0));
            }
        }
        
        return iniciales.toString().toUpperCase();
    }
}