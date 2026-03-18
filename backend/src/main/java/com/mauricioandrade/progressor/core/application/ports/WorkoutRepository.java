package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.workout.WorkoutExercise;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutRepository {

  void saveAll(List<WorkoutExercise> exercises, UUID studentId);

  List<WorkoutExercise> findByStudentId(UUID studentId);

  List<WorkoutExercise> findByStudentIdAndScheduledDay(UUID studentId, String day);

  List<WorkoutExercise> findByBlockId(UUID blockId);

  Optional<WorkoutExercise> findById(UUID id);

  void deleteById(UUID id);

  WorkoutExercise update(UUID id, WorkoutExercise exercise);
}
