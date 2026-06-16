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

  // Returns one row per exercise: [exercise_id, actual_weight, actual_reps]
  @Query(value = """
      SELECT DISTINCT ON (exercise_id) exercise_id, actual_weight, actual_reps
      FROM workout_logs
      WHERE student_id = :studentId
      ORDER BY exercise_id, completed_at DESC
      """, nativeQuery = true)
  List<Object[]> findLastSessionPerExercise(@Param("studentId") UUID studentId);

  // Returns one row per exercise: [exercise_id, max_weight, reps_at_max_weight]
  @Query(value = """
      SELECT w.exercise_id, w.actual_weight AS max_weight, w.actual_reps
      FROM workout_logs w
      INNER JOIN (
        SELECT exercise_id, MAX(actual_weight) AS max_w
        FROM workout_logs WHERE student_id = :studentId
        GROUP BY exercise_id
      ) pr ON pr.exercise_id = w.exercise_id AND w.actual_weight = pr.max_w
           AND w.student_id = :studentId
      ORDER BY w.exercise_id, w.completed_at DESC
      """, nativeQuery = true)
  List<Object[]> findPrPerExercise(@Param("studentId") UUID studentId);
}
