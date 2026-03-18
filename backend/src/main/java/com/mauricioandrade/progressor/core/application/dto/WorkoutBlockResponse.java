package com.mauricioandrade.progressor.core.application.dto;

import java.util.List;
import java.util.UUID;

public record WorkoutBlockResponse(
    UUID id,
    UUID workoutPlanId,
    String name,
    int position,
    List<WorkoutExerciseResponse> exercises) {

}
