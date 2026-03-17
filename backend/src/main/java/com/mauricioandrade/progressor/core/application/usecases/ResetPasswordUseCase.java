package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.PasswordEncoder;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;

public class ResetPasswordUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public ResetPasswordUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public boolean execute(String token, String newPassword) {
    return userRepository.findIdByValidResetToken(token).map(userId -> {
      userRepository.updatePassword(userId, passwordEncoder.encode(newPassword));
      userRepository.clearResetToken(userId);
      return true;
    }).orElse(false);
  }
}
