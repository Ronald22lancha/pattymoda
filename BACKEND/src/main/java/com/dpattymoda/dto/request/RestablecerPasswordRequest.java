package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * DTO para restablecer contraseña
 */
@Data
@Schema(description = "Datos para restablecer contraseña")
public class RestablecerPasswordRequest {

    @Schema(description = "Token de recuperación", example = "abc123-def456-ghi789")
    @NotBlank(message = "El token es requerido")
    private String token;

    @Schema(description = "Nueva contraseña", example = "NuevaPassword123!")
    @NotBlank(message = "La nueva contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número")
    private String nuevaPassword;
}