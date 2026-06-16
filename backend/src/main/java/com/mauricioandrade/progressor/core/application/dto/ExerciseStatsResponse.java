package com.mauricioandrade.progressor.core.application.dto;

import java.util.UUID;

public record ExerciseStatsResponse(
    UUID exerciseId,
    Double lastWeight,
    Integer lastReps,
    Double prWeight,
    Integer prReps
) {}
