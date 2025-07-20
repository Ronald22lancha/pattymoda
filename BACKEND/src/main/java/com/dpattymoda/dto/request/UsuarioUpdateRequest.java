package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO para actualización de usuario
 */
@Data
@Schema(description = "Datos para actualizar usuario")
public class UsuarioUpdateRequest {

    @Schema(description = "Email del usuario", example = "usuario@email.com")
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Schema(description = "Nombres del usuario", example = "Juan Carlos")
    @NotBlank(message = "Los nombres son requeridos")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;

    @Schema(description = "Apellidos del usuario", example = "García López")
    @NotBlank(message = "Los apellidos son requeridos")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @Schema(description = "Teléfono del usuario", example = "+51 987654321")
    private String telefono;

    @Schema(description = "DNI del usuario", example = "12345678")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @Schema(description = "RUC del usuario (opcional)", example = "20123456789")
    @Pattern(regexp = "^\\d{11}$", message = "El RUC debe tener 11 dígitos")
    private String ruc;

    @Schema(description = "Dirección del usuario")
    private String direccion;

    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
    private LocalDate fechaNacimiento;

    @Schema(description = "Género del usuario", example = "M")
    @Pattern(regexp = "^[MF]$", message = "El género debe ser M o F")
    private String genero;

    @Schema(description = "Nombre del rol (solo para administradores)", example = "Empleado")
    private String nombreRol;
}