package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

/**
 * DTO para respuesta de datos del rol
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del rol")
public class RolResponse {

    @Schema(description = "ID único del rol")
    private UUID id;

    @Schema(description = "Nombre del rol", example = "Administrador")
    private String nombreRol;

    @Schema(description = "Descripción del rol", example = "Acceso completo al sistema")
    private String descripcion;

    @Schema(description = "Estado activo del rol", example = "true")
    private Boolean activo;
}