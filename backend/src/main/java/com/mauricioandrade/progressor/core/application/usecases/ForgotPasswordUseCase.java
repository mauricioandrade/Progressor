package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import java.time.LocalDateTime;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ForgotPasswordUseCase {

  private static final Logger log = LoggerFactory.getLogger(ForgotPasswordUseCase.class);

  private final UserRepository userRepository;

  public ForgotPasswordUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void execute(String email) {
    userRepository.findIdByEmail(email).ifPresent(userId -> {
      String token = UUID.randomUUID().toString();
      LocalDateTime expiry = LocalDateTime.now().plusHours(1);
      userRepository.saveResetToken(userId, token, expiry);
      log.info("[MOCK EMAIL] Password reset link for {}: http://localhost:5173/reset-password?token={}", email, token);
    });
  }
}
