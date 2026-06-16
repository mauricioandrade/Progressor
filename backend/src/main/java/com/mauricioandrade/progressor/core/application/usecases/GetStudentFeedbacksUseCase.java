package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WorkoutFeedbackResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutFeedbackRepository;
import java.util.List;
import java.util.UUID;

public class GetStudentFeedbacksUseCase {

  private final WorkoutFeedbackRepository feedbackRepository;

  public GetStudentFeedbacksUseCase(WorkoutFeedbackRepository feedbackRepository) {
    this.feedbackRepository = feedbackRepository;
  }

  public List<WorkoutFeedbackResponse> execute(UUID studentId) {
    return feedbackRepository.findByStudentId(studentId).stream()
        .map(f -> new WorkoutFeedbackResponse(f.getId(), f.getStudentId(), f.getRating(),
            f.getComment(), f.getFeedbackDate(), f.getCreatedAt()))
        .toList();
  }
}
