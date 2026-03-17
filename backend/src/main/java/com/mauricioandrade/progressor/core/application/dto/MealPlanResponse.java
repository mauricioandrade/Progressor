package com.mauricioandrade.progressor.core.application.dto;

import java.util.List;
import java.util.UUID;

public record MealPlanResponse(
    UUID id,
    UUID studentId,
    String name,
    String goal,
    List<MealItemResponse> items,
    boolean cheatMeal
) {}
