package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutRequest;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutUseCase;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/workouts")
public class WorkoutController {

  private final CreateWorkoutUseCase createWorkoutUseCase;

  public WorkoutController(CreateWorkoutUseCase createWorkoutUseCase) {
    this.createWorkoutUseCase = createWorkoutUseCase;
  }

  @PostMapping
  public ResponseEntity<Void> createWorkout(@RequestBody CreateWorkoutRequest request) {
    createWorkoutUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }
}