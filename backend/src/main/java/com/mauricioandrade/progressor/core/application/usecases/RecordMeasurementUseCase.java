package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.RecordMeasurementRequest;
import com.mauricioandrade.progressor.core.application.ports.MeasurementRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.measurement.BodyMeasurement;
import java.time.LocalDate;
import java.util.UUID;

public class RecordMeasurementUseCase {

  private final MeasurementRepository measurementRepository;
  private final UserRepository userRepository;

  public RecordMeasurementUseCase(MeasurementRepository measurementRepository,
      UserRepository userRepository) {
    this.measurementRepository = measurementRepository;
    this.userRepository = userRepository;
  }

  public UUID execute(RecordMeasurementRequest request) {
    if (!userRepository.existsById(request.studentId())) {
      throw new IllegalArgumentException("Student not found");
    }

    BodyMeasurement measurement = new BodyMeasurement(null, request.studentId(), LocalDate.now());

    measurement.updateMeasurements(request.rightBicep(), request.leftBicep(), request.chest(),
        request.waist(), request.abdomen(), request.hips(), request.leftThigh(),
        request.rightThigh(), request.rightCalf(), request.leftCalf());

    measurementRepository.save(measurement);

    return measurement.getId();
  }
}


