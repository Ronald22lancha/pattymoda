package com.dpattymoda.mapper;

import com.dpattymoda.dto.response.RolResponse;
import com.dpattymoda.entity.Rol;
import org.mapstruct.Mapper;

/**
 * Mapper para conversión entre Rol y DTOs
 */
@Mapper(componentModel = "spring")
public interface RolMapper {

    RolResponse toResponse(Rol rol);
}