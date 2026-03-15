package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.workout.WorkoutExercise;
import java.util.List;
import java.util.UUID;

public interface WorkoutRepository {

  void saveAll(List<WorkoutExercise> exercises, UUID studentId);
}