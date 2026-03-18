package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WeightGoalResponse;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import java.util.UUID;

public class GetWeightGoalUseCase {

  private final UserRepository userRepository;

  public GetWeightGoalUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public WeightGoalResponse execute(UUID studentId) {
    Double goal = userRepository.findWeightGoalByStudentId(studentId).orElse(null);
    return new WeightGoalResponse(goal);
  }
}
