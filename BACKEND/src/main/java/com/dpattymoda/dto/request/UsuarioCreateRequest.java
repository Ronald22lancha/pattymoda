package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO para creación de usuario por administrador
 */
@Data
@Schema(description = "Datos para crear nuevo usuario")
public class UsuarioCreateRequest {

    @Schema(description = "Email del usuario", example = "empleado@dpattymoda.com")
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Schema(description = "Contraseña temporal", example = "TempPassword123!")
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    private String password;

    @Schema(description = "Nombres del usuario", example = "María Elena")
    @NotBlank(message = "Los nombres son requeridos")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;

    @Schema(description = "Apellidos del usuario", example = "Rodríguez Silva")
    @NotBlank(message = "Los apellidos son requeridos")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @Schema(description = "Teléfono del usuario", example = "+51 987654321")
    private String telefono;

    @Schema(description = "DNI del usuario", example = "87654321")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @Schema(description = "RUC del usuario (opcional)", example = "20123456789")
    @Pattern(regexp = "^\\d{11}$", message = "El RUC debe tener 11 dígitos")
    private String ruc;

    @Schema(description = "Dirección del usuario")
    private String direccion;

    @Schema(description = "Fecha de nacimiento", example = "1985-03-20")
    private LocalDate fechaNacimiento;

    @Schema(description = "Género del usuario", example = "F")
    @Pattern(regexp = "^[MF]$", message = "El género debe ser M o F")
    private String genero;

    @Schema(description = "Nombre del rol", example = "Empleado")
    @NotBlank(message = "El rol es requerido")
    private String nombreRol;
}