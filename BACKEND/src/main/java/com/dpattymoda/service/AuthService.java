package com.dpattymoda.service;

import com.dpattymoda.dto.request.LoginRequest;
import com.dpattymoda.dto.request.RegisterRequest;
import com.dpattymoda.dto.response.LoginResponse;
import com.dpattymoda.dto.response.UsuarioResponse;

/**
 * Servicio para autenticación y autorización
 */
public interface AuthService {

    /**
     * Autenticar usuario y generar token JWT
     */
    LoginResponse login(LoginRequest request);

    /**
     * Registrar nuevo cliente
     */
    UsuarioResponse register(RegisterRequest request);

    /**
     * Cerrar sesión e invalidar token
     */
    void logout(String token);

    /**
     * Verificar email del usuario
     */
    void verificarEmail(String token);

    /**
     * Solicitar recuperación de contraseña
     */
    void solicitarRecuperacionPassword(String email);

    /**
     * Restablecer contraseña con token
     */
    void restablecerPassword(String token, String nuevaPassword);

    /**
     * Validar token JWT
     */
    boolean validarToken(String token);

    /**
     * Refrescar token JWT
     */
    LoginResponse refreshToken(String token);
}