package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record LogExerciseRequest(
    @NotNull UUID exerciseId,
    @NotBlank String exerciseName,
    @NotNull @Min(1) Integer actualReps,
    Double actualWeight) {

}
