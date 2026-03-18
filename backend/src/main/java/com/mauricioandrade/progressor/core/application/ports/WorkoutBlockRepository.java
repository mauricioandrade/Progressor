package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.workout.WorkoutBlock;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutBlockRepository {

  void save(WorkoutBlock block);

  Optional<WorkoutBlock> findById(UUID id);

  List<WorkoutBlock> findByWorkoutPlanId(UUID workoutPlanId);
}
