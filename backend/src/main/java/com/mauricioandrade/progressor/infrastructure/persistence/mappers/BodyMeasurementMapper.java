package com.mauricioandrade.progressor.infrastructure.persistence.mappers;

import com.mauricioandrade.progressor.core.domain.measurement.BodyMeasurement;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.BodyMeasurementEntity;

public class BodyMeasurementMapper {

  public static BodyMeasurementEntity toEntity(BodyMeasurement domain) {
    BodyMeasurementEntity entity = new BodyMeasurementEntity();
    entity.setId(domain.getId());
    entity.setStudentId(domain.getStudentId());
    entity.setRecordedAt(domain.getRecordedAt());
    entity.setWeight(domain.getWeight());
    entity.setBodyFatPercentage(domain.getBodyFatPercentage());
    entity.setRightBicep(domain.getRightBicep());
    entity.setLeftBicep(domain.getLeftBicep());
    entity.setChest(domain.getChest());
    entity.setWaist(domain.getWaist());
    entity.setAbdomen(domain.getAbdomen());
    entity.setHips(domain.getHips());
    entity.setLeftThigh(domain.getLeftThigh());
    entity.setRightThigh(domain.getRightThigh());
    entity.setRightCalf(domain.getRightCalf());
    entity.setLeftCalf(domain.getLeftCalf());
    return entity;
  }

  public static BodyMeasurement toDomain(BodyMeasurementEntity entity) {
    BodyMeasurement measurement = new BodyMeasurement(entity.getId(), entity.getStudentId(),
        entity.getRecordedAt());
    measurement.updateMeasurements(entity.getWeight(), entity.getBodyFatPercentage(),
        entity.getRightBicep(), entity.getLeftBicep(), entity.getChest(),
        entity.getWaist(), entity.getAbdomen(), entity.getHips(), entity.getLeftThigh(),
        entity.getRightThigh(), entity.getRightCalf(), entity.getLeftCalf());
    return measurement;
  }
}
