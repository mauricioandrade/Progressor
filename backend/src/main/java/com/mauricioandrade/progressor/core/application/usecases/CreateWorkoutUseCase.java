package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutExerciseRequest;
import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutRequest;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import com.mauricioandrade.progressor.core.domain.workout.MeasurementType;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutExercise;
import java.util.ArrayList;
import java.util.List;

public class CreateWorkoutUseCase {

  private final WorkoutRepository workoutRepository;
  private final UserRepository userRepository;

  public CreateWorkoutUseCase(WorkoutRepository workoutRepository, UserRepository userRepository) {
    this.workoutRepository = workoutRepository;
    this.userRepository = userRepository;
  }

  public void execute(CreateWorkoutRequest request) {
    if (!userRepository.existsById(request.studentId())) {
      throw new IllegalArgumentException("Student not found");
    }

    List<WorkoutExercise> workoutExercises = new ArrayList<>();

    for (CreateWorkoutExerciseRequest exerciseDto : request.exercises()) {
      MeasurementType type = MeasurementType.valueOf(exerciseDto.measurementType().toUpperCase());

      WorkoutExercise exercise = new WorkoutExercise(null, exerciseDto.name(), exerciseDto.sets(),
          exerciseDto.repetitions(), type);

      if (exerciseDto.weightInKg() != null) {
        exercise.applyWeight(exerciseDto.weightInKg());
      }
      if (exerciseDto.speed() != null) {
        exercise.applySpeed(exerciseDto.speed());
      }
      if (exerciseDto.timeInSeconds() != null) {
        exercise.applyTime(exerciseDto.timeInSeconds());
      }
      if (exerciseDto.cadence() != null) {
        exercise.applyCadence(exerciseDto.cadence());
      }

      workoutExercises.add(exercise);
    }

    workoutRepository.saveAll(workoutExercises, request.studentId());
  }
}