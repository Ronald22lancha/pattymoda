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
import java.util.UUID;

/**
 * Entidad Cupon para descuentos y promociones
 */
@Entity
@Table(name = "cupones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Cupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "codigo_cupon", nullable = false, unique = true, length = 50)
    private String codigoCupon;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tipo_descuento", nullable = false, length = 20)
    private String tipoDescuento; // porcentaje, monto_fijo

    @Column(name = "valor_descuento", nullable = false, precision = 10, scale = 2)
    private BigDecimal valorDescuento;

    @Column(name = "monto_minimo_compra", precision = 10, scale = 2)
    private BigDecimal montoMinimoCompra;

    @Column(name = "monto_maximo_descuento", precision = 10, scale = 2)
    private BigDecimal montoMaximoDescuento;

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "usos_maximos")
    private Integer usosMaximos;

    @Builder.Default
    @Column(name = "usos_por_usuario")
    private Integer usosPorUsuario = 1;

    @Builder.Default
    @Column(name = "usos_actuales")
    private Integer usosActuales = 0;

    @Builder.Default
    @Column(name = "solo_primera_compra")
    private Boolean soloPrimeraCompra = false;

    @Builder.Default
    @Column(name = "aplicable_envio")
    private Boolean aplicableEnvio = false;

    @Column(name = "categorias_incluidas")
    private UUID[] categoriasIncluidas; // Array de IDs de categorías

    @Column(name = "productos_incluidos")
    private UUID[] productosIncluidos; // Array de IDs de productos

    @Column(name = "usuarios_incluidos")
    private UUID[] usuariosIncluidos; // Array de IDs de usuarios específicos

    @Builder.Default
    @Column(name = "activo")
    private Boolean activo = true;

    @Column(name = "codigo_promocional", length = 100)
    private String codigoPromocional; // Para campañas específicas

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public boolean estaActivo() {
        return activo != null && activo;
    }

    public boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return fechaInicio.isBefore(ahora) && fechaFin.isAfter(ahora);
    }

    public boolean puedeUsarse() {
        return estaActivo() && estaVigente() && !estaAgotado();
    }

    public boolean estaAgotado() {
        return usosMaximos != null && usosActuales >= usosMaximos;
    }

    public boolean esPorcentaje() {
        return "porcentaje".equals(tipoDescuento);
    }

    public boolean esMontoFijo() {
        return "monto_fijo".equals(tipoDescuento);
    }

    public BigDecimal calcularDescuento(BigDecimal montoCompra) {
        if (!puedeUsarse()) {
            return BigDecimal.ZERO;
        }

        if (montoMinimoCompra != null && montoCompra.compareTo(montoMinimoCompra) < 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal descuento;
        if (esPorcentaje()) {
            descuento = montoCompra.multiply(valorDescuento.divide(BigDecimal.valueOf(100)));
        } else {
            descuento = valorDescuento;
        }

        // Aplicar máximo de descuento si existe
        if (montoMaximoDescuento != null && descuento.compareTo(montoMaximoDescuento) > 0) {
            descuento = montoMaximoDescuento;
        }

        return descuento;
    }

    public void usar() {
        this.usosActuales++;
    }

    public boolean esParaUsuarioEspecifico(UUID usuarioId) {
        if (usuariosIncluidos == null || usuariosIncluidos.length == 0) {
            return true; // Aplica para todos
        }
        
        for (UUID id : usuariosIncluidos) {
            if (id.equals(usuarioId)) {
                return true;
            }
        }
        return false;
    }

    public boolean aplicaParaCategoria(UUID categoriaId) {
        if (categoriasIncluidas == null || categoriasIncluidas.length == 0) {
            return true; // Aplica para todas las categorías
        }
        
        for (UUID id : categoriasIncluidas) {
            if (id.equals(categoriaId)) {
                return true;
            }
        }
        return false;
    }

    public boolean aplicaParaProducto(UUID productoId) {
        if (productosIncluidos == null || productosIncluidos.length == 0) {
            return true; // Aplica para todos los productos
        }
        
        for (UUID id : productosIncluidos) {
            if (id.equals(productoId)) {
                return true;
            }
        }
        return false;
    }

    public int getUsosRestantes() {
        if (usosMaximos == null) {
            return Integer.MAX_VALUE;
        }
        return Math.max(0, usosMaximos - usosActuales);
    }

    public double getPorcentajeUso() {
        if (usosMaximos == null || usosMaximos == 0) {
            return 0;
        }
        return (double) usosActuales / usosMaximos * 100;
    }
}