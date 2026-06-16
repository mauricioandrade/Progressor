package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WorkoutFeedbackResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutFeedbackRepository;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

public class GetTodayFeedbackUseCase {

  private final WorkoutFeedbackRepository feedbackRepository;

  public GetTodayFeedbackUseCase(WorkoutFeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  public Optional<WorkoutFeedbackResponse> execute(UUID studentId) {
    return feedbackRepository.findByStudentIdAndDate(studentId, LocalDate.now())
        .map(f -> new WorkoutFeedbackResponse(f.getId(), f.getStudentId(), f.getRating(),
            f.getComment(), f.getFeedbackDate(), f.getCreatedAt()));
  }
}
