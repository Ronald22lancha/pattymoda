package com.dpattymoda.service;

import com.dpattymoda.dto.request.UsuarioCreateRequest;
import com.dpattymoda.dto.request.UsuarioUpdateRequest;
import com.dpattymoda.dto.response.UsuarioResponse;
import com.dpattymoda.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.UUID;

/**
 * Servicio para gestión de usuarios del sistema DPattyModa
 */
public interface UsuarioService {

    /**
     * Crear un nuevo usuario
     */
    UsuarioResponse crearUsuario(UsuarioCreateRequest request);

    /**
     * Actualizar un usuario existente
     */
    UsuarioResponse actualizarUsuario(UUID id, UsuarioUpdateRequest request);

    /**
     * Obtener usuario por ID
     */
    UsuarioResponse obtenerUsuario(UUID id);

    /**
     * Obtener usuario por email
     */
    Usuario obtenerUsuarioPorEmail(String email);

    /**
     * Listar todos los usuarios activos
     */
    Page<UsuarioResponse> listarUsuarios(Pageable pageable);

    /**
     * Buscar usuarios por término de búsqueda
     */
    Page<UsuarioResponse> buscarUsuarios(String termino, Pageable pageable);

    /**
     * Obtener usuarios por rol
     */
    List<UsuarioResponse> obtenerUsuariosPorRol(String nombreRol);

    /**
     * Activar/desactivar usuario
     */
    void cambiarEstadoUsuario(UUID id, boolean activo);

    /**
     * Verificar email del usuario
     */
    void verificarEmail(String token);

    /**
     * Iniciar proceso de recuperación de contraseña
     */
    void solicitarRecuperacionPassword(String email);

    /**
     * Restablecer contraseña con token
     */
    void restablecerPassword(String token, String nuevaPassword);

    /**
     * Cambiar contraseña del usuario autenticado
     */
    void cambiarPassword(UUID usuarioId, String passwordActual, String nuevaPassword);

    /**
     * Actualizar último acceso del usuario
     */
    void actualizarUltimoAcceso(UUID usuarioId);

    /**
     * Registrar intento fallido de login
     */
    void registrarIntentoFallido(String email);

    /**
     * Limpiar bloqueo del usuario
     */
    void limpiarBloqueo(String email);

    /**
     * Verificar si el usuario está bloqueado
     */
    boolean estaUsuarioBloqueado(String email);

    /**
     * Obtener estadísticas de usuarios
     */
    long contarUsuariosRegistradosHoy();

    long contarUsuariosActivos();
}