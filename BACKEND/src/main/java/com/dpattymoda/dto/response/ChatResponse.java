package com.dpattymoda.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO para respuesta de chat
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Información del chat")
public class ChatResponse {

    @Schema(description = "ID del chat")
    private UUID id;

    @Schema(description = "Información del usuario")
    private UsuarioBasicoResponse usuario;

    @Schema(description = "Empleado asignado")
    private UsuarioBasicoResponse empleadoAsignado;

    @Schema(description = "Producto relacionado")
    private ProductoBasicoResponse producto;

    @Schema(description = "Pedido relacionado")
    private String numeroPedido;

    @Schema(description = "Asunto del chat")
    private String asunto;

    @Schema(description = "Estado del chat", example = "abierto")
    private String estado;

    @Schema(description = "Prioridad", example = "normal")
    private String prioridad;

    @Schema(description = "Categoría", example = "consulta_producto")
    private String categoria;

    @Schema(description = "Etiquetas")
    private List<String> etiquetas;

    @Schema(description = "Satisfacción del cliente", example = "5")
    private Integer satisfaccionCliente;

    @Schema(description = "Comentario de satisfacción")
    private String comentarioSatisfaccion;

    @Schema(description = "Fecha del primer mensaje")
    private LocalDateTime fechaPrimerMensaje;

    @Schema(description = "Fecha del último mensaje")
    private LocalDateTime fechaUltimoMensaje;

    @Schema(description = "Fecha de cierre")
    private LocalDateTime fechaCierre;

    @Schema(description = "Tiempo de primera respuesta")
    private String tiempoPrimeraRespuesta;

    @Schema(description = "Tiempo de resolución")
    private String tiempoResolucion;

    @Schema(description = "Motivo de cierre")
    private String motivoCierre;

    @Schema(description = "Mensajes no leídos", example = "3")
    private Integer mensajesNoLeidos;

    @Schema(description = "Total de mensajes", example = "15")
    private Integer totalMensajes;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Schema(description = "Usuario básico")
    public static class UsuarioBasicoResponse {
        private UUID id;
        private String nombreCompleto;
        private String email;
    }
}