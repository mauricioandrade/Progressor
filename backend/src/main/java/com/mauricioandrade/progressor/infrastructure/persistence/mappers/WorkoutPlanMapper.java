package com.mauricioandrade.progressor.infrastructure.persistence.mappers;

import com.mauricioandrade.progressor.core.domain.workout.WorkoutPlan;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutPlanEntity;

public class WorkoutPlanMapper {

  public static WorkoutPlanEntity toEntity(WorkoutPlan domain) {
    WorkoutPlanEntity entity = new WorkoutPlanEntity();
    entity.setId(domain.getId());
    entity.setStudentId(domain.getStudentId());
    entity.setTrainerId(domain.getTrainerId());
    entity.setName(domain.getName());
    entity.setCreatedAt(domain.getCreatedAt());
    return entity;
  }

  public static WorkoutPlan toDomain(WorkoutPlanEntity entity) {
    return new WorkoutPlan(entity.getId(), entity.getStudentId(), entity.getTrainerId(),
        entity.getName(), entity.getCreatedAt());
  }
}
