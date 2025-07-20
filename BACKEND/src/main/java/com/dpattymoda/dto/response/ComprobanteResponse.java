package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO para respuesta de comprobante electrónico
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del comprobante electrónico")
public class ComprobanteResponse {

    @Schema(description = "ID del comprobante")
    private UUID id;

    @Schema(description = "Información del pedido")
    private PedidoComprobanteResponse pedido;

    @Schema(description = "Tipo de comprobante", example = "boleta")
    private String tipoComprobante;

    @Schema(description = "Serie", example = "B001")
    private String serie;

    @Schema(description = "Número", example = "123")
    private Integer numero;

    @Schema(description = "Número completo", example = "B001-00000123")
    private String numeroCompleto;

    @Schema(description = "RUC del emisor")
    private String rucEmisor;

    @Schema(description = "Razón social del emisor")
    private String razonSocialEmisor;

    @Schema(description = "Dirección del emisor")
    private String direccionEmisor;

    @Schema(description = "Documento del receptor")
    private String documentoReceptor;

    @Schema(description = "Tipo de documento del receptor")
    private String tipoDocumentoReceptor;

    @Schema(description = "Nombre del receptor")
    private String nombreReceptor;

    @Schema(description = "Dirección del receptor")
    private String direccionReceptor;

    @Schema(description = "Subtotal", example = "100.00")
    private BigDecimal subtotal;

    @Schema(description = "IGV", example = "18.00")
    private BigDecimal igv;

    @Schema(description = "Total", example = "118.00")
    private BigDecimal total;

    @Schema(description = "Moneda", example = "PEN")
    private String moneda;

    @Schema(description = "Fecha de emisión")
    private LocalDateTime fechaEmision;

    @Schema(description = "Fecha de vencimiento")
    private LocalDateTime fechaVencimiento;

    @Schema(description = "Estado en SUNAT", example = "aceptado")
    private String estadoSunat;

    @Schema(description = "Código hash")
    private String codigoHash;

    @Schema(description = "URL del PDF")
    private String pdfUrl;

    @Schema(description = "Observaciones")
    private String observaciones;

    @Schema(description = "Fecha de envío a SUNAT")
    private LocalDateTime fechaEnvioSunat;

    @Schema(description = "Fecha de respuesta de SUNAT")
    private LocalDateTime fechaRespuestaSunat;

    @Schema(description = "Código de respuesta SUNAT")
    private String codigoRespuestaSunat;

    @Schema(description = "Descripción de respuesta SUNAT")
    private String descripcionRespuestaSunat;

    @Schema(description = "Anulado", example = "false")
    private Boolean anulado;

    @Schema(description = "Fecha de anulación")
    private LocalDateTime fechaAnulacion;

    @Schema(description = "Motivo de anulación")
    private String motivoAnulacion;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Información del pedido para comprobante")
    public static class PedidoComprobanteResponse {
        private UUID id;
        private String numeroPedido;
        private LocalDateTime fechaCreacion;
        private BigDecimal total;
    }
}