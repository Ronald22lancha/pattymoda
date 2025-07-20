package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * DTO para crear comprobante electrónico
 */
@Data
@Schema(description = "Datos para generar comprobante")
public class ComprobanteCreateRequest {

    @Schema(description = "Tipo de comprobante", example = "boleta")
    @NotBlank(message = "El tipo de comprobante es requerido")
    private String tipoComprobante; // boleta, factura

    @Schema(description = "Serie del comprobante", example = "B001")
    private String serie;

    @Schema(description = "Documento del receptor", example = "12345678")
    @NotBlank(message = "El documento del receptor es requerido")
    private String documentoReceptor;

    @Schema(description = "Tipo de documento", example = "DNI")
    @NotBlank(message = "El tipo de documento es requerido")
    private String tipoDocumentoReceptor; // DNI, RUC, CE

    @Schema(description = "Nombre o razón social del receptor")
    @NotBlank(message = "El nombre del receptor es requerido")
    private String nombreReceptor;

    @Schema(description = "Dirección del receptor")
    private String direccionReceptor;

    @Schema(description = "Email para envío del comprobante")
    private String emailReceptor;

    @Schema(description = "Observaciones adicionales")
    private String observaciones;

    @Schema(description = "Enviar por email automáticamente", example = "true")
    private Boolean enviarPorEmail = true;

    @Schema(description = "Generar PDF automáticamente", example = "true")
    private Boolean generarPdf = true;
}