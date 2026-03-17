package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.StudentSummaryResponse;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.common.Email;
import java.util.Optional;

public class FindStudentByEmailForNutritionistUseCase {

  private final UserRepository userRepository;

  public FindStudentByEmailForNutritionistUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public Optional<StudentSummaryResponse> execute(String email) {
    return userRepository.findStudentByEmailForNutritionist(new Email(email))
        .map(s -> new StudentSummaryResponse(s.getId(), s.getFirstName() + " " + s.getLastName(), s.getEmail().value(), null));
  }
}
