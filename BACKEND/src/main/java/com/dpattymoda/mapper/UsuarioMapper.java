package com.dpattymoda.mapper;

import com.dpattymoda.dto.response.UsuarioResponse;
import com.dpattymoda.entity.Usuario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Mapper para conversión entre Usuario y DTOs
 */
@Mapper(componentModel = "spring", uses = {RolMapper.class})
public abstract class UsuarioMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(target = "nombreCompleto", expression = "java(usuario.getNombreCompleto())")
    public abstract UsuarioResponse toResponse(Usuario usuario);

    public String toJson(Usuario usuario) {
        try {
            return objectMapper.writeValueAsString(usuario);
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }
}