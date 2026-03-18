package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import java.util.UUID;

public class DeleteWorkoutExerciseUseCase {

  private final WorkoutRepository workoutRepository;

  public DeleteWorkoutExerciseUseCase(WorkoutRepository workoutRepository) {
    this.workoutRepository = workoutRepository;
  }

  public void execute(UUID id) {
    workoutRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + id));
    workoutRepository.deleteById(id);
  }
}
