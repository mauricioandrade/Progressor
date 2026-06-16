package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.SubmitWorkoutFeedbackRequest;
import com.mauricioandrade.progressor.core.application.dto.WorkoutFeedbackResponse;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentFeedbacksUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetTodayFeedbackUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SubmitWorkoutFeedbackUseCase;
import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts/feedback")
public class WorkoutFeedbackController {

  private final SubmitWorkoutFeedbackUseCase submitFeedbackUseCase;
  private final GetTodayFeedbackUseCase getTodayFeedbackUseCase;
  private final GetStudentFeedbacksUseCase getStudentFeedbacksUseCase;

  public WorkoutFeedbackController(SubmitWorkoutFeedbackUseCase submitFeedbackUseCase,
      GetTodayFeedbackUseCase getTodayFeedbackUseCase,
      GetStudentFeedbacksUseCase getStudentFeedbacksUseCase) {
    this.submitFeedbackUseCase = submitFeedbackUseCase;
    this.getTodayFeedbackUseCase = getTodayFeedbackUseCase;
    this.getStudentFeedbacksUseCase = getStudentFeedbacksUseCase;
  }

  @PostMapping
  public ResponseEntity<WorkoutFeedbackResponse> submit(
      @RequestBody SubmitWorkoutFeedbackRequest request,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(submitFeedbackUseCase.execute(currentUser.getId(), request));
  }

  @GetMapping("/today")
  public ResponseEntity<WorkoutFeedbackResponse> getToday(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    Optional<WorkoutFeedbackResponse> today = getTodayFeedbackUseCase.execute(currentUser.getId());
    return today.map(ResponseEntity::ok).orElse(ResponseEntity.noContent().build());
  }

  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<WorkoutFeedbackResponse>> getByStudent(
      @PathVariable UUID studentId) {
    return ResponseEntity.ok(getStudentFeedbacksUseCase.execute(studentId));
  }
}
