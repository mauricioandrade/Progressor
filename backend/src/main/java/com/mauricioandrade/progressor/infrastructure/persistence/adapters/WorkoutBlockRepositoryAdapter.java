package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.WorkoutBlockRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutBlock;
import com.mauricioandrade.progressor.infrastructure.persistence.mappers.WorkoutBlockMapper;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutBlockRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class WorkoutBlockRepositoryAdapter implements WorkoutBlockRepository {

  private final SpringDataWorkoutBlockRepository springDataRepository;

  public WorkoutBlockRepositoryAdapter(SpringDataWorkoutBlockRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void save(WorkoutBlock block) {
    springDataRepository.save(WorkoutBlockMapper.toEntity(block));
  }

  @Override
  public Optional<WorkoutBlock> findById(UUID id) {
    return springDataRepository.findById(id).map(WorkoutBlockMapper::toDomain);
  }

  @Override
  public List<WorkoutBlock> findByWorkoutPlanId(UUID workoutPlanId) {
    return springDataRepository.findByWorkoutPlanIdOrderByPosition(workoutPlanId).stream()
        .map(WorkoutBlockMapper::toDomain).toList();
  }
}
