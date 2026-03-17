package com.mauricioandrade.progressor.infrastructure.persistence.mappers;

import com.mauricioandrade.progressor.core.domain.workout.WorkoutLog;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutLogEntity;

public class WorkoutLogMapper {

  public static WorkoutLogEntity toEntity(WorkoutLog domain) {
    WorkoutLogEntity entity = new WorkoutLogEntity();
    entity.setId(domain.getId());
    entity.setStudentId(domain.getStudentId());
    entity.setExerciseId(domain.getExerciseId());
    entity.setExerciseName(domain.getExerciseName());
    entity.setActualWeight(domain.getActualWeight());
    entity.setActualReps(domain.getActualReps());
    entity.setCompletedAt(domain.getCompletedAt());
    entity.setTonnageAchieved(domain.getTonnageAchieved());
    entity.setPersonalRecord(domain.isPersonalRecord());
    return entity;
  }

  public static WorkoutLog toDomain(WorkoutLogEntity entity) {
    return new WorkoutLog(entity.getId(), entity.getStudentId(), entity.getExerciseId(),
        entity.getExerciseName(), entity.getActualWeight(), entity.getActualReps(),
        entity.getCompletedAt(), entity.getTonnageAchieved(), entity.isPersonalRecord());
  }
}
