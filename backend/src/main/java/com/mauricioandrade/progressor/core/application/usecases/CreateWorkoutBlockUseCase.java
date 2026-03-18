package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutBlockRequest;
import com.mauricioandrade.progressor.core.application.dto.WorkoutBlockResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutBlockRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutPlanRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutBlock;
import java.util.List;

public class CreateWorkoutBlockUseCase {

  private final WorkoutBlockRepository workoutBlockRepository;
  private final WorkoutPlanRepository workoutPlanRepository;

  public CreateWorkoutBlockUseCase(WorkoutBlockRepository workoutBlockRepository,
      WorkoutPlanRepository workoutPlanRepository) {
    this.workoutBlockRepository = workoutBlockRepository;
    this.workoutPlanRepository = workoutPlanRepository;
  }

  public WorkoutBlockResponse execute(CreateWorkoutBlockRequest request) {
    workoutPlanRepository.findById(request.workoutPlanId())
        .orElseThrow(() -> new IllegalArgumentException("Workout plan not found"));

    var block = new WorkoutBlock(null, request.workoutPlanId(), request.name(), request.position());
    workoutBlockRepository.save(block);
    return new WorkoutBlockResponse(block.getId(), block.getWorkoutPlanId(), block.getName(),
        block.getPosition(), List.of());
  }
}
