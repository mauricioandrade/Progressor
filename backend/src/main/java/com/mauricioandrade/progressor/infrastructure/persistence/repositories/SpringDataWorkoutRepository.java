package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutExerciseEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataWorkoutRepository extends JpaRepository<WorkoutExerciseEntity, UUID> {

  List<WorkoutExerciseEntity> findByStudentId(UUID studentId);
}
