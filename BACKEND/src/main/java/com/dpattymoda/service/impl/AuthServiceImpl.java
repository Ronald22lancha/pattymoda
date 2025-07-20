package com.dpattymoda.service.impl;

import com.dpattymoda.dto.request.LoginRequest;
import com.dpattymoda.dto.request.RegisterRequest;
import com.dpattymoda.dto.response.LoginResponse;
import com.dpattymoda.dto.response.UsuarioResponse;
import com.dpattymoda.entity.Rol;
import com.dpattymoda.entity.Usuario;
import com.dpattymoda.exception.BusinessException;
import com.dpattymoda.exception.ResourceNotFoundException;
import com.dpattymoda.mapper.UsuarioMapper;
import com.dpattymoda.repository.RolRepository;
import com.dpattymoda.repository.UsuarioRepository;
import com.dpattymoda.security.JwtTokenProvider;
import com.dpattymoda.service.AuditoriaService;
import com.dpattymoda.service.AuthService;
import com.dpattymoda.service.EmailService;
import com.dpattymoda.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación del servicio de autenticación
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final UsuarioMapper usuarioMapper;
    private final UsuarioService usuarioService;
    private final EmailService emailService;
    private final AuditoriaService auditoriaService;

    @Override
    public LoginResponse login(LoginRequest request) {
        log.info("Intento de login para usuario: {}", request.getEmail());

        try {
            // Verificar si el usuario está bloqueado
            if (usuarioService.estaUsuarioBloqueado(request.getEmail())) {
                throw new BusinessException("Usuario temporalmente bloqueado por múltiples intentos fallidos");
            }

            // Autenticar usuario
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Obtener usuario
            Usuario usuario = usuarioService.obtenerUsuarioPorEmail(request.getEmail());

            // Limpiar intentos fallidos
            usuarioService.limpiarBloqueo(request.getEmail());

            // Actualizar último acceso
            usuarioService.actualizarUltimoAcceso(usuario.getId());

            // Generar tokens
            String accessToken = jwtTokenProvider.generateToken(authentication);
            String refreshToken = jwtTokenProvider.generateRefreshToken(authentication);

            // Auditar login exitoso
            auditoriaService.registrarAccion("LOGIN_EXITOSO", "usuarios", usuario.getId(),
                null, null, "Login exitoso para: " + usuario.getEmail());

            log.info("Login exitoso para usuario: {}", request.getEmail());

            return LoginResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .tokenType("Bearer")
                .expiresIn(jwtTokenProvider.getJwtExpirationInMs() / 1000)
                .usuario(usuarioMapper.toResponse(usuario))
                .fechaLogin(LocalDateTime.now())
                .permisos(obtenerPermisosUsuario(usuario))
                .build();

        } catch (BadCredentialsException e) {
            // Registrar intento fallido
            usuarioService.registrarIntentoFallido(request.getEmail());
            
            // Auditar intento fallido
            auditoriaService.registrarAccion("LOGIN_FALLIDO", "usuarios", null,
                null, null, "Intento de login fallido para: " + request.getEmail());

            log.warn("Intento de login fallido para usuario: {}", request.getEmail());
            throw new BusinessException("Credenciales inválidas");
        }
    }

    @Override
    public UsuarioResponse register(RegisterRequest request) {
        log.info("Registrando nuevo usuario: {}", request.getEmail());

        // Validar email único
        if (usuarioRepository.existsByEmail(request.getEmail())) {
            throw new BusinessException("Ya existe un usuario con el email: " + request.getEmail());
        }

        // Obtener rol de cliente
        Rol rolCliente = rolRepository.findByNombreRolAndActivoTrue("Cliente")
            .orElseThrow(() -> new ResourceNotFoundException("Rol Cliente no encontrado"));

        // Crear usuario
        Usuario usuario = Usuario.builder()
            .email(request.getEmail())
            .passwordHash(passwordEncoder.encode(request.getPassword()))
            .nombres(request.getNombres())
            .apellidos(request.getApellidos())
            .telefono(request.getTelefono())
            .dni(request.getDni())
            .direccion(request.getDireccion())
            .fechaNacimiento(request.getFechaNacimiento())
            .genero(request.getGenero())
            .rol(rolCliente)
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

        // Auditar registro
        auditoriaService.registrarAccion("REGISTRO_USUARIO", "usuarios", usuario.getId(),
            null, usuarioMapper.toJson(usuario), "Nuevo usuario registrado: " + usuario.getEmail());

        log.info("Usuario registrado exitosamente: {}", usuario.getEmail());
        return usuarioMapper.toResponse(usuario);
    }

    @Override
    public void logout(String token) {
        // Extraer token sin "Bearer "
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        // Invalidar token (implementar blacklist si es necesario)
        jwtTokenProvider.invalidateToken(jwtToken);
        
        // Limpiar contexto de seguridad
        SecurityContextHolder.clearContext();
        
        log.info("Usuario deslogueado exitosamente");
    }

    @Override
    public void verificarEmail(String token) {
        usuarioService.verificarEmail(token);
    }

    @Override
    public void solicitarRecuperacionPassword(String email) {
        usuarioService.solicitarRecuperacionPassword(email);
    }

    @Override
    public void restablecerPassword(String token, String nuevaPassword) {
        usuarioService.restablecerPassword(token, nuevaPassword);
    }

    @Override
    public boolean validarToken(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        return jwtTokenProvider.validateToken(jwtToken);
    }

    @Override
    public LoginResponse refreshToken(String token) {
        String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;
        
        if (!jwtTokenProvider.validateRefreshToken(jwtToken)) {
            throw new BusinessException("Token de renovación inválido o expirado");
        }

        String email = jwtTokenProvider.getUsernameFromToken(jwtToken);
        Usuario usuario = usuarioService.obtenerUsuarioPorEmail(email);

        // Crear nueva autenticación
        Authentication authentication = new UsernamePasswordAuthenticationToken(
            email, null, jwtTokenProvider.getAuthoritiesFromToken(jwtToken)
        );

        // Generar nuevos tokens
        String newAccessToken = jwtTokenProvider.generateToken(authentication);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(authentication);

        return LoginResponse.builder()
            .accessToken(newAccessToken)
            .refreshToken(newRefreshToken)
            .tokenType("Bearer")
            .expiresIn(jwtTokenProvider.getJwtExpirationInMs() / 1000)
            .usuario(usuarioMapper.toResponse(usuario))
            .fechaLogin(LocalDateTime.now())
            .permisos(obtenerPermisosUsuario(usuario))
            .build();
    }

    private String[] obtenerPermisosUsuario(Usuario usuario) {
        // Implementar lógica para obtener permisos basados en el rol
        if (usuario.esAdministrador()) {
            return new String[]{"ALL"};
        } else if (usuario.esEmpleado()) {
            return new String[]{"PRODUCTS", "INVENTORY", "ORDERS", "CUSTOMERS"};
        } else if (usuario.esCajero()) {
            return new String[]{"POS", "SALES", "CASH"};
        } else {
            return new String[]{"SHOP", "PROFILE"};
        }
    }
}