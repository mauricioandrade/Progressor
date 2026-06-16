package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.dto.WorkoutSessionResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutSessionRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutSessionEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutSessionRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

@Component
public class WorkoutSessionRepositoryAdapter implements WorkoutSessionRepository {

  private final SpringDataWorkoutSessionRepository repository;

  public WorkoutSessionRepositoryAdapter(SpringDataWorkoutSessionRepository repository) {
    this.repository = repository;
  }

  @Override
  public void save(UUID studentId, int exercises, int sets, double tonnageKg, int prCount) {
    repository.save(new WorkoutSessionEntity(studentId, exercises, sets, tonnageKg, prCount));
  }

  @Override
  public List<WorkoutSessionResponse> findRecent(UUID studentId, int limit) {
    return repository
        .findByStudentIdOrderBySessionDateDescCreatedAtDesc(studentId, PageRequest.of(0, limit))
        .stream()
        .map(e -> new WorkoutSessionResponse(e.getId(), e.getSessionDate(),
            e.getExercises(), e.getSetsCompleted(), e.getTonnageKg(), e.getPrCount()))
        .toList();
  }
}
