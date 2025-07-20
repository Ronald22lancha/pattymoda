package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

/**
 * DTO para solicitud de login
 */
@Data
@Schema(description = "Datos para iniciar sesión")
public class LoginRequest {

    @Schema(description = "Email del usuario", example = "admin@dpattymoda.com")
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "password123")
    @NotBlank(message = "La contraseña es requerida")
    private String password;

    @Schema(description = "Recordar sesión", example = "true")
    private Boolean recordarSesion = false;
}