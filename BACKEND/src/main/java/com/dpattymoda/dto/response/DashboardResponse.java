package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO para dashboard administrativo
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Dashboard administrativo")
public class DashboardResponse {

    @Schema(description = "Métricas principales")
    private MetricasPrincipales metricas;

    @Schema(description = "Gráfico de ventas")
    private GraficoVentas graficoVentas;

    @Schema(description = "Productos destacados")
    private List<ProductoDestacado> productosDestacados;

    @Schema(description = "Pedidos recientes")
    private List<PedidoReciente> pedidosRecientes;

    @Schema(description = "Alertas del sistema")
    private List<AlertaSistema> alertas;

    @Schema(description = "Actividad reciente")
    private List<ActividadReciente> actividadReciente;

    @Schema(description = "Estadísticas de empleados")
    private EstadisticasEmpleados estadisticasEmpleados;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Métricas principales")
    public static class MetricasPrincipales {
        private BigDecimal ventasHoy;
        private BigDecimal ventasSemana;
        private BigDecimal ventasMes;
        private Integer pedidosHoy;
        private Integer pedidosPendientes;
        private Integer clientesNuevosHoy;
        private Integer productosStockBajo;
        private BigDecimal tasaConversionHoy;
        private BigDecimal ticketPromedioHoy;
        private Integer chatsPendientes;
        private Integer devolucionesPendientes;
        private BigDecimal crecimientoVentas;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Gráfico de ventas")
    public static class GraficoVentas {
        private List<String> etiquetas; // Fechas
        private List<BigDecimal> ventasOnline;
        private List<BigDecimal> ventasPresenciales;
        private List<Integer> pedidos;
        private String periodo; // "7_dias", "30_dias", "12_meses"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Producto destacado")
    public static class ProductoDestacado {
        private String nombreProducto;
        private String imagen;
        private Integer ventasHoy;
        private BigDecimal ingresoHoy;
        private Integer stockActual;
        private String tendencia; // "subiendo", "bajando", "estable"
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Pedido reciente")
    public static class PedidoReciente {
        private String numeroPedido;
        private String nombreCliente;
        private BigDecimal total;
        private String estado;
        private String tipoVenta;
        private LocalDateTime fechaCreacion;
        private Integer minutosTranscurridos;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Alerta del sistema")
    public static class AlertaSistema {
        private String tipo; // "stock_bajo", "pedido_urgente", "error_sistema"
        private String titulo;
        private String mensaje;
        private String nivel; // "info", "warning", "error", "critical"
        private String icono;
        private String urlAccion;
        private LocalDateTime fechaCreacion;
        private boolean requiereAccion;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Actividad reciente")
    public static class ActividadReciente {
        private String accion;
        private String descripcion;
        private String usuario;
        private String modulo;
        private LocalDateTime fecha;
        private String icono;
        private String color;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Estadísticas de empleados")
    public static class EstadisticasEmpleados {
        private Integer empleadosActivos;
        private Integer empleadosConectados;
        private Map<String, Integer> ventasPorEmpleado;
        private String mejorVendedorHoy;
        private Integer chatsAtendidos;
        private Double satisfaccionPromedio;
    }
}