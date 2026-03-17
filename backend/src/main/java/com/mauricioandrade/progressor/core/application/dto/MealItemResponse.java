package com.mauricioandrade.progressor.core.application.dto;

import java.util.UUID;

public record MealItemResponse(
    UUID id,
    String mealTime,
    String name,
    String foodDescription,
    Double caloriesKcal,
    Double proteinG,
    Double carbsG,
    Double fatG,
    Double quantity,
    String baseUnit
) {}
