package com.dpattymoda.mapper;

import com.dpattymoda.dto.response.ChatResponse;
import com.dpattymoda.dto.response.MensajeResponse;
import com.dpattymoda.entity.Chat;
import com.dpattymoda.entity.Mensaje;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Mapper para conversión entre Chat/Mensaje y DTOs
 */
@Mapper(componentModel = "spring")
public abstract class ChatMapper {

    @Autowired
    private ObjectMapper objectMapper;

    @Mapping(target = "usuario", expression = "java(mapUsuarioBasico(chat.getUsuario()))")
    @Mapping(target = "empleadoAsignado", expression = "java(mapUsuarioBasico(chat.getEmpleadoAsignado()))")
    @Mapping(target = "producto", expression = "java(mapProductoBasico(chat.getProducto()))")
    @Mapping(target = "numeroPedido", expression = "java(chat.getPedido() != null ? chat.getPedido().getNumeroPedido() : null)")
    @Mapping(target = "etiquetas", expression = "java(arrayToList(chat.getEtiquetas()))")
    @Mapping(target = "tiempoPrimeraRespuesta", expression = "java(durationToString(chat.getTiempoPrimeraRespuesta()))")
    @Mapping(target = "tiempoResolucion", expression = "java(durationToString(chat.getTiempoResolucion()))")
    @Mapping(target = "mensajesNoLeidos", expression = "java((int)chat.getMensajesNoLeidos(getCurrentUserId()))")
    @Mapping(target = "totalMensajes", expression = "java(chat.getTotalMensajes())")
    public abstract ChatResponse toResponse(Chat chat);

    @Mapping(target = "remitente", expression = "java(mapRemitenteResponse(mensaje.getRemitente()))")
    @Mapping(target = "archivosAdjuntos", expression = "java(jsonToList(mensaje.getArchivosAdjuntos()))")
    public abstract MensajeResponse toResponse(Mensaje mensaje);

    protected ChatResponse.UsuarioBasicoResponse mapUsuarioBasico(com.dpattymoda.entity.Usuario usuario) {
        if (usuario == null) return null;
        
        return ChatResponse.UsuarioBasicoResponse.builder()
            .id(usuario.getId())
            .nombreCompleto(usuario.getNombreCompleto())
            .email(usuario.getEmail())
            .build();
    }

    protected com.dpattymoda.dto.response.ProductoBasicoResponse mapProductoBasico(com.dpattymoda.entity.Producto producto) {
        if (producto == null) return null;
        
        return com.dpattymoda.dto.response.ProductoBasicoResponse.builder()
            .id(producto.getId())
            .codigoProducto(producto.getCodigoProducto())
            .nombreProducto(producto.getNombreProducto())
            .descripcionCorta(producto.getDescripcionCorta())
            .marca(producto.getMarca())
            .precioBase(producto.getPrecioBase())
            .precioOferta(producto.getPrecioOferta())
            .precioVenta(producto.getPrecioVenta())
            .calificacionPromedio(producto.getCalificacionPromedio())
            .totalReseñas(producto.getTotalReseñas())
            .nombreCategoria(producto.getCategoria() != null ? producto.getCategoria().getNombreCategoria() : null)
            .build();
    }

    protected MensajeResponse.RemitenteResponse mapRemitenteResponse(com.dpattymoda.entity.Usuario usuario) {
        if (usuario == null) return null;
        
        return MensajeResponse.RemitenteResponse.builder()
            .id(usuario.getId())
            .nombreCompleto(usuario.getNombreCompleto())
            .email(usuario.getEmail())
            .rol(usuario.getRol() != null ? usuario.getRol().getNombreRol() : null)
            .build();
    }

    protected List<String> arrayToList(String[] array) {
        return array != null ? Arrays.asList(array) : null;
    }

    @SuppressWarnings("unchecked")
    protected List<String> jsonToList(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (JsonProcessingException e) {
            return null;
        }
    }

    protected String durationToString(Duration duration) {
        if (duration == null) return null;
        
        long horas = duration.toHours();
        long minutos = duration.toMinutes() % 60;
        
        if (horas > 0) {
            return String.format("%d horas y %d minutos", horas, minutos);
        } else {
            return String.format("%d minutos", minutos);
        }
    }

    // Método placeholder para obtener el ID del usuario actual
    // En una implementación real, esto se obtendría del contexto de seguridad
    protected UUID getCurrentUserId() {
        return UUID.randomUUID(); // Placeholder
    }
}