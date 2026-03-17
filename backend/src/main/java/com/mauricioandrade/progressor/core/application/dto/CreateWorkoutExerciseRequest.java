package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record CreateWorkoutExerciseRequest(
    @NotBlank String name,
    @NotNull @Min(1) Integer sets,
    @NotNull @Min(1) Integer repetitions,
    @NotBlank String measurementType,
    Double weightInKg,
    Double speed,
    Integer timeInSeconds,
    String cadence,
    String videoUrl,
    @Min(0) Integer restTime,
    String workoutLabel,
    List<String> scheduledDays) {

}
