package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import java.util.UUID;

public class AssignStudentToNutritionistUseCase {

  private final UserRepository userRepository;

  public AssignStudentToNutritionistUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void execute(UUID studentId, UUID nutritionistId) {
    var student = userRepository.findStudentById(studentId)
        .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    if (!userRepository.existsById(nutritionistId)) throw new IllegalArgumentException("Nutritionist not found");
    student.assignNutritionist(nutritionistId);
    userRepository.update(student);
  }
}
