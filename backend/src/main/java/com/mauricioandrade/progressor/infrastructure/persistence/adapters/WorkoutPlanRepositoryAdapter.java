package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.WorkoutPlanRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutPlan;
import com.mauricioandrade.progressor.infrastructure.persistence.mappers.WorkoutPlanMapper;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutPlanRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class WorkoutPlanRepositoryAdapter implements WorkoutPlanRepository {

  private final SpringDataWorkoutPlanRepository springDataRepository;

  public WorkoutPlanRepositoryAdapter(SpringDataWorkoutPlanRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void save(WorkoutPlan plan) {
    springDataRepository.save(WorkoutPlanMapper.toEntity(plan));
  }

  @Override
  public Optional<WorkoutPlan> findById(UUID id) {
    return springDataRepository.findById(id).map(WorkoutPlanMapper::toDomain);
  }

  @Override
  public List<WorkoutPlan> findByStudentId(UUID studentId) {
    return springDataRepository.findByStudentId(studentId).stream()
        .map(WorkoutPlanMapper::toDomain).toList();
  }
}
