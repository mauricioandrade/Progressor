package com.mauricioandrade.progressor.infrastructure.persistence.mappers;

import com.mauricioandrade.progressor.core.domain.workout.WorkoutBlock;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutBlockEntity;

public class WorkoutBlockMapper {

  public static WorkoutBlockEntity toEntity(WorkoutBlock domain) {
    WorkoutBlockEntity entity = new WorkoutBlockEntity();
    entity.setId(domain.getId());
    entity.setWorkoutPlanId(domain.getWorkoutPlanId());
    entity.setName(domain.getName());
    entity.setPosition(domain.getPosition());
    return entity;
  }

  public static WorkoutBlock toDomain(WorkoutBlockEntity entity) {
    return new WorkoutBlock(entity.getId(), entity.getWorkoutPlanId(), entity.getName(),
        entity.getPosition());
  }
}
