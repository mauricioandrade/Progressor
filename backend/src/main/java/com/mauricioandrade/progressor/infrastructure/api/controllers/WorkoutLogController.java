package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.ExerciseLogResponse;
import com.mauricioandrade.progressor.core.application.dto.ExerciseStatsResponse;
import com.mauricioandrade.progressor.core.application.dto.LogExerciseRequest;
import com.mauricioandrade.progressor.core.application.usecases.GetExerciseHistoryUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetExerciseStatsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentPersonalRecordsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.CheckInStudentUseCase;
import com.mauricioandrade.progressor.core.application.usecases.LogExerciseExecutionUseCase;
import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import jakarta.validation.Valid;
import java.util.List;
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
@RequestMapping("/api/workouts")
public class WorkoutLogController {

  private final LogExerciseExecutionUseCase logExerciseExecutionUseCase;
  private final GetExerciseHistoryUseCase getExerciseHistoryUseCase;
  private final GetStudentPersonalRecordsUseCase getStudentPersonalRecordsUseCase;
  private final GetExerciseStatsUseCase getExerciseStatsUseCase;
  private final CheckInStudentUseCase checkInStudentUseCase;

  public WorkoutLogController(LogExerciseExecutionUseCase logExerciseExecutionUseCase,
      GetExerciseHistoryUseCase getExerciseHistoryUseCase,
      GetStudentPersonalRecordsUseCase getStudentPersonalRecordsUseCase,
      GetExerciseStatsUseCase getExerciseStatsUseCase,
      CheckInStudentUseCase checkInStudentUseCase) {
    this.logExerciseExecutionUseCase = logExerciseExecutionUseCase;
    this.getExerciseHistoryUseCase = getExerciseHistoryUseCase;
    this.getStudentPersonalRecordsUseCase = getStudentPersonalRecordsUseCase;
    this.getExerciseStatsUseCase = getExerciseStatsUseCase;
    this.checkInStudentUseCase = checkInStudentUseCase;
  }

  @PostMapping("/log")
  public ResponseEntity<ExerciseLogResponse> logExecution(
      @Valid @RequestBody LogExerciseRequest request,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    ExerciseLogResponse response = logExerciseExecutionUseCase.execute(request, currentUser.getId());
    checkInStudentUseCase.execute(currentUser.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @GetMapping("/history/{exerciseId}")
  public ResponseEntity<List<ExerciseLogResponse>> getHistory(@PathVariable UUID exerciseId,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(getExerciseHistoryUseCase.execute(currentUser.getId(), exerciseId));
  }

  @GetMapping("/prs")
  public ResponseEntity<List<ExerciseLogResponse>> getPersonalRecords(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(getStudentPersonalRecordsUseCase.execute(currentUser.getId()));
  }

  @GetMapping("/exercise-stats")
  public ResponseEntity<List<ExerciseStatsResponse>> getExerciseStats(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(getExerciseStatsUseCase.execute(currentUser.getId()));
  }
}
