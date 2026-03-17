package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WorkoutExerciseResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import java.util.List;
import java.util.UUID;

public class GetTodayWorkoutUseCase {

  private final WorkoutRepository workoutRepository;

  public GetTodayWorkoutUseCase(WorkoutRepository workoutRepository) {
    this.workoutRepository = workoutRepository;
  }

  public List<WorkoutExerciseResponse> execute(UUID studentId, String day) {
    return workoutRepository.findByStudentIdAndScheduledDay(studentId, day).stream()
        .map(e -> new WorkoutExerciseResponse(e.getId(), e.getName(), e.getSets(),
            e.getRepetitions(), e.getMeasurementType().name(), e.getWeightInKg(), e.getSpeed(),
            e.getTimeInSeconds(), e.getCadence(), e.getTonnage(), e.getVideoUrl(), e.getRestTime(),
            e.getWorkoutLabel(), e.getScheduledDays()))
        .toList();
  }
}
