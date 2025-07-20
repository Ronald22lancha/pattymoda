package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * DTO para respuesta de login exitoso
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Respuesta de login exitoso")
public class LoginResponse {

    @Schema(description = "Token JWT de acceso")
    private String accessToken;

    @Schema(description = "Token de renovación")
    private String refreshToken;

    @Schema(description = "Tipo de token", example = "Bearer")
    @Builder.Default
    private String tokenType = "Bearer";

    @Schema(description = "Tiempo de expiración en segundos", example = "86400")
    private Long expiresIn;

    @Schema(description = "Información del usuario autenticado")
    private UsuarioResponse usuario;

    @Schema(description = "Fecha y hora del login")
    private LocalDateTime fechaLogin;

    @Schema(description = "Permisos del usuario")
    private String[] permisos;
}