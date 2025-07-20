package com.dpattymoda.security;

import com.dpattymoda.entity.Usuario;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;
import java.util.UUID;

/**
 * Principal del usuario para Spring Security
 */
@Data
@AllArgsConstructor
public class UserPrincipal implements UserDetails {

    private UUID id;
    private String email;
    private String password;
    private String nombreCompleto;
    private String rol;
    private boolean activo;
    private boolean emailVerificado;
    private boolean bloqueado;

    public static UserPrincipal create(Usuario usuario) {
        return new UserPrincipal(
            usuario.getId(),
            usuario.getEmail(),
            usuario.getPasswordHash(),
            usuario.getNombreCompleto(),
            usuario.getRol() != null ? usuario.getRol().getNombreRol() : "Cliente",
            usuario.estaActivo(),
            usuario.getEmailVerificado() != null ? usuario.getEmailVerificado() : false,
            usuario.estaBloqueado()
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol.toUpperCase()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !bloqueado;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return activo;
    }
}