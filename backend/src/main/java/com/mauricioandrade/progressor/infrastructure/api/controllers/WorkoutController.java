package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutRequest;
import com.mauricioandrade.progressor.core.application.dto.WorkoutExerciseResponse;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentWorkoutUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetTodayWorkoutUseCase;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import jakarta.validation.Valid;
import java.time.LocalDate;
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
public class WorkoutController {

  private final CreateWorkoutUseCase createWorkoutUseCase;
  private final GetStudentWorkoutUseCase getStudentWorkoutUseCase;
  private final GetTodayWorkoutUseCase getTodayWorkoutUseCase;

  public WorkoutController(CreateWorkoutUseCase createWorkoutUseCase,
      GetStudentWorkoutUseCase getStudentWorkoutUseCase,
      GetTodayWorkoutUseCase getTodayWorkoutUseCase) {
    this.createWorkoutUseCase = createWorkoutUseCase;
    this.getStudentWorkoutUseCase = getStudentWorkoutUseCase;
    this.getTodayWorkoutUseCase = getTodayWorkoutUseCase;
  }

  @PostMapping
  public ResponseEntity<Void> createWorkout(@Valid @RequestBody CreateWorkoutRequest request) {
    createWorkoutUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<WorkoutExerciseResponse>> getStudentWorkout(
      @PathVariable UUID studentId) {
    return ResponseEntity.ok(getStudentWorkoutUseCase.execute(studentId));
  }

  @GetMapping("/my")
  public ResponseEntity<List<WorkoutExerciseResponse>> getMyWorkout(
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(getStudentWorkoutUseCase.execute(currentUser.getId()));
  }

  @GetMapping("/today")
  public ResponseEntity<List<WorkoutExerciseResponse>> getTodayWorkout(
      @AuthenticationPrincipal UserEntity currentUser) {
    String day = LocalDate.now().getDayOfWeek().name().substring(0, 3);
    return ResponseEntity.ok(getTodayWorkoutUseCase.execute(currentUser.getId(), day));
  }
}
