package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.workout.WorkoutFeedback;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutFeedbackRepository {
  WorkoutFeedback save(WorkoutFeedback feedback);
  Optional<WorkoutFeedback> findByStudentIdAndDate(UUID studentId, LocalDate date);
  List<WorkoutFeedback> findByStudentId(UUID studentId);
  List<WorkoutFeedback> findByTrainerId(UUID trainerId);
}
