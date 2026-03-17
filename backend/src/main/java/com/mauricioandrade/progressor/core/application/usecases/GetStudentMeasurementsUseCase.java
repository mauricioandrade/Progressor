package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.MeasurementResponse;
import com.mauricioandrade.progressor.core.application.ports.MeasurementRepository;
import java.util.List;
import java.util.UUID;

public class GetStudentMeasurementsUseCase {

  private final MeasurementRepository measurementRepository;

  public GetStudentMeasurementsUseCase(MeasurementRepository measurementRepository) {
    this.measurementRepository = measurementRepository;
  }

  public List<MeasurementResponse> execute(UUID studentId) {
    return measurementRepository.findByStudentId(studentId).stream()
        .map(m -> new MeasurementResponse(m.getId(), m.getRecordedAt(), m.getWeight(),
            m.getBodyFatPercentage(), m.getRightBicep(), m.getLeftBicep(), m.getChest(),
            m.getWaist(), m.getAbdomen(), m.getHips(), m.getLeftThigh(), m.getRightThigh(),
            m.getRightCalf(), m.getLeftCalf()))
        .toList();
  }
}
