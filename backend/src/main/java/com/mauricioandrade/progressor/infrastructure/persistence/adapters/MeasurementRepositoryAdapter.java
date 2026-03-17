package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.MeasurementRepository;
import com.mauricioandrade.progressor.core.domain.measurement.BodyMeasurement;
import com.mauricioandrade.progressor.infrastructure.persistence.mappers.BodyMeasurementMapper;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataMeasurementRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class MeasurementRepositoryAdapter implements MeasurementRepository {

  private final SpringDataMeasurementRepository springDataRepository;

  public MeasurementRepositoryAdapter(SpringDataMeasurementRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void save(BodyMeasurement measurement) {
    springDataRepository.save(BodyMeasurementMapper.toEntity(measurement));
  }

  @Override
  public List<BodyMeasurement> findByStudentId(UUID studentId) {
    return springDataRepository.findByStudentId(studentId).stream()
        .map(BodyMeasurementMapper::toDomain).toList();
  }
}
