package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;

/**
 * DTO para solicitud de registro de usuario
 */
@Data
@Schema(description = "Datos para registro de nuevo usuario")
public class RegisterRequest {

    @Schema(description = "Email del usuario", example = "cliente@email.com")
    @NotBlank(message = "El email es requerido")
    @Email(message = "El email debe tener un formato válido")
    private String email;

    @Schema(description = "Contraseña del usuario", example = "MiPassword123!")
    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 8, message = "La contraseña debe tener al menos 8 caracteres")
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).*$", 
             message = "La contraseña debe contener al menos una mayúscula, una minúscula y un número")
    private String password;

    @Schema(description = "Nombres del usuario", example = "Juan Carlos")
    @NotBlank(message = "Los nombres son requeridos")
    @Size(max = 100, message = "Los nombres no pueden exceder 100 caracteres")
    private String nombres;

    @Schema(description = "Apellidos del usuario", example = "García López")
    @NotBlank(message = "Los apellidos son requeridos")
    @Size(max = 100, message = "Los apellidos no pueden exceder 100 caracteres")
    private String apellidos;

    @Schema(description = "Teléfono del usuario", example = "+51 987654321")
    @Pattern(regexp = "^\\+?[1-9]\\d{1,14}$", message = "El teléfono debe tener un formato válido")
    private String telefono;

    @Schema(description = "DNI del usuario", example = "12345678")
    @Pattern(regexp = "^\\d{8}$", message = "El DNI debe tener 8 dígitos")
    private String dni;

    @Schema(description = "Dirección del usuario")
    private String direccion;

    @Schema(description = "Fecha de nacimiento", example = "1990-05-15")
    private LocalDate fechaNacimiento;

    @Schema(description = "Género del usuario", example = "M")
    @Pattern(regexp = "^[MF]$", message = "El género debe ser M o F")
    private String genero;

    @Schema(description = "Aceptación de términos y condiciones", example = "true")
    private Boolean aceptaTerminos = false;
}