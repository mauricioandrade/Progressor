package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.workout.WorkoutLog;
import java.util.List;
import java.util.UUID;

public interface WorkoutLogRepository {

  void save(WorkoutLog log);

  List<WorkoutLog> findByStudentIdAndExerciseId(UUID studentId, UUID exerciseId);

  List<WorkoutLog> findLatestPrsByStudentId(UUID studentId, int limit);
}
