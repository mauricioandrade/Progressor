package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.RegisterNutritionistRequest;
import com.mauricioandrade.progressor.core.application.ports.PasswordEncoder;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.common.Crn;
import com.mauricioandrade.progressor.core.domain.common.Email;
import com.mauricioandrade.progressor.core.domain.user.Nutritionist;
import java.util.UUID;

public class RegisterNutritionistUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegisterNutritionistUseCase(UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UUID execute(RegisterNutritionistRequest request) {
    Email email = new Email(request.email());

    if (userRepository.existsByEmail(email)) {
      throw new IllegalStateException("Email is already registered");
    }

    Crn crn = new Crn(request.crn());
    String hashedPassword = passwordEncoder.encode(request.password());

    Nutritionist nutritionist = new Nutritionist(null, request.firstName(), request.lastName(),
        email, hashedPassword, request.birthDate(), crn);

    userRepository.save(nutritionist);

    return nutritionist.getId();
  }
}
