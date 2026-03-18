package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.UpdateWorkoutExerciseRequest;
import com.mauricioandrade.progressor.core.application.dto.WorkoutExerciseResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import com.mauricioandrade.progressor.core.domain.workout.MeasurementType;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutExercise;
import java.util.UUID;

public class UpdateWorkoutExerciseUseCase {

  private final WorkoutRepository workoutRepository;

  public UpdateWorkoutExerciseUseCase(WorkoutRepository workoutRepository) {
    this.workoutRepository = workoutRepository;
  }

  public WorkoutExerciseResponse execute(UUID id, UpdateWorkoutExerciseRequest request) {
    workoutRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + id));

    MeasurementType type = MeasurementType.valueOf(request.measurementType().toUpperCase());
    WorkoutExercise exercise = new WorkoutExercise(id, request.name(), request.sets(),
        request.repetitions(), type);

    if (request.weightInKg() != null) {
      exercise.applyWeight(request.weightInKg());
    }
    if (request.speed() != null) {
      exercise.applySpeed(request.speed());
    }
    if (request.timeInSeconds() != null) {
      exercise.applyTime(request.timeInSeconds());
    }
    if (request.cadence() != null) {
      exercise.applyCadence(request.cadence());
    }
    if (request.videoUrl() != null && !request.videoUrl().isBlank()) {
      exercise.applyVideoUrl(request.videoUrl());
    }
    if (request.restTime() != null) {
      exercise.applyRestTime(request.restTime());
    }
    String joined = (request.scheduledDays() != null && !request.scheduledDays().isEmpty())
        ? String.join(",", request.scheduledDays()) : null;
    if (request.workoutLabel() != null || joined != null) {
      exercise.applySchedule(request.workoutLabel(), joined);
    }
    if (request.blockId() != null) {
      exercise.assignToBlock(request.blockId());
    }

    WorkoutExercise saved = workoutRepository.update(id, exercise);
    return new WorkoutExerciseResponse(saved.getId(), saved.getName(), saved.getSets(),
        saved.getRepetitions(), saved.getMeasurementType().name(), saved.getWeightInKg(),
        saved.getSpeed(), saved.getTimeInSeconds(), saved.getCadence(), saved.getTonnage(),
        saved.getVideoUrl(), saved.getRestTime(), saved.getWorkoutLabel(),
        saved.getScheduledDays(), saved.getBlockId());
  }
}
