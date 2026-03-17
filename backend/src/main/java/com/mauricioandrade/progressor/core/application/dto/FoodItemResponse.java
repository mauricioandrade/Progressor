package com.mauricioandrade.progressor.core.application.dto;

public record FoodItemResponse(
    String foodId,
    String name,
    Double caloriesKcal,
    Double proteinG,
    Double carbsG,
    Double fatG,
    String description
) {}
