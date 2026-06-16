package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WorkoutBlockResponse;
import com.mauricioandrade.progressor.core.application.dto.WorkoutExerciseResponse;
import com.mauricioandrade.progressor.core.application.dto.WorkoutPlanResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutBlockRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutPlanRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutBlock;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutExercise;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
    var plans = workoutPlanRepository.findByStudentId(studentId);
    if (plans.isEmpty()) return List.of();

    // 3 queries total regardless of plan/block count (was 1 + P + P*B)
    var planIds = plans.stream().map(p -> p.getId()).toList();
    var allBlocks = workoutBlockRepository.findByWorkoutPlanIds(planIds);

    var blockIds = allBlocks.stream().map(WorkoutBlock::getId).toList();
    var allExercises = workoutRepository.findByBlockIds(blockIds);

    Map<UUID, List<WorkoutExercise>> exercisesByBlock = allExercises.stream()
        .collect(Collectors.groupingBy(WorkoutExercise::getBlockId));

    Map<UUID, List<WorkoutBlock>> blocksByPlan = allBlocks.stream()
        .collect(Collectors.groupingBy(WorkoutBlock::getWorkoutPlanId));

    return plans.stream().map(plan -> {
      List<WorkoutBlockResponse> blockResponses = blocksByPlan
          .getOrDefault(plan.getId(), List.of()).stream()
          .map(block -> {
            List<WorkoutExerciseResponse> exResponses = exercisesByBlock
                .getOrDefault(block.getId(), List.of()).stream()
                .map(e -> new WorkoutExerciseResponse(e.getId(), e.getName(), e.getSets(),
                    e.getRepetitions(), e.getMeasurementType().name(), e.getWeightInKg(),
                    e.getSpeed(), e.getTimeInSeconds(), e.getCadence(), e.getTonnage(),
                    e.getVideoUrl(), e.getRestTime(), e.getWorkoutLabel(), e.getScheduledDays(),
                    e.getBlockId()))
                .toList();
            return new WorkoutBlockResponse(block.getId(), block.getWorkoutPlanId(), block.getName(),
                block.getPosition(), exResponses);
          })
          .toList();
      return new WorkoutPlanResponse(plan.getId(), plan.getStudentId(), plan.getTrainerId(),
          plan.getName(), plan.getCreatedAt(), blockResponses);
    }).toList();
  }
}
