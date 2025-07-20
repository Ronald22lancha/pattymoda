package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad UsoCupon para historial de uso de cupones
 */
@Entity
@Table(name = "uso_cupones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class UsoCupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cupon_id", nullable = false)
    private Cupon cupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id")
    private Pedido pedido;

    @Column(name = "monto_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoDescuento;

    @Column(name = "monto_original", precision = 10, scale = 2)
    private BigDecimal montoOriginal;

    @Column(name = "ip_address", length = 45)
    private String ipAddress;

    @Column(name = "user_agent", columnDefinition = "TEXT")
    private String userAgent;

    @CreatedDate
    @Column(name = "fecha_uso", nullable = false, updatable = false)
    private LocalDateTime fechaUso;

    // Métodos de utilidad
    public BigDecimal getPorcentajeDescuento() {
        if (montoOriginal == null || montoOriginal.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return montoDescuento.divide(montoOriginal, 4, BigDecimal.ROUND_HALF_UP)
                            .multiply(BigDecimal.valueOf(100));
    }

    public BigDecimal getMontoFinal() {
        if (montoOriginal == null) {
            return BigDecimal.ZERO;
        }
        return montoOriginal.subtract(montoDescuento);
    }

    public String getCodigoCupon() {
        return cupon != null ? cupon.getCodigoCupon() : null;
    }

    public String getNombreCupon() {
        return cupon != null ? cupon.getNombre() : null;
    }

    public String getNumeroPedido() {
        return pedido != null ? pedido.getNumeroPedido() : null;
    }

    public String getNombreUsuario() {
        return usuario != null ? usuario.getNombreCompleto() : "Usuario anónimo";
    }
}