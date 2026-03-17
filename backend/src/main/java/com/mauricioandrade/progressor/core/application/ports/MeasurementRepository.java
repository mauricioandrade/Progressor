package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.measurement.BodyMeasurement;
import java.util.List;
import java.util.UUID;

public interface MeasurementRepository {

  void save(BodyMeasurement measurement);

  List<BodyMeasurement> findByStudentId(UUID studentId);
}
