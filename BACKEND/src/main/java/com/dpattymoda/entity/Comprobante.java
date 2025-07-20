package com.dpattymoda.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entidad Comprobante para facturación electrónica
 */
@Entity
@Table(name = "comprobantes")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Comprobante {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(name = "tipo_comprobante", nullable = false, length = 20)
    private String tipoComprobante; // boleta, factura

    @Column(name = "serie", nullable = false, length = 10)
    private String serie;

    @Column(name = "numero", nullable = false)
    private Integer numero;

    @Column(name = "ruc_emisor", nullable = false, length = 20)
    private String rucEmisor;

    @Column(name = "razon_social_emisor", nullable = false, length = 200)
    private String razonSocialEmisor;

    @Column(name = "direccion_emisor", columnDefinition = "TEXT")
    private String direccionEmisor;

    @Column(name = "documento_receptor", length = 20)
    private String documentoReceptor;

    @Column(name = "tipo_documento_receptor", length = 10)
    private String tipoDocumentoReceptor; // DNI, RUC, CE

    @Column(name = "nombre_receptor", length = 200)
    private String nombreReceptor;

    @Column(name = "direccion_receptor", columnDefinition = "TEXT")
    private String direccionReceptor;

    @Column(name = "subtotal", nullable = false, precision = 10, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "igv", nullable = false, precision = 10, scale = 2)
    private BigDecimal igv;

    @Column(name = "total", nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Builder.Default
    @Column(name = "moneda", length = 10)
    private String moneda = "PEN";

    @Builder.Default
    @Column(name = "fecha_emision")
    private LocalDateTime fechaEmision = LocalDateTime.now();

    @Column(name = "fecha_vencimiento")
    private LocalDateTime fechaVencimiento;

    @Column(name = "estado_sunat", length = 30)
    private String estadoSunat; // pendiente, aceptado, rechazado

    @Column(name = "codigo_hash", length = 255)
    private String codigoHash;

    @Column(name = "xml_firmado", columnDefinition = "TEXT")
    private String xmlFirmado;

    @Column(name = "pdf_url", length = 500)
    private String pdfUrl;

    @Column(name = "observaciones", columnDefinition = "TEXT")
    private String observaciones;

    @Column(name = "fecha_envio_sunat")
    private LocalDateTime fechaEnvioSunat;

    @Column(name = "fecha_respuesta_sunat")
    private LocalDateTime fechaRespuestaSunat;

    @Column(name = "codigo_respuesta_sunat", length = 10)
    private String codigoRespuestaSunat;

    @Column(name = "descripcion_respuesta_sunat", columnDefinition = "TEXT")
    private String descripcionRespuestaSunat;

    @Builder.Default
    @Column(name = "anulado")
    private Boolean anulado = false;

    @Column(name = "fecha_anulacion")
    private LocalDateTime fechaAnulacion;

    @Column(name = "motivo_anulacion", columnDefinition = "TEXT")
    private String motivoAnulacion;

    @CreatedDate
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private LocalDateTime fechaCreacion;

    // Métodos de utilidad
    public String getNumeroCompleto() {
        return serie + "-" + String.format("%08d", numero);
    }

    public boolean esBoleta() {
        return "boleta".equals(tipoComprobante);
    }

    public boolean esFactura() {
        return "factura".equals(tipoComprobante);
    }

    public boolean estaAceptadoPorSunat() {
        return "aceptado".equals(estadoSunat);
    }

    public boolean estaRechazadoPorSunat() {
        return "rechazado".equals(estadoSunat);
    }

    public boolean estaPendienteSunat() {
        return "pendiente".equals(estadoSunat) || estadoSunat == null;
    }

    public boolean estaAnulado() {
        return anulado != null && anulado;
    }

    public void anular(String motivo) {
        this.anulado = true;
        this.fechaAnulacion = LocalDateTime.now();
        this.motivoAnulacion = motivo;
    }

    public void marcarComoAceptadoPorSunat(String codigoRespuesta, String descripcionRespuesta) {
        this.estadoSunat = "aceptado";
        this.fechaRespuestaSunat = LocalDateTime.now();
        this.codigoRespuestaSunat = codigoRespuesta;
        this.descripcionRespuestaSunat = descripcionRespuesta;
    }

    public void marcarComoRechazadoPorSunat(String codigoRespuesta, String descripcionRespuesta) {
        this.estadoSunat = "rechazado";
        this.fechaRespuestaSunat = LocalDateTime.now();
        this.codigoRespuestaSunat = codigoRespuesta;
        this.descripcionRespuestaSunat = descripcionRespuesta;
    }

    public boolean puedeSerAnulado() {
        return !estaAnulado() && estaAceptadoPorSunat();
    }
}