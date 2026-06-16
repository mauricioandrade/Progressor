package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record WorkoutSessionResponse(
    UUID id,
    LocalDate sessionDate,
    int exercises,
    int sets,
    double tonnageKg,
    int prCount
) {}
