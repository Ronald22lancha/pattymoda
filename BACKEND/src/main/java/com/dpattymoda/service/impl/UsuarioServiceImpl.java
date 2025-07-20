package com.dpattymoda.service.impl;

import com.dpattymoda.dto.request.UsuarioCreateRequest;
import com.dpattymoda.dto.request.UsuarioUpdateRequest;
import com.dpattymoda.dto.response.UsuarioResponse;
import com.dpattymoda.entity.Rol;
import com.dpattymoda.entity.Usuario;
import com.dpattymoda.exception.BusinessException;
import com.dpattymoda.exception.ResourceNotFoundException;
import com.dpattymoda.mapper.UsuarioMapper;
import com.dpattymoda.repository.RolRepository;
import com.dpattymoda.repository.UsuarioRepository;
import com.dpattymoda.service.AuditoriaService;
import com.dpattymoda.service.EmailService;
import com.dpattymoda.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Implementación del servicio de usuarios
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final AuditoriaService auditoriaService;

    @Value("${app.security.max-intentos-login:5}")
    private int maxIntentosLogin;

    @Value("${app.security.tiempo-bloqueo-minutos:30}")
    private int tiempoBloqueoMinutos;

    @Override
    public UsuarioResponse crearUsuario(UsuarioCreateRequest request) {
        log.info("Creando nuevo usuario con email: {}", request.getEmail());

        // Validar email único
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un usuario con el email: " + request.getEmail());
        }

        // Validar DNI único si se proporciona
        if (request.getDni() != null && usuarioRepository.existsByDni(request.getDni())) {
            throw new BusinessException("Ya existe un usuario con el DNI: " + request.getDni());
        }

        // Validar RUC único si se proporciona
        if (request.getRuc() != null && usuarioRepository.existsByRuc(request.getRuc())) {
            throw new BusinessException("Ya existe un usuario con el RUC: " + request.getRuc());
        }

        // Obtener rol
        Rol rol = rolRepository.findByNombreRolAndActivoTrue(request.getNombreRol())
            .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + request.getNombreRol()));

        // Crear usuario
        Usuario usuario = Usuario.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .nombres(request.getNombres())
            .apellidos(request.getApellidos())
            .telefono(request.getTelefono())
            .dni(request.getDni())
            .ruc(request.getRuc())
            .direccion(request.getDireccion())
            .fechaNacimiento(request.getFechaNacimiento())
            .genero(request.getGenero())
            .rol(rol)
            .activo(true)
            .emailVerificado(false)
            .tokenVerificacion(UUID.randomUUID().toString())
            .build();

        usuario = usuarioRepository.save(usuario);

        // Enviar email de verificación
        try {
            emailService.enviarEmailVerificacion(usuario.getEmail(), usuario.getTokenVerificacion());
        } catch (Exception e) {
            log.warn("No se pudo enviar email de verificación para: {}", usuario.getEmail(), e);
        }

        // Auditar creación
        auditoriaService.registrarAccion("CREAR_USUARIO", "usuarios", usuario.getId(),
            null, usuarioMapper.toJson(usuario), "Usuario creado: " + usuario.getEmail());

        log.info("Usuario creado exitosamente: {}", usuario.getEmail());
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    public UsuarioResponse actualizarUsuario(UUID id, UsuarioUpdateRequest request) {
        log.info("Actualizando usuario ID: {}", id);

        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        Usuario usuarioAnterior = Usuario.builder()
            .id(usuario.getId())
            .email(usuario.getEmail())
            .nombres(usuario.getNombres())
            .apellidos(usuario.getApellidos())
            .telefono(usuario.getTelefono())
            .build();

        // Validar email único si cambió
        if (!usuario.getEmail().equals(request.getEmail()) && 
            usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un usuario con el email: " + request.getEmail());
        }

        // Actualizar datos
        usuario.setEmail(request.getEmail());
        usuario.setNombres(request.getNombres());
        usuario.setApellidos(request.getApellidos());
        usuario.setTelefono(request.getTelefono());
        usuario.setDni(request.getDni());
        usuario.setRuc(request.getRuc());
        usuario.setDireccion(request.getDireccion());
        usuario.setFechaNacimiento(request.getFechaNacimiento());
        usuario.setGenero(request.getGenero());

        // Actualizar rol si se especifica
        if (request.getNombreRol() != null) {
            Rol nuevoRol = rolRepository.findByNombreRolAndActivoTrue(request.getNombreRol())
                .orElseThrow(() -> new ResourceNotFoundException("Rol no encontrado: " + request.getNombreRol()));
            usuario.setRol(nuevoRol);
        }

        usuario = usuarioRepository.save(usuario);

        // Auditar actualización
        auditoriaService.registrarAccion("ACTUALIZAR_USUARIO", "usuarios", usuario.getId(),
            usuarioMapper.toJson(usuarioAnterior), usuarioMapper.toJson(usuario), 
            "Usuario actualizado: " + usuario.getEmail());

        log.info("Usuario actualizado exitosamente: {}", usuario.getEmail());
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponse obtenerUsuario(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario obtenerUsuarioPorEmail(String email) {
        return usuarioRepository.findByEmailAndActivoTrue(email)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con email: " + email));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> listarUsuarios(Pageable pageable) {
        return usuarioRepository.findByActivoTrue(pageable)
            .map(usuarioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UsuarioResponse> buscarUsuarios(String termino, Pageable pageable) {
        return usuarioRepository.buscarUsuariosActivos(termino, pageable)
            .map(usuarioMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponse> obtenerUsuariosPorRol(String nombreRol) {
        return usuarioRepository.findByRol_NombreRolAndActivoTrue(nombreRol)
            .stream()
            .map(usuarioMapper::toResponse)
            .toList();
    }

    @Override
    public void cambiarEstadoUsuario(UUID id, boolean activo) {
        log.info("Cambiando estado del usuario ID: {} a {}", id, activo);

        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con ID: " + id));

        boolean estadoAnterior = usuario.getActivo();
        usuario.setActivo(activo);
        usuarioRepository.save(usuario);

        // Auditar cambio de estado
        auditoriaService.registrarAccion(
            activo ? "ACTIVAR_USUARIO" : "DESACTIVAR_USUARIO",
            "usuarios", usuario.getId(),
            "{\"activo\":" + estadoAnterior + "}",
            "{\"activo\":" + activo + "}",
            "Estado de usuario cambiado: " + usuario.getEmail()
        );

        log.info("Estado del usuario {} cambiado a: {}", usuario.getEmail(), activo);
    }

    @Override
    public void verificarEmail(String token) {
        log.info("Verificando email con token: {}", token);

        Usuario usuario = usuarioRepository.findByTokenVerificacion(token)
            .orElseThrow(() -> new BusinessException("Token de verificación inválido"));

        usuario.setEmailVerificado(true);
        usuario.setTokenVerificacion(null);
        usuarioRepository.save(usuario);

        log.info("Email verificado exitosamente para usuario: {}", usuario.getEmail());
    }

    @Override
    public void solicitarRecuperacionPassword(String email) {
        log.info("Solicitando recuperación de contraseña para: {}", email);

        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(email)
            .orElse(null);

        if (usuario != null) {
            String token = UUID.randomUUID().toString();
            usuario.setTokenRecuperacion(token);
            usuario.setFechaTokenRecuperacion(LocalDateTime.now());
            usuarioRepository.save(usuario);

            // Enviar email de recuperación
            try {
                emailService.enviarEmailRecuperacion(email, token);
            } catch (Exception e) {
                log.error("Error al enviar email de recuperación para: {}", email, e);
                throw new BusinessException("Error al enviar email de recuperación");
            }
        }

        // Siempre responder exitosamente por seguridad
        log.info("Proceso de recuperación iniciado para: {}", email);
    }

    @Override
    public void restablecerPassword(String token, String nuevaPassword) {
        log.info("Restableciendo contraseña con token: {}", token);

        LocalDateTime fechaLimite = LocalDateTime.now().minusHours(1); // Token válido por 1 hora
        Usuario usuario = usuarioRepository.findByTokenRecuperacionValido(token, fechaLimite)
            .orElseThrow(() -> new BusinessException("Token de recuperación inválido o expirado"));

        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuario.setTokenRecuperacion(null);
        usuario.setFechaTokenRecuperacion(null);
        usuario.setIntentosFallidos(0);
        usuario.setBloqueadoHasta(null);
        usuarioRepository.save(usuario);

        // Auditar cambio de contraseña
        auditoriaService.registrarAccion("RESTABLECER_PASSWORD", "usuarios", usuario.getId(),
            null, null, "Contraseña restablecida para: " + usuario.getEmail());

        log.info("Contraseña restablecida exitosamente para: {}", usuario.getEmail());
    }

    @Override
    public void cambiarPassword(UUID usuarioId, String passwordActual, String nuevaPassword) {
        log.info("Cambiando contraseña para usuario ID: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        if (!passwordEncoder.matches(passwordActual, usuario.getPasswordHash())) {
            throw new BusinessException("La contraseña actual es incorrecta");
        }

        usuario.setPasswordHash(passwordEncoder.encode(nuevaPassword));
        usuarioRepository.save(usuario);

        // Auditar cambio de contraseña
        auditoriaService.registrarAccion("CAMBIAR_PASSWORD", "usuarios", usuario.getId(),
            null, null, "Contraseña cambiada por el usuario: " + usuario.getEmail());

        log.info("Contraseña cambiada exitosamente para: {}", usuario.getEmail());
    }

    @Override
    public void actualizarUltimoAcceso(UUID usuarioId) {
        usuarioRepository.actualizarUltimoAcceso(usuarioId, LocalDateTime.now());
    }

    @Override
    public void registrarIntentoFallido(String email) {
        usuarioRepository.incrementarIntentosFallidos(email);

        Usuario usuario = usuarioRepository.findByEmail(email).orElse(null);
        if (usuario != null && usuario.getIntentosFallidos() >= maxIntentosLogin) {
            LocalDateTime fechaBloqueo = LocalDateTime.now().plusMinutes(tiempoBloqueoMinutos);
            usuarioRepository.bloquearUsuario(email, fechaBloqueo);
            
            log.warn("Usuario bloqueado por múltiples intentos fallidos: {}", email);
        }
    }

    @Override
    public void limpiarBloqueo(String email) {
        usuarioRepository.limpiarBloqueo(email);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean estaUsuarioBloqueado(String email) {
        return usuarioRepository.findByEmail(email)
            .map(Usuario::estaBloqueado)
            .orElse(false);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarUsuariosRegistradosHoy() {
        LocalDateTime inicioHoy = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0);
        return usuarioRepository.contarUsuariosRegistradosDesde(inicioHoy);
    }

    @Override
    @Transactional(readOnly = true)
    public long contarUsuariosActivos() {
        return usuarioRepository.count();
    }
}