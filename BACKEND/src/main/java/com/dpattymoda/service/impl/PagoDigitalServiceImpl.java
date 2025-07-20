package com.dpattymoda.service.impl;

import com.dpattymoda.dto.request.PagoDigitalRequest;
import com.dpattymoda.dto.response.PagoDigitalResponse;
import com.dpattymoda.entity.Pago;
import com.dpattymoda.entity.Pedido;
import com.dpattymoda.exception.BusinessException;
import com.dpattymoda.exception.ResourceNotFoundException;
import com.dpattymoda.repository.PagoRepository;
import com.dpattymoda.repository.PedidoRepository;
import com.dpattymoda.service.AuditoriaService;
import com.dpattymoda.service.PagoDigitalService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Implementación del servicio de pagos digitales
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PagoDigitalServiceImpl implements PagoDigitalService {

    private final PedidoRepository pedidoRepository;
    private final PagoRepository pagoRepository;
    private final AuditoriaService auditoriaService;
    private final ObjectMapper objectMapper;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${app.pagos.yape.enabled:true}")
    private boolean yapeEnabled;

    @Value("${app.pagos.plin.enabled:true}")
    private boolean plinEnabled;

    @Value("${app.pagos.lukita.enabled:true}")
    private boolean lukitaEnabled;

    @Value("${app.pagos.yape.numero:}")
    private String numeroYape;

    @Value("${app.pagos.plin.numero:}")
    private String numeroPlin;

    @Value("${app.pagos.lukita.numero:}")
    private String numeroLukita;

    @Override
    public PagoDigitalResponse procesarPagoYape(UUID pedidoId, PagoDigitalRequest request) {
        log.info("Procesando pago Yape para pedido: {}", pedidoId);

        if (!yapeEnabled) {
            throw new BusinessException("Pagos con Yape no están habilitados");
        }

        Pedido pedido = obtenerPedido(pedidoId);
        validarPedidoParaPago(pedido);

        // Crear registro de pago
        Pago pago = crearRegistroPago(pedido, "yape", request.getMonto());

        try {
            // Simular integración con API de Yape
            String referenciaExterna = "YAPE_" + System.currentTimeMillis();
            String urlQR = generarURLQR("yape", pedido.getTotal(), referenciaExterna);

            // Actualizar pago con referencia externa
            pago.setReferenciaExterna(referenciaExterna);
            pago.setDatosTransaccion(convertirAJson(request));
            pago.setFechaVencimiento(LocalDateTime.now().plusMinutes(15));
            pagoRepository.save(pago);

            // Auditar transacción
            auditoriaService.registrarAccion("PAGO_YAPE_INICIADO", "pagos", pago.getId(),
                null, convertirAJson(pago), "Pago Yape iniciado: " + referenciaExterna);

            return PagoDigitalResponse.builder()
                .referenciaExterna(referenciaExterna)
                .estado("pendiente")
                .metodoPago("yape")
                .monto(request.getMonto())
                .urlQR(urlQR)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(15))
                .mensaje("Escanea el código QR con tu app Yape para completar el pago")
                .exitoso(true)
                .build();

        } catch (Exception e) {
            pago.fallarPago("Error al procesar pago Yape: " + e.getMessage());
            pagoRepository.save(pago);
            throw new BusinessException("Error al procesar pago con Yape: " + e.getMessage());
        }
    }

    @Override
    public PagoDigitalResponse procesarPagoPlin(UUID pedidoId, PagoDigitalRequest request) {
        log.info("Procesando pago Plin para pedido: {}", pedidoId);

        if (!plinEnabled) {
            throw new BusinessException("Pagos con Plin no están habilitados");
        }

        Pedido pedido = obtenerPedido(pedidoId);
        validarPedidoParaPago(pedido);

        // Crear registro de pago
        Pago pago = crearRegistroPago(pedido, "plin", request.getMonto());

        try {
            // Simular integración con API de Plin
            String referenciaExterna = "PLIN_" + System.currentTimeMillis();
            String urlQR = generarURLQR("plin", pedido.getTotal(), referenciaExterna);

            // Actualizar pago con referencia externa
            pago.setReferenciaExterna(referenciaExterna);
            pago.setDatosTransaccion(convertirAJson(request));
            pago.setFechaVencimiento(LocalDateTime.now().plusMinutes(10));
            pagoRepository.save(pago);

            // Auditar transacción
            auditoriaService.registrarAccion("PAGO_PLIN_INICIADO", "pagos", pago.getId(),
                null, convertirAJson(pago), "Pago Plin iniciado: " + referenciaExterna);

            return PagoDigitalResponse.builder()
                .referenciaExterna(referenciaExterna)
                .estado("pendiente")
                .metodoPago("plin")
                .monto(request.getMonto())
                .urlQR(urlQR)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(10))
                .mensaje("Escanea el código QR con tu app Plin para completar el pago")
                .exitoso(true)
                .build();

        } catch (Exception e) {
            pago.fallarPago("Error al procesar pago Plin: " + e.getMessage());
            pagoRepository.save(pago);
            throw new BusinessException("Error al procesar pago con Plin: " + e.getMessage());
        }
    }

    @Override
    public PagoDigitalResponse procesarPagoLukita(UUID pedidoId, PagoDigitalRequest request) {
        log.info("Procesando pago Lukita para pedido: {}", pedidoId);

        if (!lukitaEnabled) {
            throw new BusinessException("Pagos con Lukita no están habilitados");
        }

        Pedido pedido = obtenerPedido(pedidoId);
        validarPedidoParaPago(pedido);

        // Crear registro de pago
        Pago pago = crearRegistroPago(pedido, "lukita", request.getMonto());

        try {
            // Simular integración con API de Lukita
            String referenciaExterna = "LUKITA_" + System.currentTimeMillis();
            String urlQR = generarURLQR("lukita", pedido.getTotal(), referenciaExterna);

            // Actualizar pago con referencia externa
            pago.setReferenciaExterna(referenciaExterna);
            pago.setDatosTransaccion(convertirAJson(request));
            pago.setFechaVencimiento(LocalDateTime.now().plusMinutes(20));
            pagoRepository.save(pago);

            // Auditar transacción
            auditoriaService.registrarAccion("PAGO_LUKITA_INICIADO", "pagos", pago.getId(),
                null, convertirAJson(pago), "Pago Lukita iniciado: " + referenciaExterna);

            return PagoDigitalResponse.builder()
                .referenciaExterna(referenciaExterna)
                .estado("pendiente")
                .metodoPago("lukita")
                .monto(request.getMonto())
                .urlQR(urlQR)
                .fechaExpiracion(LocalDateTime.now().plusMinutes(20))
                .mensaje("Escanea el código QR con tu app Lukita para completar el pago")
                .exitoso(true)
                .build();

        } catch (Exception e) {
            pago.fallarPago("Error al procesar pago Lukita: " + e.getMessage());
            pagoRepository.save(pago);
            throw new BusinessException("Error al procesar pago con Lukita: " + e.getMessage());
        }
    }

    @Override
    public String generarQRPago(UUID pedidoId, String metodoPago) {
        Pedido pedido = obtenerPedido(pedidoId);
        String referencia = metodoPago.toUpperCase() + "_" + System.currentTimeMillis();
        return generarURLQR(metodoPago, pedido.getTotal(), referencia);
    }

    @Override
    public PagoDigitalResponse verificarEstadoPago(String referenciaExterna) {
        log.info("Verificando estado de pago: {}", referenciaExterna);

        Pago pago = pagoRepository.findByReferenciaExterna(referenciaExterna)
            .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + referenciaExterna));

        // Simular consulta a API externa
        String estadoActual = simularConsultaEstadoPago(referenciaExterna);

        return PagoDigitalResponse.builder()
            .referenciaExterna(referenciaExterna)
            .estado(estadoActual)
            .metodoPago(pago.getMetodoPago())
            .monto(pago.getMonto())
            .exitoso("procesado".equals(estadoActual))
            .build();
    }

    @Override
    public void confirmarPagoDigital(String referenciaExterna, String estadoPago) {
        log.info("Confirmando pago digital: {} con estado: {}", referenciaExterna, estadoPago);

        Pago pago = pagoRepository.findByReferenciaExterna(referenciaExterna)
            .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + referenciaExterna));

        if ("procesado".equals(estadoPago)) {
            pago.procesarPago(referenciaExterna, "Pago confirmado por billetera digital");
            
            // Actualizar estado del pedido
            Pedido pedido = pago.getPedido();
            pedido.setEstadoPago("procesado");
            pedido.setEstado("confirmado");
            pedidoRepository.save(pedido);

            // Auditar confirmación
            auditoriaService.registrarAccion("PAGO_DIGITAL_CONFIRMADO", "pagos", pago.getId(),
                null, null, "Pago digital confirmado: " + referenciaExterna);

        } else if ("fallido".equals(estadoPago)) {
            pago.fallarPago("Pago rechazado por billetera digital");
        }

        pagoRepository.save(pago);
    }

    @Override
    public void cancelarPagoDigital(String referenciaExterna, String motivo) {
        log.info("Cancelando pago digital: {} por: {}", referenciaExterna, motivo);

        Pago pago = pagoRepository.findByReferenciaExterna(referenciaExterna)
            .orElseThrow(() -> new ResourceNotFoundException("Pago no encontrado: " + referenciaExterna));

        pago.fallarPago("Pago cancelado: " + motivo);
        pagoRepository.save(pago);

        // Auditar cancelación
        auditoriaService.registrarAccion("PAGO_DIGITAL_CANCELADO", "pagos", pago.getId(),
            null, null, "Pago digital cancelado: " + motivo);
    }

    // Métodos privados de utilidad

    private Pedido obtenerPedido(UUID pedidoId) {
        return pedidoRepository.findById(pedidoId)
            .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado: " + pedidoId));
    }

    private void validarPedidoParaPago(Pedido pedido) {
        if (!"pendiente".equals(pedido.getEstadoPago())) {
            throw new BusinessException("El pedido ya tiene el pago procesado");
        }

        if ("cancelado".equals(pedido.getEstado())) {
            throw new BusinessException("No se puede pagar un pedido cancelado");
        }
    }

    private Pago crearRegistroPago(Pedido pedido, String metodoPago, java.math.BigDecimal monto) {
        Pago pago = Pago.builder()
            .pedido(pedido)
            .metodoPago(metodoPago)
            .monto(monto)
            .moneda("PEN")
            .estado("pendiente")
            .fechaVencimiento(LocalDateTime.now().plusMinutes(15))
            .build();

        return pagoRepository.save(pago);
    }

    private String generarURLQR(String metodoPago, java.math.BigDecimal monto, String referencia) {
        // En producción, aquí se generaría el QR real con las APIs correspondientes
        return String.format("https://api.dpattymoda.com/qr/%s?monto=%s&ref=%s", 
                            metodoPago, monto, referencia);
    }

    private String simularConsultaEstadoPago(String referenciaExterna) {
        // Simular respuesta de API externa
        // En producción, aquí se haría la consulta real a la API de la billetera
        return Math.random() > 0.3 ? "procesado" : "pendiente";
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
}