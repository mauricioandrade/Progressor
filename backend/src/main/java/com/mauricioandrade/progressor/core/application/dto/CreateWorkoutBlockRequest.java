package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateWorkoutBlockRequest(
    @NotNull UUID workoutPlanId,
    @NotBlank String name,
    @Min(0) int position) {

}
