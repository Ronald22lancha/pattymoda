package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

/**
 * Entidad Promocion para promociones automáticas
 */
@Entity
@Table(name = "promociones")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Promocion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "nombre_promocion", nullable = false, length = 100)
    private String nombrePromocion;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "tipo_promocion", nullable = false, length = 30)
    private String tipoPromocion; // 2x1, 3x2, descuento_cantidad, descuento_categoria

    @Column(name = "condiciones", nullable = false, columnDefinition = "jsonb")
    private String condiciones; // Condiciones específicas de la promoción

    @Column(name = "descuento", nullable = false, columnDefinition = "jsonb")
    private String descuento; // Configuración del descuento

    @Builder.Default
    @Column(name = "prioridad")
    private Integer prioridad = 1; // Orden de aplicación

    @Column(name = "fecha_inicio", nullable = false)
    private LocalDateTime fechaInicio;

    @Column(name = "fecha_fin", nullable = false)
    private LocalDateTime fechaFin;

    @Column(name = "dias_semana")
    private Integer[] diasSemana; // 1=Lunes, 7=Domingo

    @Column(name = "horas_inicio")
    private LocalTime horasInicio;

    @Column(name = "horas_fin")
    private LocalTime horasFin;

    @Column(name = "sucursales_incluidas")
    private UUID[] sucursalesIncluidas;

    @Column(name = "categorias_incluidas")
    private UUID[] categoriasIncluidas;

    @Column(name = "productos_incluidos")
    private UUID[] productosIncluidos;

    @Builder.Default
    @Column(name = "aplicable_online")
    private Boolean aplicableOnline = true;

    @Builder.Default
    @Column(name = "aplicable_presencial")
    private Boolean aplicablePresencial = true;

    @Column(name = "limite_usos")
    private Integer limiteUsos;

    @Builder.Default
    @Column(name = "usos_actuales")
    private Integer usosActuales = 0;

    @Builder.Default
    @Column(name = "activa")
    private Boolean activa = true;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    @LastModifiedDate
    @Column(name = "fecha_actualizacion")
    private LocalDateTime fechaActualizacion;

    // Métodos de utilidad
    public boolean estaActiva() {
        return activa != null && activa;
    }

    public boolean estaVigente() {
        LocalDateTime ahora = LocalDateTime.now();
        return fechaInicio.isBefore(ahora) && fechaFin.isAfter(ahora);
    }

    public boolean aplicaEnHorario() {
        if (horasInicio == null || horasFin == null) {
            return true; // Sin restricción horaria
        }
        
        LocalTime ahora = LocalTime.now();
        return !ahora.isBefore(horasInicio) && !ahora.isAfter(horasFin);
    }

    public boolean aplicaEnDia() {
        if (diasSemana == null || diasSemana.length == 0) {
            return true; // Todos los días
        }
        
        int diaActual = LocalDateTime.now().getDayOfWeek().getValue();
        for (Integer dia : diasSemana) {
            if (dia == diaActual) {
                return true;
            }
        }
        return false;
    }

    public boolean puedeAplicarse() {
        return estaActiva() && estaVigente() && aplicaEnHorario() && 
               aplicaEnDia() && !estaAgotada();
    }

    public boolean estaAgotada() {
        return limiteUsos != null && usosActuales >= limiteUsos;
    }

    public boolean aplicaParaSucursal(UUID sucursalId) {
        if (sucursalesIncluidas == null || sucursalesIncluidas.length == 0) {
            return true; // Aplica para todas las sucursales
        }
        
        for (UUID id : sucursalesIncluidas) {
            if (id.equals(sucursalId)) {
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

    public boolean aplicaParaTipoVenta(String tipoVenta) {
        if ("online".equals(tipoVenta)) {
            return aplicableOnline != null && aplicableOnline;
        } else if ("presencial".equals(tipoVenta)) {
            return aplicablePresencial != null && aplicablePresencial;
        }
        return false;
    }

    public void usar() {
        this.usosActuales++;
    }

    public int getUsosRestantes() {
        if (limiteUsos == null) {
            return Integer.MAX_VALUE;
        }
        return Math.max(0, limiteUsos - usosActuales);
    }

    public boolean es2x1() {
        return "2x1".equals(tipoPromocion);
    }

    public boolean es3x2() {
        return "3x2".equals(tipoPromocion);
    }

    public boolean esDescuentoPorCantidad() {
        return "descuento_cantidad".equals(tipoPromocion);
    }

    public boolean esDescuentoPorCategoria() {
        return "descuento_categoria".equals(tipoPromocion);
    }
}