package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para respuesta de datos del usuario
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del usuario")
public class UsuarioResponse {

    @Schema(description = "ID único del usuario")
    private UUID id;

    @Schema(description = "Email del usuario", example = "cliente@email.com")
    private String email;

    @Schema(description = "Nombres del usuario", example = "Juan Carlos")
    private String nombres;

    @Schema(description = "Apellidos del usuario", example = "García López")
    private String apellidos;

    @Schema(description = "Nombre completo del usuario", example = "Juan Carlos García López")
    private String nombreCompleto;

    @Schema(description = "Teléfono del usuario", example = "+51 987654321")
    private String telefono;

    @Schema(description = "DNI del usuario", example = "12345678")
    private String dni;

    @Schema(description = "RUC del usuario", example = "20123456789")
    private String ruc;

    @Schema(description = "Dirección del usuario")
    private String direccion;

    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
    private LocalDate fechaNacimiento;

    @Schema(description = "Género del usuario", example = "M")
    private String genero;

    @Schema(description = "Rol del usuario")
    private RolResponse rol;

    @Schema(description = "Estado activo del usuario", example = "true")
    private Boolean activo;

    @Schema(description = "Email verificado", example = "true")
    private Boolean emailVerificado;

    @Schema(description = "Último acceso del usuario")
    private LocalDateTime ultimoAcceso;

    @Schema(description = "Fecha de creación de la cuenta")
    private LocalDateTime fechaCreacion;

    @Schema(description = "Fecha de última actualización")
    private LocalDateTime fechaActualizacion;
}