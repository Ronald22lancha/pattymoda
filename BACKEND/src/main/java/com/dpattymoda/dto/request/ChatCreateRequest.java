package com.dpattymoda.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;
import java.util.UUID;

/**
 * DTO para crear nuevo chat
 */
@Data
@Schema(description = "Datos para crear nuevo chat")
public class ChatCreateRequest {

    @Schema(description = "Asunto del chat", example = "Consulta sobre producto")
    @NotBlank(message = "El asunto es requerido")
    @Size(max = 200, message = "El asunto no puede exceder 200 caracteres")
    private String asunto;

    @Schema(description = "Categoría del chat", example = "consulta_producto")
    private String categoria;

    @Schema(description = "ID del producto relacionado")
    private UUID productoId;

    @Schema(description = "ID del pedido relacionado")
    private UUID pedidoId;

    @Schema(description = "Prioridad del chat", example = "normal")
    private String prioridad = "normal";

    @Schema(description = "Mensaje inicial del chat")
    @NotBlank(message = "El mensaje inicial es requerido")
    private String mensajeInicial;

    @Schema(description = "Etiquetas del chat")
    private List<String> etiquetas;
}