package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.WorkoutSessionRequest;
import com.mauricioandrade.progressor.core.application.dto.WorkoutSessionResponse;
import com.mauricioandrade.progressor.core.application.usecases.GetMyWorkoutSessionsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SaveWorkoutSessionUseCase;
import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts/sessions")
public class WorkoutSessionController {

  private final SaveWorkoutSessionUseCase saveUseCase;
  private final GetMyWorkoutSessionsUseCase getUseCase;

  public WorkoutSessionController(SaveWorkoutSessionUseCase saveUseCase,
      GetMyWorkoutSessionsUseCase getUseCase) {
    this.saveUseCase = saveUseCase;
    this.getUseCase = getUseCase;
  }

  @PostMapping
  public ResponseEntity<Void> save(@RequestBody WorkoutSessionRequest request,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    saveUseCase.execute(currentUser.getId(), request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  @GetMapping("/my")
  public ResponseEntity<List<WorkoutSessionResponse>> getMy(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(getUseCase.execute(currentUser.getId()));
  }
}
