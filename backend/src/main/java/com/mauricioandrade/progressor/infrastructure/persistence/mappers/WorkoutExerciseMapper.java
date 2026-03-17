package com.mauricioandrade.progressor.infrastructure.persistence.mappers;

import com.mauricioandrade.progressor.core.domain.workout.MeasurementType;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutExercise;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutExerciseEntity;

public class WorkoutExerciseMapper {

  public static WorkoutExerciseEntity toEntity(WorkoutExercise domain, java.util.UUID studentId) {
    WorkoutExerciseEntity entity = new WorkoutExerciseEntity();
    entity.setId(domain.getId());
    entity.setStudentId(studentId);
    entity.setName(domain.getName());
    entity.setSets(domain.getSets());
    entity.setRepetitions(domain.getRepetitions());
    entity.setMeasurementType(domain.getMeasurementType().name());
    entity.setWeightInKg(domain.getWeightInKg());
    entity.setSpeed(domain.getSpeed());
    entity.setTimeInSeconds(domain.getTimeInSeconds());
    entity.setCadence(domain.getCadence());
    entity.setTonnage(domain.getTonnage());
    entity.setVideoUrl(domain.getVideoUrl());
    entity.setRestTime(domain.getRestTime());
    entity.setWorkoutLabel(domain.getWorkoutLabel());
    entity.setScheduledDays(domain.getScheduledDays());
    return entity;
  }

  public static WorkoutExercise toDomain(WorkoutExerciseEntity entity) {
    MeasurementType type = MeasurementType.valueOf(entity.getMeasurementType());
    WorkoutExercise exercise = new WorkoutExercise(entity.getId(), entity.getName(),
        entity.getSets(), entity.getRepetitions(), type);

    if (entity.getWeightInKg() != null) {
      exercise.applyWeight(entity.getWeightInKg());
    }
    if (entity.getSpeed() != null) {
      exercise.applySpeed(entity.getSpeed());
    }
    if (entity.getTimeInSeconds() != null) {
      exercise.applyTime(entity.getTimeInSeconds());
    }
    if (entity.getCadence() != null) {
      exercise.applyCadence(entity.getCadence());
    }
    if (entity.getVideoUrl() != null) {
      exercise.applyVideoUrl(entity.getVideoUrl());
    }
    if (entity.getRestTime() != null) {
      exercise.applyRestTime(entity.getRestTime());
    }
    if (entity.getWorkoutLabel() != null || entity.getScheduledDays() != null) {
      exercise.applySchedule(entity.getWorkoutLabel(), entity.getScheduledDays());
    }
    return exercise;
  }
}
