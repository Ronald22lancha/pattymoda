package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Entidad Pedido para gestión de órdenes de venta
 */
@Entity
@Table(name = "pedidos")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "numero_pedido", nullable = false, unique = true, length = 50)
    private String numeroPedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sucursal_id")
    private Sucursal sucursal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "direccion_envio_id")
    private DireccionEnvio direccionEnvio;

    @Builder.Default
    @Column(name = "tipo_venta", nullable = false, length = 20)
    private String tipoVenta = "online"; // online, presencial

    @Builder.Default
    @Column(name = "estado", nullable = false, length = 30)
    private String estado = "pendiente"; // pendiente, confirmado, procesando, enviado, entregado, cancelado

    @Builder.Default
    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "descuento_total", precision = 10, scale = 2)
    private BigDecimal descuentoTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "impuestos_total", precision = 10, scale = 2)
    private BigDecimal impuestosTotal = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "costo_envio", precision = 10, scale = 2)
    private BigDecimal costoEnvio = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "moneda", length = 10)
    private String moneda = "PEN";

    @Column(name = "metodo_pago", length = 50)
    private String metodoPago; // efectivo, tarjeta, yape, plin, lukita, paypal, etc.

    @Builder.Default
    @Column(name = "estado_pago", length = 30)
    private String estadoPago = "pendiente"; // pendiente, procesado, fallido, reembolsado

    @Column(name = "notas_cliente", columnDefinition = "TEXT")
    private String notasCliente;

    @Column(name = "notas_internas", columnDefinition = "TEXT")
    private String notasInternas;

    @Column(name = "cupones_aplicados", columnDefinition = "jsonb")
    private String cuponesAplicados;

    @Column(name = "datos_cliente", columnDefinition = "jsonb")
    private String datosCliente; // Para ventas presenciales sin registro

    @Column(name = "fecha_estimada_entrega")
    private LocalDateTime fechaEstimadaEntrega;

    @Column(name = "fecha_entrega_real")
    private LocalDateTime fechaEntregaReal;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendedor_id")
    private Usuario vendedor;

    @Column(name = "caja_id")
    private UUID cajaId; // Referencia a la caja donde se realizó la venta presencial

    @Builder.Default
    @Column(name = "comprobante_requerido")
    private Boolean comprobanteRequerido = false;

    @Column(name = "tipo_comprobante", length = 20)
    private String tipoComprobante; // boleta, factura

    @Column(name = "datos_facturacion", columnDefinition = "jsonb")
    private String datosFacturacion;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Relaciones
    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<DetallePedido> detalles = new ArrayList<>();

    @OneToMany(mappedBy = "pedido", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Builder.Default
    private List<Pago> pagos = new ArrayList<>();

    // Métodos de utilidad
    public boolean esVentaPresencial() {
        return "presencial".equals(tipoVenta);
    }

    public boolean esVentaOnline() {
        return "online".equals(tipoVenta);
    }

    public boolean estaPendiente() {
        return "pendiente".equals(estado);
    }

    public boolean estaConfirmado() {
        return "confirmado".equals(estado);
    }

    public boolean estaCancelado() {
        return "cancelado".equals(estado);
    }

    public boolean estaEntregado() {
        return "entregado".equals(estado);
    }

    public boolean pagoPendiente() {
        return "pendiente".equals(estadoPago);
    }

    public boolean pagoProcesado() {
        return "procesado".equals(estadoPago);
    }

    public void calcularTotales() {
        this.subtotal = detalles.stream()
                              .map(DetallePedido::getSubtotal)
                              .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // IGV (18% en Perú)
        this.impuestosTotal = subtotal.subtract(descuentoTotal)
                                    .multiply(new BigDecimal("0.18"));
        
        this.total = subtotal.subtract(descuentoTotal)
                           .add(impuestosTotal)
                           .add(costoEnvio);
    }

    public int getCantidadTotalItems() {
        return detalles.stream()
                      .mapToInt(DetallePedido::getCantidad)
                      .sum();
    }

    public boolean requiereEnvio() {
        return esVentaOnline() && direccionEnvio != null;
    }

    public String getNombreCliente() {
        if (usuario != null) {
            return usuario.getNombreCompleto();
        }
        // Para ventas presenciales, extraer del JSON datosCliente
        return "Cliente de mostrador";
    }
}