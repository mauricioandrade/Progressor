package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutFeedbackEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataWorkoutFeedbackRepository
    extends JpaRepository<WorkoutFeedbackEntity, UUID> {

  Optional<WorkoutFeedbackEntity> findByStudentIdAndFeedbackDate(UUID studentId, LocalDate date);

  List<WorkoutFeedbackEntity> findByStudentIdOrderByFeedbackDateDesc(UUID studentId);

  List<WorkoutFeedbackEntity> findByTrainerIdOrderByFeedbackDateDesc(UUID trainerId);

  @Query(value = """
      SELECT DISTINCT ON (student_id) *
      FROM workout_feedbacks
      WHERE trainer_id = :trainerId
      ORDER BY student_id, feedback_date DESC
      """, nativeQuery = true)
  List<WorkoutFeedbackEntity> findLatestPerStudentByTrainerId(@Param("trainerId") UUID trainerId);
}
