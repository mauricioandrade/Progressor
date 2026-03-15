package com.mauricioandrade.progressor.infrastructure.persistence.mappers;

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
    return entity;
  }
}