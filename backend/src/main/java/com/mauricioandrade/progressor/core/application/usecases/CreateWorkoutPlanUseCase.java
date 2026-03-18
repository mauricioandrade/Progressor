package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutPlanRequest;
import com.mauricioandrade.progressor.core.application.dto.WorkoutPlanResponse;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutPlanRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutPlan;
import java.util.List;
import java.util.UUID;

public class CreateWorkoutPlanUseCase {

  private final WorkoutPlanRepository workoutPlanRepository;
  private final UserRepository userRepository;

  public CreateWorkoutPlanUseCase(WorkoutPlanRepository workoutPlanRepository,
      UserRepository userRepository) {
    this.workoutPlanRepository = workoutPlanRepository;
    this.userRepository = userRepository;
  }

  public WorkoutPlanResponse execute(CreateWorkoutPlanRequest request, UUID trainerId) {
    if (!userRepository.existsById(request.studentId())) {
      throw new IllegalArgumentException("Student not found");
    }
    var plan = new WorkoutPlan(null, request.studentId(), trainerId, request.name());
    workoutPlanRepository.save(plan);
    return new WorkoutPlanResponse(plan.getId(), plan.getStudentId(), plan.getTrainerId(),
        plan.getName(), plan.getCreatedAt(), List.of());
  }
}
