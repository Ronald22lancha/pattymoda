package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para solicitud de recuperación de contraseña
 */
@Data
@Schema(description = "Datos para recuperar contraseña")
public class RecuperarPasswordRequest {

    @Schema(description = "Email del usuario", example = "usuario@email.com")
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;
}