package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutSessionEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWorkoutSessionRepository extends JpaRepository<WorkoutSessionEntity, UUID> {

  List<WorkoutSessionEntity> findByStudentIdOrderBySessionDateDescCreatedAtDesc(UUID studentId, Pageable pageable);
}
