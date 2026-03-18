package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutPlanEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWorkoutPlanRepository extends JpaRepository<WorkoutPlanEntity, UUID> {

  List<WorkoutPlanEntity> findByStudentId(UUID studentId);
}
