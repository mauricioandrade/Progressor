package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import java.util.UUID;

public class UpdateWeightGoalUseCase {

  private final UserRepository userRepository;

  public UpdateWeightGoalUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void execute(UUID studentId, Double goal) {
    if (!userRepository.existsById(studentId)) {
      throw new IllegalArgumentException("Student not found");
    }
    userRepository.updateWeightGoal(studentId, goal);
  }
}
