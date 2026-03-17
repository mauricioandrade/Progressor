package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.WorkoutLogRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutLog;
import com.mauricioandrade.progressor.infrastructure.persistence.mappers.WorkoutLogMapper;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutLogRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Repository;

@Repository
public class WorkoutLogRepositoryAdapter implements WorkoutLogRepository {

  private final SpringDataWorkoutLogRepository springDataRepository;

  public WorkoutLogRepositoryAdapter(SpringDataWorkoutLogRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void save(WorkoutLog log) {
    springDataRepository.save(WorkoutLogMapper.toEntity(log));
  }

  @Override
  public List<WorkoutLog> findByStudentIdAndExerciseId(UUID studentId, UUID exerciseId) {
    return springDataRepository
        .findByStudentIdAndExerciseIdOrderByCompletedAtDesc(studentId, exerciseId).stream()
        .map(WorkoutLogMapper::toDomain).toList();
  }

  @Override
  public List<WorkoutLog> findLatestPrsByStudentId(UUID studentId, int limit) {
    return springDataRepository
        .findPersonalRecordsByStudentId(studentId, PageRequest.of(0, limit)).stream()
        .map(WorkoutLogMapper::toDomain).toList();
  }
}
