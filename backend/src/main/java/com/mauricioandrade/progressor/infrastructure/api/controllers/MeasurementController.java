package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.MeasurementResponse;
import com.mauricioandrade.progressor.core.application.dto.RecordMeasurementRequest;
import com.mauricioandrade.progressor.core.application.dto.SelfMeasurementRequest;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentMeasurementsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RecordMeasurementUseCase;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
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
@RequestMapping("/api/measurements")
public class MeasurementController {

  private final RecordMeasurementUseCase recordMeasurementUseCase;
  private final GetStudentMeasurementsUseCase getStudentMeasurementsUseCase;

  public MeasurementController(RecordMeasurementUseCase recordMeasurementUseCase,
      GetStudentMeasurementsUseCase getStudentMeasurementsUseCase) {
    this.recordMeasurementUseCase = recordMeasurementUseCase;
    this.getStudentMeasurementsUseCase = getStudentMeasurementsUseCase;
  }

  @PostMapping
  public ResponseEntity<Map<String, UUID>> recordMeasurement(
      @Valid @RequestBody RecordMeasurementRequest request) {
    UUID id = recordMeasurementUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
  }

  @PostMapping("/my")
  public ResponseEntity<Map<String, UUID>> recordSelfMeasurement(
      @RequestBody SelfMeasurementRequest request,
      @AuthenticationPrincipal UserEntity currentUser) {
    RecordMeasurementRequest fullRequest = new RecordMeasurementRequest(currentUser.getId(),
        request.weight(), request.bodyFatPercentage(), request.rightBicep(), request.leftBicep(),
        request.chest(), request.waist(), request.abdomen(), request.hips(), request.leftThigh(),
        request.rightThigh(), request.rightCalf(), request.leftCalf());
    UUID id = recordMeasurementUseCase.execute(fullRequest);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
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
}
