package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.EmailPort;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;

public class ForgotPasswordUseCase {

  private final UserRepository userRepository;
  private final EmailPort emailPort;
  private final String frontendUrl;

  public ForgotPasswordUseCase(UserRepository userRepository, EmailPort emailPort,
      String frontendUrl) {
    this.userRepository = userRepository;
    this.emailPort = emailPort;
    this.frontendUrl = frontendUrl;
  }

  public void execute(String email) {
    userRepository.findIdByEmail(email).ifPresent(userId -> {
      String token = UUID.randomUUID().toString();
      LocalDateTime expiry = LocalDateTime.now().plusHours(1);
      userRepository.saveResetToken(userId, token, expiry);

      // Look up first name for personalised email
      String firstName = userRepository.findById(userId)
          .map(u -> u.getFirstName())
          .orElse("usuário");

      String resetLink = frontendUrl + "/reset-password?token=" + token;
      emailPort.sendPasswordReset(email, firstName, resetLink);
    });
  }
}
