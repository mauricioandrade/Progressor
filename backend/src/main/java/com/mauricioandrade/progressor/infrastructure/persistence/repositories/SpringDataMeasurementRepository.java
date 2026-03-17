package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.BodyMeasurementEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMeasurementRepository extends
    JpaRepository<BodyMeasurementEntity, UUID> {

  List<BodyMeasurementEntity> findByStudentId(UUID studentId);
}
