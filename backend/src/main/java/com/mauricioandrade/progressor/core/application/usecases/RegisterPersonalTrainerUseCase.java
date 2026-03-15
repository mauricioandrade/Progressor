package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.RegisterPersonalTrainerRequest;
import com.mauricioandrade.progressor.core.application.ports.PasswordEncoder;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.common.Cref;
import com.mauricioandrade.progressor.core.domain.common.Email;
import com.mauricioandrade.progressor.core.domain.user.PersonalTrainer;
import java.util.UUID;

public class RegisterPersonalTrainerUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegisterPersonalTrainerUseCase(UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UUID execute(RegisterPersonalTrainerRequest request) {
    Email email = new Email(request.email());

    if (userRepository.existsByEmail(email)) {
      throw new IllegalStateException("Email is already registered");
    }

    Cref cref = new Cref(request.cref());
    String hashedPassword = passwordEncoder.encode(request.password());

    PersonalTrainer personalTrainer = new PersonalTrainer(null, request.firstName(),
        request.lastName(), email, hashedPassword, request.birthDate(), cref);

    userRepository.save(personalTrainer);

    return personalTrainer.getId();
  }
}