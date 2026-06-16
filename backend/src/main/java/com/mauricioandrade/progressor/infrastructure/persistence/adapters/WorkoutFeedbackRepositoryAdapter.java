package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.WorkoutFeedbackRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutFeedback;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutFeedbackEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutFeedbackRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class WorkoutFeedbackRepositoryAdapter implements WorkoutFeedbackRepository {

  private final SpringDataWorkoutFeedbackRepository repository;

  public WorkoutFeedbackRepositoryAdapter(SpringDataWorkoutFeedbackRepository repository) {
    this.repository = repository;
  }

  @Override
  public WorkoutFeedback save(WorkoutFeedback feedback) {
    WorkoutFeedbackEntity entity = toEntity(feedback);
    return toDomain(repository.save(entity));
  }

  @Override
  public Optional<WorkoutFeedback> findByStudentIdAndDate(UUID studentId, LocalDate date) {
    return repository.findByStudentIdAndFeedbackDate(studentId, date).map(this::toDomain);
  }

  @Override
  public List<WorkoutFeedback> findByStudentId(UUID studentId) {
    return repository.findByStudentIdOrderByFeedbackDateDesc(studentId).stream()
        .map(this::toDomain).toList();
  }

  @Override
  public List<WorkoutFeedback> findByTrainerId(UUID trainerId) {
    return repository.findByTrainerIdOrderByFeedbackDateDesc(trainerId).stream()
        .map(this::toDomain).toList();
  }

  private WorkoutFeedbackEntity toEntity(WorkoutFeedback f) {
    WorkoutFeedbackEntity e = new WorkoutFeedbackEntity();
    e.setId(f.getId());
    e.setStudentId(f.getStudentId());
    e.setTrainerId(f.getTrainerId());
    e.setRating(f.getRating());
    e.setComment(f.getComment());
    e.setFeedbackDate(f.getFeedbackDate());
    e.setCreatedAt(f.getCreatedAt());
    return e;
  }

  private WorkoutFeedback toDomain(WorkoutFeedbackEntity e) {
    return new WorkoutFeedback(e.getId(), e.getStudentId(), e.getTrainerId(), e.getRating(),
        e.getComment(), e.getFeedbackDate(), e.getCreatedAt());
  }
}
