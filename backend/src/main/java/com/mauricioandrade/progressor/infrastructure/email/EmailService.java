package com.mauricioandrade.progressor.infrastructure.email;

import com.mauricioandrade.progressor.core.application.ports.EmailPort;
import jakarta.mail.internet.MimeMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailService implements EmailPort {

  private static final Logger log = LoggerFactory.getLogger(EmailService.class);
  private static final String FROM_ADDRESS = "contato@send.mauricioandradedev.com.br";
  private static final String FROM_NAME = "Progressor";

  private final JavaMailSender mailSender;

  public EmailService(JavaMailSender mailSender) {
    this.mailSender = mailSender;
  }

  @Override
  public void sendPasswordReset(String to, String firstName, String resetLink) {
    String subject = "Redefinição de Senha — Progressor";
    String html = """
        <div style="font-family:sans-serif;max-width:480px;margin:auto;padding:24px">
          <h2 style="color:#2563eb">Progressor</h2>
          <p>Olá, <strong>%s</strong>!</p>
          <p>Recebemos uma solicitação para redefinir a sua senha. Clique no botão abaixo para criar uma nova senha:</p>
          <a href="%s" style="display:inline-block;margin:16px 0;padding:12px 24px;background:#2563eb;color:#fff;border-radius:8px;text-decoration:none;font-weight:600">
            Redefinir Senha
          </a>
          <p style="color:#6b7280;font-size:13px">Este link expira em 1 hora. Se você não solicitou a redefinição, ignore este e-mail.</p>
          <hr style="border:none;border-top:1px solid #e5e7eb;margin:24px 0"/>
          <p style="color:#9ca3af;font-size:12px">© Progressor — Sua jornada fitness levada a sério.</p>
        </div>
        """.formatted(firstName, resetLink);
    sendHtml(to, subject, html);
  }

  @Override
  public void sendInactivityAlert(String to, String firstName) {
    String subject = "Sentimos sua falta, " + firstName + "! 💪";
    String html = """
        <div style="font-family:sans-serif;max-width:480px;margin:auto;padding:24px">
          <h2 style="color:#2563eb">Progressor</h2>
          <p>Olá, <strong>%s</strong>!</p>
          <p>Notamos que faz mais de 3 dias desde o seu último treino registrado. Lembre-se: a consistência é a chave para atingir seus objetivos! 🏋️</p>
          <p>Cada sessão conta. Volte hoje e continue sua jornada rumo ao melhor de você!</p>
          <p style="color:#6b7280;font-size:13px">Continue firme — sua equipe Progressor acredita em você.</p>
          <hr style="border:none;border-top:1px solid #e5e7eb;margin:24px 0"/>
          <p style="color:#9ca3af;font-size:12px">© Progressor — Sua jornada fitness levada a sério.</p>
        </div>
        """.formatted(firstName);
    sendHtml(to, subject, html);
  }

  private void sendHtml(String to, String subject, String html) {
    try {
      MimeMessage message = mailSender.createMimeMessage();
      MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
      helper.setFrom(FROM_ADDRESS, FROM_NAME);
      helper.setTo(to);
      helper.setSubject(subject);
      helper.setText(html, true);
      mailSender.send(message);
      log.info("E-mail enviado para {}: {}", to, subject);
    } catch (Exception e) {
      log.error("Falha ao enviar e-mail para {}: {}", to, e.getMessage());
    }
  }
}
