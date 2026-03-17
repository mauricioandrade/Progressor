package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record MealItemRequest(
    @NotBlank String mealTime,
    @NotBlank String name,
    String foodDescription,
    @NotNull Double caloriesKcal,
    @NotNull Double proteinG,
    @NotNull Double carbsG,
    @NotNull Double fatG,
    @NotNull Double quantity,
    @NotBlank String baseUnit
) {}
