package com.dpattymoda.service.impl;

import com.dpattymoda.entity.Usuario;
import com.dpattymoda.repository.UsuarioRepository;
import com.dpattymoda.security.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Servicio personalizado para cargar detalles del usuario
 */
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Usuario usuario = usuarioRepository.findByEmailAndActivoTrue(email)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con email: " + email));

        return UserPrincipal.create(usuario);
    }

    @Transactional(readOnly = true)
    public UserDetails loadUserById(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con ID: " + id));

        return UserPrincipal.create(usuario);
    }
}