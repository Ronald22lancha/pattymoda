package com.dpattymoda.repository;

import com.dpattymoda.entity.DetallePedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio para gestión de detalles de pedidos
 */
@Repository
public interface DetallePedidoRepository extends JpaRepository<DetallePedido, UUID> {

    List<DetallePedido> findByPedido_Id(UUID pedidoId);

    @Query("SELECT dp FROM DetallePedido dp JOIN dp.pedido p " +
           "WHERE p.fechaCreacion BETWEEN :fechaInicio AND :fechaFin " +
           "AND p.estado NOT IN ('cancelado') " +
           "ORDER BY dp.cantidad DESC")
    List<DetallePedido> findProductosMasVendidosPorPeriodo(@Param("fechaInicio") LocalDateTime fechaInicio,
                                                          @Param("fechaFin") LocalDateTime fechaFin);

    @Query("SELECT SUM(dp.cantidad) FROM DetallePedido dp JOIN dp.pedido p " +
           "WHERE dp.variante.id = :varianteId " +
           "AND p.fechaCreacion >= :fechaInicio " +
           "AND p.estado NOT IN ('cancelado')")
    Integer contarVentasPorVariante(@Param("varianteId") UUID varianteId,
                                   @Param("fechaInicio") LocalDateTime fechaInicio);
}