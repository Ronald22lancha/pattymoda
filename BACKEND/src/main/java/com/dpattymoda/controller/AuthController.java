package com.dpattymoda.controller;

import com.dpattymoda.dto.request.LoginRequest;
import com.dpattymoda.dto.request.RegisterRequest;
import com.dpattymoda.dto.request.RecuperarPasswordRequest;
import com.dpattymoda.dto.request.RestablecerPasswordRequest;
import com.dpattymoda.dto.response.LoginResponse;
import com.dpattymoda.dto.response.UsuarioResponse;
import com.dpattymoda.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Controlador para autenticación y registro de usuarios
 */
@Tag(name = "Autenticación", description = "Endpoints para autenticación y registro")
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Iniciar sesión", description = "Autenticar usuario y obtener token JWT")
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Registrar usuario", description = "Crear cuenta de cliente nuevo")
    @PostMapping("/register")
    public ResponseEntity<UsuarioResponse> register(@Valid @RequestBody RegisterRequest request) {
        UsuarioResponse response = authService.register(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Cerrar sesión", description = "Invalidar token JWT actual")
    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestHeader("Authorization") String token) {
        authService.logout(token);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Verificar email", description = "Verificar dirección de correo electrónico")
    @GetMapping("/verificar-email")
    public ResponseEntity<String> verificarEmail(@RequestParam String token) {
        authService.verificarEmail(token);
        return ResponseEntity.ok("Email verificado exitosamente");
    }

    @Operation(summary = "Solicitar recuperación de contraseña")
    @PostMapping("/recuperar-password")
    public ResponseEntity<String> recuperarPassword(@Valid @RequestBody RecuperarPasswordRequest request) {
        authService.solicitarRecuperacionPassword(request.getEmail());
        return ResponseEntity.ok("Se ha enviado un enlace de recuperación a tu email");
    }

    @Operation(summary = "Restablecer contraseña")
    @PostMapping("/restablecer-password")
    public ResponseEntity<String> restablecerPassword(@Valid @RequestBody RestablecerPasswordRequest request) {
        authService.restablecerPassword(request.getToken(), request.getNuevaPassword());
        return ResponseEntity.ok("Contraseña restablecida exitosamente");
    }

    @Operation(summary = "Validar token JWT")
    @GetMapping("/validar-token")
    public ResponseEntity<Boolean> validarToken(@RequestHeader("Authorization") String token) {
        boolean esValido = authService.validarToken(token);
        return ResponseEntity.ok(esValido);
    }

    @Operation(summary = "Refrescar token JWT")
    @PostMapping("/refresh-token")
    public ResponseEntity<LoginResponse> refreshToken(@RequestHeader("Authorization") String token) {
        LoginResponse response = authService.refreshToken(token);
        return ResponseEntity.ok(response);
    }
}