package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutFeedbackEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWorkoutFeedbackRepository
    extends JpaRepository<WorkoutFeedbackEntity, UUID> {

  Optional<WorkoutFeedbackEntity> findByStudentIdAndFeedbackDate(UUID studentId, LocalDate date);

  List<WorkoutFeedbackEntity> findByStudentIdOrderByFeedbackDateDesc(UUID studentId);

  List<WorkoutFeedbackEntity> findByTrainerIdOrderByFeedbackDateDesc(UUID trainerId);
}
