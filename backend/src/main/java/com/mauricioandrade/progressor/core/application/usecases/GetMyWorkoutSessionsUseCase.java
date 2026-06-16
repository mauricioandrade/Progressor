package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WorkoutSessionResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutSessionRepository;
import java.util.List;
import java.util.UUID;

public class GetMyWorkoutSessionsUseCase {

  private final WorkoutSessionRepository repository;

  public GetMyWorkoutSessionsUseCase(WorkoutSessionRepository repository) {
    this.repository = repository;
  }

  public List<WorkoutSessionResponse> execute(UUID studentId) {
    return repository.findRecent(studentId, 10);
  }
}
