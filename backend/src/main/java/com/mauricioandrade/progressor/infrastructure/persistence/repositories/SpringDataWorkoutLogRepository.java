package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutLogEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataWorkoutLogRepository extends JpaRepository<WorkoutLogEntity, UUID> {

  List<WorkoutLogEntity> findByStudentIdAndExerciseIdOrderByCompletedAtDesc(UUID studentId,
      UUID exerciseId);

  @Query("SELECT w FROM WorkoutLogEntity w WHERE w.studentId = :studentId AND w.personalRecord = true ORDER BY w.completedAt DESC")
  List<WorkoutLogEntity> findPersonalRecordsByStudentId(@Param("studentId") UUID studentId,
      Pageable pageable);
}
