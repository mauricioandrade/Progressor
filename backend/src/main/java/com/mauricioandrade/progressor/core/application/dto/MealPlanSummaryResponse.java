package com.mauricioandrade.progressor.core.application.dto;

import java.util.UUID;

public record MealPlanSummaryResponse(
    UUID id,
    String name,
    String goal,
    boolean cheatMeal
) {}
