package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WorkoutSessionRequest;
import com.mauricioandrade.progressor.core.application.ports.WorkoutSessionRepository;
import java.util.UUID;

public class SaveWorkoutSessionUseCase {

  private final WorkoutSessionRepository repository;

  public SaveWorkoutSessionUseCase(WorkoutSessionRepository repository) {
    this.repository = repository;
  }

  public void execute(UUID studentId, WorkoutSessionRequest request) {
    repository.save(studentId, request.exercises(), request.sets(), request.tonnageKg(),
        request.prCount());
  }
}
