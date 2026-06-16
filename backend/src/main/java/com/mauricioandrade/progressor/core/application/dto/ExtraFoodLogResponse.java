package com.mauricioandrade.progressor.core.application.dto;

import java.util.UUID;

public record ExtraFoodLogResponse(
    UUID id,
    String foodName,
    Double caloriesKcal,
    Double proteinG,
    Double carbsG,
    Double fatG,
    Double quantity,
    String baseUnit
) {}
