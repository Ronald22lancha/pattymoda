package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para respuesta de venta presencial
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información de venta presencial")
public class VentaPresencialResponse {

    @Schema(description = "ID del pedido")
    private UUID id;

    @Schema(description = "Número de pedido", example = "DPM20240001")
    private String numeroPedido;

    @Schema(description = "Información del vendedor")
    private VendedorResponse vendedor;

    @Schema(description = "Información de la caja")
    private CajaResponse caja;

    @Schema(description = "Subtotal de la venta", example = "150.00")
    private BigDecimal subtotal;

    @Schema(description = "Descuento total", example = "15.00")
    private BigDecimal descuentoTotal;

    @Schema(description = "Impuestos (IGV)", example = "24.30")
    private BigDecimal impuestosTotal;

    @Schema(description = "Total de la venta", example = "159.30")
    private BigDecimal total;

    @Schema(description = "Método de pago", example = "efectivo")
    private String metodoPago;

    @Schema(description = "Estado del pago", example = "procesado")
    private String estadoPago;

    @Schema(description = "Monto recibido", example = "200.00")
    private BigDecimal montoRecibido;

    @Schema(description = "Cambio entregado", example = "40.70")
    private BigDecimal cambio;

    @Schema(description = "Referencia de pago")
    private String referenciaPago;

    @Schema(description = "Items de la venta")
    private List<ItemVentaResponse> items;

    @Schema(description = "Datos del cliente")
    private DatosClienteResponse datosCliente;

    @Schema(description = "Comprobante requerido", example = "true")
    private Boolean comprobanteRequerido;

    @Schema(description = "Tipo de comprobante", example = "boleta")
    private String tipoComprobante;

    @Schema(description = "Número de comprobante", example = "B001-00000123")
    private String numeroComprobante;

    @Schema(description = "URL del comprobante PDF")
    private String urlComprobante;

    @Schema(description = "Notas de la venta")
    private String notas;

    @Schema(description = "Fecha de la venta")
    private LocalDateTime fechaVenta;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información del vendedor")
    public static class VendedorResponse {
        
        @Schema(description = "ID del vendedor")
        private UUID id;

        @Schema(description = "Nombre completo del vendedor")
        private String nombreCompleto;

        @Schema(description = "Email del vendedor")
        private String email;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información de la caja")
    public static class CajaResponse {
        
        @Schema(description = "ID de la caja")
        private UUID id;

        @Schema(description = "Número de caja")
        private String numeroCaja;

        @Schema(description = "Nombre de la caja")
        private String nombreCaja;

        @Schema(description = "Sucursal")
        private String nombreSucursal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Item de venta")
    public static class ItemVentaResponse {
        
        @Schema(description = "Información del producto")
        private ProductoVentaResponse producto;

        @Schema(description = "Cantidad vendida")
        private Integer cantidad;

        @Schema(description = "Precio unitario")
        private BigDecimal precioUnitario;

        @Schema(description = "Descuento unitario")
        private BigDecimal descuentoUnitario;

        @Schema(description = "Subtotal del item")
        private BigDecimal subtotal;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información del producto en venta")
    public static class ProductoVentaResponse {
        
        @Schema(description = "ID de la variante")
        private UUID varianteId;

        @Schema(description = "SKU de la variante")
        private String sku;

        @Schema(description = "Nombre del producto")
        private String nombreProducto;

        @Schema(description = "Talla")
        private String talla;

        @Schema(description = "Color")
        private String color;

        @Schema(description = "Imagen del producto")
        private String imagen;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Datos del cliente")
    public static class DatosClienteResponse {
        
        @Schema(description = "Nombre completo")
        private String nombreCompleto;

        @Schema(description = "DNI")
        private String dni;

        @Schema(description = "Teléfono")
        private String telefono;

        @Schema(description = "Email")
        private String email;
    }
}