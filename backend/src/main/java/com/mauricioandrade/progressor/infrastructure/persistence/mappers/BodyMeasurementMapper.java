package com.mauricioandrade.progressor.infrastructure.persistence.mappers;

import com.mauricioandrade.progressor.core.domain.measurement.BodyMeasurement;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.BodyMeasurementEntity;

public class BodyMeasurementMapper {

  public static BodyMeasurementEntity toEntity(BodyMeasurement domain) {
    BodyMeasurementEntity entity = new BodyMeasurementEntity();
    entity.setId(domain.getId());
    entity.setStudentId(domain.getStudentId());
    entity.setRecordedAt(domain.getRecordedAt());
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
}


