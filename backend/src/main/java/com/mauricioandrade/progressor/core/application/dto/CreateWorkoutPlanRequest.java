package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record CreateWorkoutPlanRequest(
    @NotNull UUID studentId,
    @NotBlank String name) {

}
