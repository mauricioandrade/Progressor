package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record UpdateMealPlanRequest(
    @NotBlank String name,
    @NotBlank String goal,
    Boolean cheatMeal,
    @Valid List<MealItemRequest> items
) {}
