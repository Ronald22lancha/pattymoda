package com.dpattymoda.repository;

import com.dpattymoda.entity.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de usuarios
 */
@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmailAndActivoTrue(String email);

    Optional<Usuario> findByEmail(String email);

    Optional<Usuario> findByDni(String dni);

    Optional<Usuario> findByRuc(String ruc);

    Optional<Usuario> findByTokenVerificacion(String token);

    Optional<Usuario> findByTokenRecuperacion(String token);

    @Query("SELECT u FROM Usuario u WHERE u.tokenRecuperacion = :token " +
           "AND u.fechaTokenRecuperacion > :fechaLimite")
    Optional<Usuario> findByTokenRecuperacionValido(@Param("token") String token, 
                                                   @Param("fechaLimite") LocalDateTime fechaLimite);

    List<Usuario> findByRol_NombreRolAndActivoTrue(String nombreRol);

    Page<Usuario> findByActivoTrue(Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.activo = true " +
           "AND (LOWER(u.nombres) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%', :termino, '%')) " +
           "OR LOWER(u.email) LIKE LOWER(CONCAT('%', :termino, '%')))")
    Page<Usuario> buscarUsuariosActivos(@Param("termino") String termino, Pageable pageable);

    @Query("SELECT u FROM Usuario u WHERE u.rol.nombreRol IN :roles AND u.activo = true")
    List<Usuario> findByRolesAndActivoTrue(@Param("roles") List<String> roles);

    @Query("SELECT COUNT(u) FROM Usuario u WHERE u.fechaCreacion >= :fechaInicio")
    long contarUsuariosRegistradosDesde(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT u FROM Usuario u WHERE u.ultimoAcceso < :fechaLimite AND u.activo = true")
    List<Usuario> findUsuariosInactivos(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Modifying
    @Query("UPDATE Usuario u SET u.intentosFallidos = u.intentosFallidos + 1 WHERE u.email = :email")
    void incrementarIntentosFallidos(@Param("email") String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.intentosFallidos = 0, u.bloqueadoHasta = null WHERE u.email = :email")
    void limpiarBloqueo(@Param("email") String email);

    @Modifying
    @Query("UPDATE Usuario u SET u.bloqueadoHasta = :fechaBloqueo WHERE u.email = :email")
    void bloquearUsuario(@Param("email") String email, @Param("fechaBloqueo") LocalDateTime fechaBloqueo);

    @Modifying
    @Query("UPDATE Usuario u SET u.ultimoAcceso = :fecha WHERE u.id = :id")
    void actualizarUltimoAcceso(@Param("id") UUID id, @Param("fecha") LocalDateTime fecha);

    boolean existsByEmail(String email);

    boolean existsByDni(String dni);

    boolean existsByRuc(String ruc);
}