package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.SubmitWorkoutFeedbackRequest;
import com.mauricioandrade.progressor.core.application.dto.WorkoutFeedbackResponse;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutFeedbackRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutFeedback;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class SubmitWorkoutFeedbackUseCase {

  private final WorkoutFeedbackRepository feedbackRepository;
  private final UserRepository userRepository;

  public SubmitWorkoutFeedbackUseCase(WorkoutFeedbackRepository feedbackRepository,
      UserRepository userRepository) {
    this.feedbackRepository = feedbackRepository;
    this.userRepository = userRepository;
  }

  public WorkoutFeedbackResponse execute(UUID studentId, SubmitWorkoutFeedbackRequest request) {
    LocalDate today = LocalDate.now();
    feedbackRepository.findByStudentIdAndDate(studentId, today)
        .ifPresent(existing -> {
          throw new IllegalStateException("Feedback already submitted for today");
        });

    UUID trainerId = userRepository.findStudentById(studentId)
        .map(s -> s.getPersonalTrainerId())
        .orElse(null);

    WorkoutFeedback feedback = new WorkoutFeedback(
        UUID.randomUUID(), studentId, trainerId,
        request.rating(), request.comment(), today, LocalDateTime.now());
    WorkoutFeedback saved = feedbackRepository.save(feedback);
    return toResponse(saved);
  }

  private WorkoutFeedbackResponse toResponse(WorkoutFeedback f) {
    return new WorkoutFeedbackResponse(f.getId(), f.getStudentId(), f.getRating(),
        f.getComment(), f.getFeedbackDate(), f.getCreatedAt());
  }
}
