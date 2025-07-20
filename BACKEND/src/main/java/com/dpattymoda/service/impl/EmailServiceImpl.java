package com.dpattymoda.service.impl;

import com.dpattymoda.service.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

/**
 * Implementación del servicio de email
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    @Value("${app.empresa.nombre}")
    private String nombreEmpresa;

    @Value("${app.empresa.web:https://dpattymoda.com}")
    private String sitioWeb;

    @Override
    public void enviarEmailVerificacion(String email, String token) {
        log.info("Enviando email de verificación a: {}", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, nombreEmpresa);
            helper.setTo(email);
            helper.setSubject("Verifica tu cuenta en " + nombreEmpresa);

            String contenido = construirEmailVerificacion(token);
            helper.setText(contenido, true);

            mailSender.send(message);
            log.info("Email de verificación enviado exitosamente a: {}", email);

        } catch (Exception e) {
            log.error("Error enviando email de verificación a {}: {}", email, e.getMessage());
            throw new RuntimeException("Error enviando email de verificación", e);
        }
    }

    @Override
    public void enviarEmailRecuperacion(String email, String token) {
        log.info("Enviando email de recuperación a: {}", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, nombreEmpresa);
            helper.setTo(email);
            helper.setSubject("Recupera tu contraseña - " + nombreEmpresa);

            String contenido = construirEmailRecuperacion(token);
            helper.setText(contenido, true);

            mailSender.send(message);
            log.info("Email de recuperación enviado exitosamente a: {}", email);

        } catch (Exception e) {
            log.error("Error enviando email de recuperación a {}: {}", email, e.getMessage());
            throw new RuntimeException("Error enviando email de recuperación", e);
        }
    }

    @Override
    public void enviarNotificacionPedidoConfirmado(String email, String numeroPedido) {
        log.info("Enviando notificación de pedido confirmado a: {}", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, nombreEmpresa);
            helper.setTo(email);
            helper.setSubject("Pedido Confirmado #" + numeroPedido + " - " + nombreEmpresa);

            String contenido = construirEmailPedidoConfirmado(numeroPedido);
            helper.setText(contenido, true);

            mailSender.send(message);
            log.info("Notificación de pedido confirmado enviada a: {}", email);

        } catch (Exception e) {
            log.error("Error enviando notificación de pedido a {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void enviarNotificacionEstadoPedido(String email, String numeroPedido, String nuevoEstado) {
        log.info("Enviando notificación de cambio de estado a: {}", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, nombreEmpresa);
            helper.setTo(email);
            helper.setSubject("Actualización de Pedido #" + numeroPedido + " - " + nombreEmpresa);

            String contenido = construirEmailCambioEstado(numeroPedido, nuevoEstado);
            helper.setText(contenido, true);

            mailSender.send(message);
            log.info("Notificación de cambio de estado enviada a: {}", email);

        } catch (Exception e) {
            log.error("Error enviando notificación de estado a {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void enviarRecordatorioCarritoAbandonado(String email, String nombreUsuario) {
        log.info("Enviando recordatorio de carrito abandonado a: {}", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, nombreEmpresa);
            helper.setTo(email);
            helper.setSubject("¡No olvides tu carrito! - " + nombreEmpresa);

            String contenido = construirEmailCarritoAbandonado(nombreUsuario);
            helper.setText(contenido, true);

            mailSender.send(message);
            log.info("Recordatorio de carrito abandonado enviado a: {}", email);

        } catch (Exception e) {
            log.error("Error enviando recordatorio de carrito a {}: {}", email, e.getMessage());
        }
    }

    @Override
    public void enviarPromocion(String email, String asunto, String contenido) {
        log.info("Enviando promoción a: {}", email);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(fromEmail, nombreEmpresa);
            helper.setTo(email);
            helper.setSubject(asunto + " - " + nombreEmpresa);
            helper.setText(contenido, true);

            mailSender.send(message);
            log.info("Promoción enviada exitosamente a: {}", email);

        } catch (Exception e) {
            log.error("Error enviando promoción a {}: {}", email, e.getMessage());
        }
    }

    // Métodos privados para construir contenido HTML

    private String construirEmailVerificacion(String token) {
        String urlVerificacion = sitioWeb + "/verificar-email?token=" + token;
        
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2c3e50;">¡Bienvenido a %s!</h2>
                    <p>Gracias por registrarte en nuestra tienda. Para completar tu registro, por favor verifica tu dirección de correo electrónico.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Verificar Email
                        </a>
                    </div>
                    <p style="font-size: 14px; color: #666;">
                        Si no puedes hacer clic en el botón, copia y pega este enlace en tu navegador:<br>
                        <a href="%s">%s</a>
                    </p>
                    <p style="font-size: 14px; color: #666;">
                        Este enlace expirará en 24 horas por seguridad.
                    </p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                    <p style="font-size: 12px; color: #999; text-align: center;">
                        © 2024 %s. Todos los derechos reservados.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(nombreEmpresa, urlVerificacion, urlVerificacion, urlVerificacion, nombreEmpresa);
    }

    private String construirEmailRecuperacion(String token) {
        String urlRecuperacion = sitioWeb + "/restablecer-password?token=" + token;
        
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #2c3e50;">Recuperación de Contraseña</h2>
                    <p>Hemos recibido una solicitud para restablecer la contraseña de tu cuenta en %s.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #e74c3c; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Restablecer Contraseña
                        </a>
                    </div>
                    <p style="font-size: 14px; color: #666;">
                        Si no puedes hacer clic en el botón, copia y pega este enlace en tu navegador:<br>
                        <a href="%s">%s</a>
                    </p>
                    <p style="font-size: 14px; color: #666;">
                        <strong>Este enlace expirará en 1 hora por seguridad.</strong>
                    </p>
                    <p style="font-size: 14px; color: #666;">
                        Si no solicitaste este cambio, puedes ignorar este email de forma segura.
                    </p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                    <p style="font-size: 12px; color: #999; text-align: center;">
                        © 2024 %s. Todos los derechos reservados.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(nombreEmpresa, urlRecuperacion, urlRecuperacion, urlRecuperacion, nombreEmpresa);
    }

    private String construirEmailPedidoConfirmado(String numeroPedido) {
        String urlPedido = sitioWeb + "/mis-pedidos";
        
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #27ae60;">¡Pedido Confirmado!</h2>
                    <p>Tu pedido <strong>#%s</strong> ha sido confirmado y está siendo procesado.</p>
                    <p>Te notificaremos cuando tu pedido sea enviado y podrás hacer seguimiento del mismo.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #27ae60; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Ver Mis Pedidos
                        </a>
                    </div>
                    <p style="font-size: 14px; color: #666;">
                        Gracias por confiar en %s. ¡Esperamos que disfrutes tu compra!
                    </p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                    <p style="font-size: 12px; color: #999; text-align: center;">
                        © 2024 %s. Todos los derechos reservados.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(numeroPedido, urlPedido, nombreEmpresa, nombreEmpresa);
    }

    private String construirEmailCambioEstado(String numeroPedido, String nuevoEstado) {
        String estadoDescriptivo = obtenerDescripcionEstado(nuevoEstado);
        String urlPedido = sitioWeb + "/mis-pedidos";
        
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #3498db;">Actualización de Pedido</h2>
                    <p>Tu pedido <strong>#%s</strong> ha sido actualizado.</p>
                    <p><strong>Nuevo estado:</strong> %s</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #3498db; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Ver Detalles
                        </a>
                    </div>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                    <p style="font-size: 12px; color: #999; text-align: center;">
                        © 2024 %s. Todos los derechos reservados.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(numeroPedido, estadoDescriptivo, urlPedido, nombreEmpresa);
    }

    private String construirEmailCarritoAbandonado(String nombreUsuario) {
        String urlCarrito = sitioWeb + "/carrito";
        
        return """
            <html>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #f39c12;">¡Hola %s!</h2>
                    <p>Notamos que dejaste algunos productos increíbles en tu carrito. ¡No los pierdas!</p>
                    <p>Completa tu compra ahora y disfruta de nuestros productos de calidad.</p>
                    <div style="text-align: center; margin: 30px 0;">
                        <a href="%s" style="background-color: #f39c12; color: white; padding: 12px 30px; text-decoration: none; border-radius: 5px; display: inline-block;">
                            Completar Compra
                        </a>
                    </div>
                    <p style="font-size: 14px; color: #666;">
                        ¿Necesitas ayuda? Contáctanos y te asistiremos con gusto.
                    </p>
                    <hr style="border: none; border-top: 1px solid #eee; margin: 30px 0;">
                    <p style="font-size: 12px; color: #999; text-align: center;">
                        © 2024 %s. Todos los derechos reservados.
                    </p>
                </div>
            </body>
            </html>
            """.formatted(nombreUsuario, urlCarrito, nombreEmpresa);
    }

    private String obtenerDescripcionEstado(String estado) {
        return switch (estado) {
            case "procesando" -> "Procesando";
            case "enviado" -> "Enviado";
            case "entregado" -> "Entregado";
            case "cancelado" -> "Cancelado";
            default -> estado;
        };
    }
}