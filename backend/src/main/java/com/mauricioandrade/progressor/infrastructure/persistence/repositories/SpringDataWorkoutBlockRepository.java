package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutBlockEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWorkoutBlockRepository extends JpaRepository<WorkoutBlockEntity, UUID> {

  List<WorkoutBlockEntity> findByWorkoutPlanIdOrderByPosition(UUID workoutPlanId);
}
