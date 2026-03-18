package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.workout.WorkoutPlan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface WorkoutPlanRepository {

  void save(WorkoutPlan plan);

  Optional<WorkoutPlan> findById(UUID id);

  List<WorkoutPlan> findByStudentId(UUID studentId);
}
