package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.MeasurementCreatedResponse;
import com.mauricioandrade.progressor.core.application.dto.MeasurementResponse;
import com.mauricioandrade.progressor.core.application.dto.RecordMeasurementRequest;
import com.mauricioandrade.progressor.core.application.dto.SelfMeasurementRequest;
import com.mauricioandrade.progressor.core.application.dto.WeightGoalRequest;
import com.mauricioandrade.progressor.core.application.dto.WeightGoalResponse;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentMeasurementsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetWeightGoalUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RecordMeasurementUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateWeightGoalUseCase;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

  private final RecordMeasurementUseCase recordMeasurementUseCase;
  private final GetStudentMeasurementsUseCase getStudentMeasurementsUseCase;
  private final UpdateWeightGoalUseCase updateWeightGoalUseCase;
  private final GetWeightGoalUseCase getWeightGoalUseCase;

  public MeasurementController(RecordMeasurementUseCase recordMeasurementUseCase,
      GetStudentMeasurementsUseCase getStudentMeasurementsUseCase,
      UpdateWeightGoalUseCase updateWeightGoalUseCase,
      GetWeightGoalUseCase getWeightGoalUseCase) {
    this.recordMeasurementUseCase = recordMeasurementUseCase;
    this.getStudentMeasurementsUseCase = getStudentMeasurementsUseCase;
    this.updateWeightGoalUseCase = updateWeightGoalUseCase;
    this.getWeightGoalUseCase = getWeightGoalUseCase;
  }

  @PostMapping
  public ResponseEntity<MeasurementCreatedResponse> recordMeasurement(
      @Valid @RequestBody RecordMeasurementRequest request) {
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(recordMeasurementUseCase.execute(request));
  }

  @PostMapping("/my")
  public ResponseEntity<MeasurementCreatedResponse> recordSelfMeasurement(
      @RequestBody SelfMeasurementRequest request,
      @AuthenticationPrincipal UserEntity currentUser) {
    RecordMeasurementRequest fullRequest = new RecordMeasurementRequest(currentUser.getId(),
        request.weight(), request.bodyFatPercentage(), request.rightBicep(), request.leftBicep(),
        request.chest(), request.waist(), request.abdomen(), request.hips(), request.leftThigh(),
        request.rightThigh(), request.rightCalf(), request.leftCalf());
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(recordMeasurementUseCase.execute(fullRequest));
  }

  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<MeasurementResponse>> getStudentMeasurements(
      @PathVariable UUID studentId) {
    return ResponseEntity.ok(getStudentMeasurementsUseCase.execute(studentId));
  }

  @GetMapping("/my")
  public ResponseEntity<List<MeasurementResponse>> getMyMeasurements(
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(getStudentMeasurementsUseCase.execute(currentUser.getId()));
  }

  // ---- Weight Goal endpoints ----

  @GetMapping("/my/weight-goal")
  public ResponseEntity<WeightGoalResponse> getMyWeightGoal(
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(getWeightGoalUseCase.execute(currentUser.getId()));
  }

  @PutMapping("/my/weight-goal")
  public ResponseEntity<Void> updateMyWeightGoal(
      @Valid @RequestBody WeightGoalRequest request,
      @AuthenticationPrincipal UserEntity currentUser) {
    updateWeightGoalUseCase.execute(currentUser.getId(), request.goal());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/weight-goal/student/{studentId}")
  public ResponseEntity<WeightGoalResponse> getStudentWeightGoal(
      @PathVariable UUID studentId) {
    return ResponseEntity.ok(getWeightGoalUseCase.execute(studentId));
  }

  @PutMapping("/weight-goal/{studentId}")
  public ResponseEntity<Void> updateStudentWeightGoal(
      @PathVariable UUID studentId,
      @Valid @RequestBody WeightGoalRequest request) {
    updateWeightGoalUseCase.execute(studentId, request.goal());
    return ResponseEntity.noContent().build();
  }
}
