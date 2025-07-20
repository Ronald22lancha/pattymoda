package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * DTO para registrar venta presencial
 */
@Data
@Schema(description = "Datos para registrar venta presencial")
public class VentaPresencialRequest {

    @Schema(description = "Items de la venta")
    @NotEmpty(message = "La venta debe tener al menos un item")
    @Valid
    private List<ItemVentaRequest> items;

    @Schema(description = "Método de pago", example = "efectivo")
    @NotNull(message = "El método de pago es requerido")
    private String metodoPago;

    @Schema(description = "Monto recibido (para efectivo)", example = "100.00")
    private BigDecimal montoRecibido;

    @Schema(description = "Referencia de pago (voucher, operación)", example = "OP123456")
    private String referenciaPago;

    @Schema(description = "Descuento total aplicado", example = "10.00")
    private BigDecimal descuentoTotal = BigDecimal.ZERO;

    @Schema(description = "Código de cupón aplicado")
    private String codigoCupon;

    @Schema(description = "Datos del cliente (para ventas sin registro)")
    private DatosClienteRequest datosCliente;

    @Schema(description = "Requiere comprobante", example = "true")
    private Boolean requiereComprobante = false;

    @Schema(description = "Tipo de comprobante", example = "boleta")
    private String tipoComprobante; // boleta, factura

    @Schema(description = "Datos para facturación")
    private DatosFacturacionRequest datosFacturacion;

    @Schema(description = "Notas adicionales de la venta")
    private String notas;

    @Data
    @Schema(description = "Item de venta")
    public static class ItemVentaRequest {
        
        @Schema(description = "ID de la variante del producto")
        @NotNull(message = "La variante es requerida")
        private UUID varianteId;

        @Schema(description = "Cantidad", example = "2")
        @NotNull(message = "La cantidad es requerida")
        @Positive(message = "La cantidad debe ser positiva")
        private Integer cantidad;

        @Schema(description = "Precio unitario", example = "75.00")
        @NotNull(message = "El precio unitario es requerido")
        @Positive(message = "El precio debe ser positivo")
        private BigDecimal precioUnitario;

        @Schema(description = "Descuento unitario", example = "5.00")
        private BigDecimal descuentoUnitario = BigDecimal.ZERO;
    }

    @Data
    @Schema(description = "Datos del cliente para venta presencial")
    public static class DatosClienteRequest {
        
        @Schema(description = "Nombres del cliente", example = "Juan Carlos")
        private String nombres;

        @Schema(description = "Apellidos del cliente", example = "García López")
        private String apellidos;

        @Schema(description = "DNI del cliente", example = "12345678")
        private String dni;

        @Schema(description = "Teléfono del cliente", example = "987654321")
        private String telefono;

        @Schema(description = "Email del cliente", example = "cliente@email.com")
        private String email;
    }

    @Data
    @Schema(description = "Datos para facturación")
    public static class DatosFacturacionRequest {
        
        @Schema(description = "RUC de la empresa", example = "20123456789")
        @NotNull(message = "El RUC es requerido para factura")
        private String ruc;

        @Schema(description = "Razón social", example = "Empresa SAC")
        @NotNull(message = "La razón social es requerida para factura")
        private String razonSocial;

        @Schema(description = "Dirección fiscal")
        @NotNull(message = "La dirección fiscal es requerida para factura")
        private String direccionFiscal;
    }
}