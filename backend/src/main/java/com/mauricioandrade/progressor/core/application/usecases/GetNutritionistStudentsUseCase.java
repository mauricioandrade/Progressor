package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.StudentSummaryResponse;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import java.util.List;
import java.util.UUID;

public class GetNutritionistStudentsUseCase {

  private final UserRepository userRepository;

  public GetNutritionistStudentsUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public List<StudentSummaryResponse> execute(UUID nutritionistId) {
    return userRepository.findStudentsByNutritionistId(nutritionistId).stream()
        .map(s -> new StudentSummaryResponse(s.getId(), s.getFirstName() + " " + s.getLastName(), s.getEmail().value(), null))
        .toList();
  }
}
