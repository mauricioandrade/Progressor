package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WorkoutBlockResponse;
import com.mauricioandrade.progressor.core.application.dto.WorkoutExerciseResponse;
import com.mauricioandrade.progressor.core.application.dto.WorkoutPlanResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutBlockRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutPlanRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import java.util.List;
import java.util.UUID;

public class GetStudentWorkoutPlansUseCase {

  private final WorkoutPlanRepository workoutPlanRepository;
  private final WorkoutBlockRepository workoutBlockRepository;
  private final WorkoutRepository workoutRepository;

  public GetStudentWorkoutPlansUseCase(WorkoutPlanRepository workoutPlanRepository,
      WorkoutBlockRepository workoutBlockRepository, WorkoutRepository workoutRepository) {
    this.workoutPlanRepository = workoutPlanRepository;
    this.workoutBlockRepository = workoutBlockRepository;
    this.workoutRepository = workoutRepository;
  }

  public List<WorkoutPlanResponse> execute(UUID studentId) {
    return workoutPlanRepository.findByStudentId(studentId).stream().map(plan -> {
      List<WorkoutBlockResponse> blocks = workoutBlockRepository
          .findByWorkoutPlanId(plan.getId()).stream()
          .map(block -> {
            List<WorkoutExerciseResponse> exercises = workoutRepository
                .findByBlockId(block.getId()).stream()
                .map(e -> new WorkoutExerciseResponse(e.getId(), e.getName(), e.getSets(),
                    e.getRepetitions(), e.getMeasurementType().name(), e.getWeightInKg(),
                    e.getSpeed(), e.getTimeInSeconds(), e.getCadence(), e.getTonnage(),
                    e.getVideoUrl(), e.getRestTime(), e.getWorkoutLabel(), e.getScheduledDays(),
                    e.getBlockId()))
                .toList();
            return new WorkoutBlockResponse(block.getId(), block.getWorkoutPlanId(), block.getName(),
                block.getPosition(), exercises);
          })
          .toList();
      return new WorkoutPlanResponse(plan.getId(), plan.getStudentId(), plan.getTrainerId(),
          plan.getName(), plan.getCreatedAt(), blocks);
    }).toList();
  }
}
