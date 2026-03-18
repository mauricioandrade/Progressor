package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutBlockRequest;
import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutPlanRequest;
import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutRequest;
import com.mauricioandrade.progressor.core.application.dto.WorkoutBlockResponse;
import com.mauricioandrade.progressor.core.application.dto.WorkoutExerciseResponse;
import com.mauricioandrade.progressor.core.application.dto.WorkoutPlanResponse;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutBlockUseCase;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutPlanUseCase;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentWorkoutPlansUseCase;
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
  private final CreateWorkoutPlanUseCase createWorkoutPlanUseCase;
  private final GetStudentWorkoutPlansUseCase getStudentWorkoutPlansUseCase;
  private final CreateWorkoutBlockUseCase createWorkoutBlockUseCase;

  public WorkoutController(CreateWorkoutUseCase createWorkoutUseCase,
      GetStudentWorkoutUseCase getStudentWorkoutUseCase,
      GetTodayWorkoutUseCase getTodayWorkoutUseCase,
      CreateWorkoutPlanUseCase createWorkoutPlanUseCase,
      GetStudentWorkoutPlansUseCase getStudentWorkoutPlansUseCase,
      CreateWorkoutBlockUseCase createWorkoutBlockUseCase) {
    this.createWorkoutUseCase = createWorkoutUseCase;
    this.getStudentWorkoutUseCase = getStudentWorkoutUseCase;
    this.getTodayWorkoutUseCase = getTodayWorkoutUseCase;
    this.createWorkoutPlanUseCase = createWorkoutPlanUseCase;
    this.getStudentWorkoutPlansUseCase = getStudentWorkoutPlansUseCase;
    this.createWorkoutBlockUseCase = createWorkoutBlockUseCase;
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

  // ---- Workout Plan endpoints ----

  @PostMapping("/plans")
  public ResponseEntity<WorkoutPlanResponse> createPlan(
      @Valid @RequestBody CreateWorkoutPlanRequest request,
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(createWorkoutPlanUseCase.execute(request, currentUser.getId()));
  }

  @GetMapping("/plans/student/{studentId}")
  public ResponseEntity<List<WorkoutPlanResponse>> getPlansForStudent(
      @PathVariable UUID studentId) {
    return ResponseEntity.ok(getStudentWorkoutPlansUseCase.execute(studentId));
  }

  @GetMapping("/plans/my")
  public ResponseEntity<List<WorkoutPlanResponse>> getMyPlans(
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(getStudentWorkoutPlansUseCase.execute(currentUser.getId()));
  }

  // ---- Workout Block endpoints ----

  @PostMapping("/blocks")
  public ResponseEntity<WorkoutBlockResponse> createBlock(
      @Valid @RequestBody CreateWorkoutBlockRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(createWorkoutBlockUseCase.execute(request));
  }
}
