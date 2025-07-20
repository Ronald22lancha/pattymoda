package com.dpattymoda.security;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Proveedor de tokens JWT
 */
@Slf4j
@Component
public class JwtTokenProvider {

    private final SecretKey jwtSecret;
    private final long jwtExpirationInMs;
    private final long refreshExpirationInMs;

    public JwtTokenProvider(@Value("${app.security.jwt.secret}") String secret,
                           @Value("${app.security.jwt.expiration}") long expiration,
                           @Value("${app.security.jwt.refresh-expiration}") long refreshExpiration) {
        this.jwtSecret = Keys.hmacShaKeyFor(secret.getBytes());
        this.jwtExpirationInMs = expiration;
        this.refreshExpirationInMs = refreshExpiration;
    }

    public String generateToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + jwtExpirationInMs);

        List<String> authorities = authentication.getAuthorities().stream()
            .map(GrantedAuthority::getAuthority)
            .collect(Collectors.toList());

        return Jwts.builder()
            .setSubject(userPrincipal.getEmail())
            .claim("userId", userPrincipal.getId().toString())
            .claim("authorities", authorities)
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(jwtSecret, SignatureAlgorithm.HS512)
            .compact();
    }

    public String generateRefreshToken(Authentication authentication) {
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();
        Date expiryDate = new Date(System.currentTimeMillis() + refreshExpirationInMs);

        return Jwts.builder()
            .setSubject(userPrincipal.getEmail())
            .claim("userId", userPrincipal.getId().toString())
            .claim("type", "refresh")
            .setIssuedAt(new Date())
            .setExpiration(expiryDate)
            .signWith(jwtSecret, SignatureAlgorithm.HS512)
            .compact();
    }

    public String getUsernameFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(jwtSecret)
            .build()
            .parseClaimsJws(token)
            .getBody();

        return claims.getSubject();
    }

    public String getUserIdFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(jwtSecret)
            .build()
            .parseClaimsJws(token)
            .getBody();

        return claims.get("userId", String.class);
    }

    @SuppressWarnings("unchecked")
    public Collection<GrantedAuthority> getAuthoritiesFromToken(String token) {
        Claims claims = Jwts.parserBuilder()
            .setSigningKey(jwtSecret)
            .build()
            .parseClaimsJws(token)
            .getBody();

        List<String> authorities = claims.get("authorities", List.class);
        return authorities.stream()
            .map(SimpleGrantedAuthority::new)
            .collect(Collectors.toList());
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(authToken);
            return true;
        } catch (SecurityException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty");
        }
        return false;
    }

    public boolean validateRefreshToken(String refreshToken) {
        try {
            Claims claims = Jwts.parserBuilder()
                .setSigningKey(jwtSecret)
                .build()
                .parseClaimsJws(refreshToken)
                .getBody();

            String tokenType = claims.get("type", String.class);
            return "refresh".equals(tokenType);
        } catch (Exception ex) {
            log.error("Invalid refresh token", ex);
            return false;
        }
    }

    public void invalidateToken(String token) {
        // Implementar blacklist de tokens si es necesario
        // Por ahora, los tokens expiran automáticamente
        log.info("Token invalidado: {}", token.substring(0, Math.min(token.length(), 20)) + "...");
    }

    public long getJwtExpirationInMs() {
        return jwtExpirationInMs;
    }
}