package com.projetoweb.oficinamecanica.services;

import com.projetoweb.oficinamecanica.entities.enums.OrderStatus;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String remetente;

    @Value("${app.admin.email}")
    private String adminEmail;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void enviarAtualizacaoStatus(Long orderId, String nomeCliente, String emailCliente,
                                        OrderStatus status, BigDecimal total) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(emailCliente);
            helper.setSubject("Atualização da sua Ordem de Serviço #" + orderId);
            helper.setText(buildEmailBody(orderId, nomeCliente, status, total), true);

            mailSender.send(message);
            log.info("Email enviado para {} — OS #{} status: {}", emailCliente, orderId, status);

        } catch (MailException | MessagingException e) {
            log.error("Falha ao enviar email para {} (OS #{}): {}", emailCliente, orderId, e.getMessage());
        }
    }

    public void enviarAlertaEstoque(String nomeProduto, int quantidadeAtual, int quantidadeMinima) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom(remetente);
            helper.setTo(adminEmail);
            helper.setSubject("Alerta de Estoque Mínimo — " + nomeProduto);
            helper.setText(buildAlertaBody(nomeProduto, quantidadeAtual, quantidadeMinima), true);

            mailSender.send(message);
            log.warn("Alerta de estoque mínimo enviado — produto: '{}', atual: {}, mínimo: {}",
                    nomeProduto, quantidadeAtual, quantidadeMinima);

        } catch (MailException | MessagingException e) {
            log.error("Falha ao enviar alerta de estoque para {}: {}", adminEmail, e.getMessage());
        }
    }

    private String buildAlertaBody(String nomeProduto, int quantidadeAtual, int quantidadeMinima) {
        return """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2 style="color: #e53935;">Oficina Mecânica — Alerta de Estoque</h2>
                    <p>O produto abaixo atingiu ou ficou abaixo do estoque mínimo:</p>
                    <table style="border-collapse: collapse; width: 100%%; max-width: 400px;">
                      <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;"><strong>Produto</strong></td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                      </tr>
                      <tr style="background-color: #fff3f3;">
                        <td style="padding: 8px; border: 1px solid #ddd;"><strong>Quantidade atual</strong></td>
                        <td style="padding: 8px; border: 1px solid #ddd; color: #e53935;"><strong>%d</strong></td>
                      </tr>
                      <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;"><strong>Quantidade mínima</strong></td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%d</td>
                      </tr>
                    </table>
                    <p style="margin-top: 16px;">Por favor, realize a reposição do estoque.</p>
                  </body>
                </html>
                """.formatted(nomeProduto, quantidadeAtual, quantidadeMinima);
    }

    private String buildEmailBody(Long orderId, String nomeCliente, OrderStatus status, BigDecimal total) {
        String statusLabel = status.name().replace("_", " ");
        String totalFormatado = String.format("R$ %.2f", total);

        return """
                <html>
                  <body style="font-family: Arial, sans-serif; color: #333;">
                    <h2 style="color: #1a73e8;">Oficina Mecânica — Atualização de OS</h2>
                    <p>Olá, <strong>%s</strong>!</p>
                    <p>Sua Ordem de Serviço foi atualizada:</p>
                    <table style="border-collapse: collapse; width: 100%%; max-width: 400px;">
                      <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;"><strong>Número da OS</strong></td>
                        <td style="padding: 8px; border: 1px solid #ddd;">#%d</td>
                      </tr>
                      <tr style="background-color: #f9f9f9;">
                        <td style="padding: 8px; border: 1px solid #ddd;"><strong>Status</strong></td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                      </tr>
                      <tr>
                        <td style="padding: 8px; border: 1px solid #ddd;"><strong>Total</strong></td>
                        <td style="padding: 8px; border: 1px solid #ddd;">%s</td>
                      </tr>
                    </table>
                    <p style="margin-top: 20px; font-size: 0.85em; color: #888;">
                      Em caso de dúvidas, entre em contato com a oficina.
                    </p>
                  </body>
                </html>
                """.formatted(nomeCliente, orderId, statusLabel, totalFormatado);
    }
}
