package com.mauricioandrade.progressor.core.application.dto;

import java.util.UUID;

public record WorkoutExerciseResponse(UUID id, String name, Integer sets, Integer repetitions,
    String measurementType, Double weightInKg, Double speed, Integer timeInSeconds, String cadence,
    Double tonnage, String videoUrl, Integer restTime, String workoutLabel, String scheduledDays) {

}
