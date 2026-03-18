package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutExercise;
import com.mauricioandrade.progressor.infrastructure.persistence.mappers.WorkoutExerciseMapper;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class WorkoutRepositoryAdapter implements WorkoutRepository {

  private final SpringDataWorkoutRepository springDataRepository;

  public WorkoutRepositoryAdapter(SpringDataWorkoutRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void saveAll(List<WorkoutExercise> exercises, UUID studentId) {
    var entities = exercises.stream()
        .map(domain -> WorkoutExerciseMapper.toEntity(domain, studentId)).toList();
    springDataRepository.saveAll(entities);
  }

  @Override
  public List<WorkoutExercise> findByStudentId(UUID studentId) {
    return springDataRepository.findByStudentId(studentId).stream()
        .map(WorkoutExerciseMapper::toDomain).toList();
  }

  @Override
  public List<WorkoutExercise> findByStudentIdAndScheduledDay(UUID studentId, String day) {
    return springDataRepository.findByStudentIdAndScheduledDayContaining(studentId, day).stream()
        .map(WorkoutExerciseMapper::toDomain).toList();
  }

  @Override
  public List<WorkoutExercise> findByBlockId(UUID blockId) {
    return springDataRepository.findByBlockId(blockId).stream()
        .map(WorkoutExerciseMapper::toDomain).toList();
  }

  @Override
  public Optional<WorkoutExercise> findById(UUID id) {
    return springDataRepository.findById(id).map(WorkoutExerciseMapper::toDomain);
  }

  @Override
  public void deleteById(UUID id) {
    springDataRepository.deleteById(id);
  }

  @Override
  public WorkoutExercise update(UUID id, WorkoutExercise exercise) {
    var existing = springDataRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + id));
    var entity = WorkoutExerciseMapper.toEntity(exercise, existing.getStudentId());
    springDataRepository.save(entity);
    return exercise;
  }
}
