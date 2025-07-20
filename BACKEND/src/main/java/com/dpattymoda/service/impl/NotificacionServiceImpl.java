package com.dpattymoda.service.impl;

import com.dpattymoda.dto.request.NotificacionCreateRequest;
import com.dpattymoda.dto.response.NotificacionResponse;
import com.dpattymoda.entity.*;
import com.dpattymoda.exception.ResourceNotFoundException;
import com.dpattymoda.repository.*;
import com.dpattymoda.service.EmailService;
import com.dpattymoda.service.NotificacionService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Implementación del servicio de notificaciones
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class NotificacionServiceImpl implements NotificacionService {

    private final NotificacionRepository notificacionRepository;
    private final UsuarioRepository usuarioRepository;
    private final PedidoRepository pedidoRepository;
    private final ProductoRepository productoRepository;
    private final CarritoRepository carritoRepository;
    private final ChatRepository chatRepository;
    private final EmailService emailService;
    private final ObjectMapper objectMapper;

    @Override
    public NotificacionResponse enviarNotificacion(UUID usuarioId, NotificacionCreateRequest request) {
        log.info("Enviando notificación a usuario: {}", usuarioId);

        Usuario usuario = usuarioRepository.findById(usuarioId)
            .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Notificacion notificacion = Notificacion.builder()
            .usuario(usuario)
            .tipo(request.getTipo())
            .titulo(request.getTitulo())
            .mensaje(request.getMensaje())
            .canal(request.getCanal())
            .datos(convertirAJson(request.getDatos()))
            .urlAccion(request.getUrlAccion())
            .prioridad(request.getPrioridad())
            .expiraEn(request.getExpiraEn())
            .build();

        notificacion = notificacionRepository.save(notificacion);

        // Enviar inmediatamente si no está programada
        if (request.getProgramarPara() == null || request.getProgramarPara().isBefore(LocalDateTime.now())) {
            enviarNotificacionInmediata(notificacion);
        }

        return convertirAResponse(notificacion);
    }

    @Override
    public void enviarNotificacionMasiva(String rol, NotificacionCreateRequest request) {
        log.info("Enviando notificación masiva a rol: {}", rol);

        List<Usuario> usuarios = usuarioRepository.findByRol_NombreRolAndActivoTrue(rol);
        
        for (Usuario usuario : usuarios) {
            try {
                enviarNotificacion(usuario.getId(), request);
            } catch (Exception e) {
                log.error("Error enviando notificación masiva a usuario {}: {}", usuario.getId(), e.getMessage());
            }
        }
    }

    @Override
    @Transactional(readOnly = true)
    public Page<NotificacionResponse> obtenerNotificacionesUsuario(UUID usuarioId, Pageable pageable) {
        return notificacionRepository.findByUsuario_IdOrderByFechaCreacionDesc(usuarioId, pageable)
            .map(this::convertirAResponse);
    }

    @Override
    public void marcarComoLeida(UUID notificacionId, UUID usuarioId) {
        Notificacion notificacion = notificacionRepository.findById(notificacionId)
            .orElseThrow(() -> new ResourceNotFoundException("Notificación no encontrada"));

        if (!notificacion.getUsuario().getId().equals(usuarioId)) {
            throw new IllegalArgumentException("No tienes permisos para esta notificación");
        }

        notificacion.marcarComoLeida();
        notificacionRepository.save(notificacion);
    }

    @Override
    public void marcarTodasComoLeidas(UUID usuarioId) {
        notificacionRepository.marcarTodasComoLeidasPorUsuario(usuarioId, LocalDateTime.now());
    }

    @Override
    @Transactional(readOnly = true)
    public Integer contarNoLeidas(UUID usuarioId) {
        return notificacionRepository.contarNoLeidasPorUsuario(usuarioId);
    }

    @Override
    public void eliminarNotificacion(UUID notificacionId, UUID usuarioId) {
        notificacionRepository.deleteByUsuario_IdAndId(usuarioId, notificacionId);
    }

    @Override
    public void notificarPedidoConfirmado(UUID pedidoId) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (pedido.getUsuario() != null) {
            NotificacionCreateRequest request = new NotificacionCreateRequest();
            request.setTipo("pedido_confirmado");
            request.setTitulo("Pedido Confirmado");
            request.setMensaje("Tu pedido " + pedido.getNumeroPedido() + " ha sido confirmado y está siendo procesado.");
            request.setCanal("email");
            request.setUrlAccion("/pedidos/" + pedido.getId());

            enviarNotificacion(pedido.getUsuario().getId(), request);
        }
    }

    @Override
    public void notificarCambioEstadoPedido(UUID pedidoId, String nuevoEstado) {
        Pedido pedido = pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado"));

        if (pedido.getUsuario() != null) {
            String mensaje = generarMensajeEstadoPedido(pedido.getNumeroPedido(), nuevoEstado);
            
            NotificacionCreateRequest request = new NotificacionCreateRequest();
            request.setTipo("cambio_estado_pedido");
            request.setTitulo("Actualización de Pedido");
            request.setMensaje(mensaje);
            request.setCanal("email");
            request.setUrlAccion("/pedidos/" + pedido.getId());

            enviarNotificacion(pedido.getUsuario().getId(), request);
        }
    }

    @Override
    public void notificarStockBajo(UUID productoId) {
        // Notificar a administradores y empleados
        List<Usuario> empleados = usuarioRepository.findByRolesAndActivoTrue(
            List.of("Administrador", "Empleado"));

        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado"));

        NotificacionCreateRequest request = new NotificacionCreateRequest();
        request.setTipo("stock_bajo");
        request.setTitulo("Stock Bajo");
        request.setMensaje("El producto " + producto.getNombreProducto() + " tiene stock bajo.");
        request.setCanal("in_app");
        request.setPrioridad("alta");
        request.setUrlAccion("/admin/inventario/" + productoId);

        for (Usuario empleado : empleados) {
            enviarNotificacion(empleado.getId(), request);
        }
    }

    @Override
    public void notificarNuevoMensajeChat(UUID chatId, UUID destinatarioId) {
        NotificacionCreateRequest request = new NotificacionCreateRequest();
        request.setTipo("nuevo_mensaje_chat");
        request.setTitulo("Nuevo Mensaje");
        request.setMensaje("Tienes un nuevo mensaje en el chat.");
        request.setCanal("push");
        request.setUrlAccion("/chat/" + chatId);

        enviarNotificacion(destinatarioId, request);
    }

    @Override
    public void notificarCarritoAbandonado(UUID carritoId) {
        Carrito carrito = carritoRepository.findById(carritoId)
            .orElseThrow(() -> new ResourceNotFoundException("Carrito no encontrado"));

        if (carrito.getUsuario() != null) {
            NotificacionCreateRequest request = new NotificacionCreateRequest();
            request.setTipo("carrito_abandonado");
            request.setTitulo("¡No olvides tu carrito!");
            request.setMensaje("Tienes productos esperándote en tu carrito. ¡Completa tu compra!");
            request.setCanal("email");
            request.setUrlAccion("/carrito");

            enviarNotificacion(carrito.getUsuario().getId(), request);
        }
    }

    @Override
    public void notificarOfertaEspecial(UUID usuarioId, String codigoCupon) {
        NotificacionCreateRequest request = new NotificacionCreateRequest();
        request.setTipo("oferta_especial");
        request.setTitulo("¡Oferta Especial para Ti!");
        request.setMensaje("Usa el código " + codigoCupon + " y obtén un descuento especial.");
        request.setCanal("email");
        request.setUrlAccion("/ofertas");

        enviarNotificacion(usuarioId, request);
    }

    @Override
    public void configurarPreferencias(UUID usuarioId, String tipoNotificacion, boolean habilitado) {
        // Implementar lógica para guardar preferencias en el perfil del usuario
        log.info("Configurando preferencia {} = {} para usuario {}", tipoNotificacion, habilitado, usuarioId);
    }

    @Override
    public void procesarColaPendientes() {
        log.info("Procesando cola de notificaciones pendientes");

        List<Notificacion> pendientes = notificacionRepository
            .findNotificacionesPendientesEnvio(LocalDateTime.now());

        for (Notificacion notificacion : pendientes) {
            try {
                enviarNotificacionInmediata(notificacion);
            } catch (Exception e) {
                log.error("Error procesando notificación {}: {}", notificacion.getId(), e.getMessage());
                notificacion.registrarErrorEnvio(e.getMessage());
                notificacionRepository.save(notificacion);
            }
        }
    }

    // Métodos privados de utilidad

    private void enviarNotificacionInmediata(Notificacion notificacion) {
        try {
            switch (notificacion.getCanal()) {
                case "email":
                    enviarPorEmail(notificacion);
                    break;
                case "push":
                    enviarPorPush(notificacion);
                    break;
                case "sms":
                    enviarPorSms(notificacion);
                    break;
                case "in_app":
                    // Ya está guardada en BD, no requiere envío externo
                    break;
                default:
                    log.warn("Canal de notificación no soportado: {}", notificacion.getCanal());
            }

            notificacion.marcarComoEnviada();
            notificacionRepository.save(notificacion);

        } catch (Exception e) {
            log.error("Error enviando notificación {}: {}", notificacion.getId(), e.getMessage());
            notificacion.registrarErrorEnvio(e.getMessage());
            notificacionRepository.save(notificacion);
        }
    }

    private void enviarPorEmail(Notificacion notificacion) {
        String email = notificacion.getUsuario().getEmail();
        String asunto = notificacion.getTitulo();
        String contenido = notificacion.getMensaje();

        emailService.enviarPromocion(email, asunto, contenido);
    }

    private void enviarPorPush(Notificacion notificacion) {
        // Implementar envío de push notifications
        log.info("Enviando push notification a usuario: {}", notificacion.getUsuario().getId());
    }

    private void enviarPorSms(Notificacion notificacion) {
        // Implementar envío de SMS
        log.info("Enviando SMS a usuario: {}", notificacion.getUsuario().getId());
    }

    private String generarMensajeEstadoPedido(String numeroPedido, String estado) {
        switch (estado) {
            case "procesando":
                return "Tu pedido " + numeroPedido + " está siendo preparado.";
            case "enviado":
                return "Tu pedido " + numeroPedido + " ha sido enviado.";
            case "entregado":
                return "Tu pedido " + numeroPedido + " ha sido entregado.";
            case "cancelado":
                return "Tu pedido " + numeroPedido + " ha sido cancelado.";
            default:
                return "Tu pedido " + numeroPedido + " ha cambiado de estado a: " + estado;
        }
    }

    private NotificacionResponse convertirAResponse(Notificacion notificacion) {
        return NotificacionResponse.builder()
            .id(notificacion.getId())
            .tipo(notificacion.getTipo())
            .titulo(notificacion.getTitulo())
            .mensaje(notificacion.getMensaje())
            .canal(notificacion.getCanal())
            .datos(convertirDeJson(notificacion.getDatos()))
            .urlAccion(notificacion.getUrlAccion())
            .prioridad(notificacion.getPrioridad())
            .leida(notificacion.getLeida())
            .fechaLectura(notificacion.getFechaLectura())
            .enviada(notificacion.getEnviada())
            .fechaEnvio(notificacion.getFechaEnvio())
            .intentosEnvio(notificacion.getIntentosEnvio())
            .errorEnvio(notificacion.getErrorEnvio())
            .expiraEn(notificacion.getExpiraEn())
            .fechaCreacion(notificacion.getFechaCreacion())
            .build();
    }

    private String convertirAJson(Object objeto) {
        if (objeto == null) return null;
        try {
            return objectMapper.writeValueAsString(objeto);
        } catch (JsonProcessingException e) {
            log.warn("Error al convertir objeto a JSON: {}", e.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> convertirDeJson(String json) {
        if (json == null || json.isEmpty()) return null;
        try {
            return objectMapper.readValue(json, new TypeReference<Map<String, Object>>() {});
        } catch (JsonProcessingException e) {
            log.warn("Error al convertir JSON a objeto: {}", e.getMessage());
            return null;
        }
    }
}