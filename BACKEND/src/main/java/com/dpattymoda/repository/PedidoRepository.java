package com.dpattymoda.repository;

import com.dpattymoda.entity.Pedido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repositorio para gestión de pedidos
 */
@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {

    Optional<Pedido> findByNumeroPedido(String numeroPedido);

    Page<Pedido> findByUsuario_IdOrderByFechaCreacionDesc(UUID usuarioId, Pageable pageable);

    Page<Pedido> findByEstadoOrderByFechaCreacionDesc(String estado, Pageable pageable);

    Page<Pedido> findByTipoVentaOrderByFechaCreacionDesc(String tipoVenta, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.sucursal.id = :sucursalId " +
           "AND p.tipoVenta = 'presencial' " +
           "ORDER BY p.fechaCreacion DESC")
    Page<Pedido> findVentasPresencialesPorSucursal(@Param("sucursalId") UUID sucursalId, Pageable pageable);

    @Query("SELECT p FROM Pedido p WHERE p.vendedor.id = :vendedorId " +
           "AND p.fechaCreacion >= :fechaInicio " +
           "ORDER BY p.fechaCreacion DESC")
    List<Pedido> findVentasPorVendedor(@Param("vendedorId") UUID vendedorId, 
                                      @Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT p FROM Pedido p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
           "AND p.estado NOT IN ('cancelado') " +
           "ORDER BY p.fechaCreacion DESC")
    List<Pedido> findVentasPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                                     @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT SUM(p.total) FROM Pedido p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
           "AND p.estado NOT IN ('cancelado')")
    BigDecimal calcularVentasPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                                       @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT COUNT(p) FROM Pedido p WHERE p.fechaCreacion >= :fechaInicio " +
           "AND p.estado NOT IN ('cancelado')")
    long contarVentasDesde(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query("SELECT AVG(p.total) FROM Pedido p WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
           "AND p.estado NOT IN ('cancelado')")
    BigDecimal calcularTicketPromedio(@Param("fechaInicio") LocalDateTime fechaInicio,
                                     @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT p FROM Pedido p WHERE p.estado = :estado " +
           "AND p.fechaCreacion < :fechaLimite")
    List<Pedido> findPedidosAntiguosPorEstado(@Param("estado") String estado,
                                             @Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT p FROM Pedido p WHERE p.tipoVenta = 'online' " +
           "AND p.estado = 'pendiente' " +
           "AND p.fechaCreacion < :fechaLimite")
    List<Pedido> findCarritosAbandonados(@Param("fechaLimite") LocalDateTime fechaLimite);

    @Query("SELECT p.metodoPago, COUNT(p), SUM(p.total) FROM Pedido p " +
           "WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
           "AND p.estado NOT IN ('cancelado') " +
           "GROUP BY p.metodoPago")
    List<Object[]> obtenerEstadisticasPorMetodoPago(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                   @Param("fechaFin") LocalDateTime fechaFin);

    boolean existsByNumeroPedido(String numeroPedido);
}