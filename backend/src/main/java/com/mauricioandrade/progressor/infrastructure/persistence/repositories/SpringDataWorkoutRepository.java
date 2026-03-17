package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutExerciseEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataWorkoutRepository extends JpaRepository<WorkoutExerciseEntity, UUID> {

  List<WorkoutExerciseEntity> findByStudentId(UUID studentId);

  @Query("SELECT e FROM WorkoutExerciseEntity e WHERE e.studentId = :studentId AND e.scheduledDays LIKE %:day%")
  List<WorkoutExerciseEntity> findByStudentIdAndScheduledDayContaining(
      @Param("studentId") UUID studentId, @Param("day") String day);
}
