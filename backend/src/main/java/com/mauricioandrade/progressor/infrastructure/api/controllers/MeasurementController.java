package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.RecordMeasurementRequest;
import com.mauricioandrade.progressor.core.application.usecases.RecordMeasurementUseCase;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/measurements")
public class MeasurementController {

  private final RecordMeasurementUseCase recordMeasurementUseCase;

  public MeasurementController(RecordMeasurementUseCase recordMeasurementUseCase) {
    this.recordMeasurementUseCase = recordMeasurementUseCase;
  }

  @PostMapping
  public ResponseEntity<Map<String, UUID>> recordMeasurement(
      @RequestBody RecordMeasurementRequest request) {
    UUID id = recordMeasurementUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
  }
}