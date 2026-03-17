package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateWorkoutRequest(
    @NotNull UUID studentId,
    @NotEmpty @Valid List<CreateWorkoutExerciseRequest> exercises) {

}
