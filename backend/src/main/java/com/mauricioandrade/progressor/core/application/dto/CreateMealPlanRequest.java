package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.UUID;

public record CreateMealPlanRequest(
    @NotNull UUID studentId,
    @NotBlank String name,
    @NotBlank String goal,
    @NotEmpty @Valid List<MealItemRequest> items,
    Boolean cheatMeal
) {}
