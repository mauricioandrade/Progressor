package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.measurement.BodyMeasurement;

public interface MeasurementRepository {

  void save(BodyMeasurement measurement);
}