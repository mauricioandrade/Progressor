package com.mauricioandrade.progressor.core.application.dto;

public record FoodItemResponse(
    String foodId,
    String name,
    String brandName,       // null for generic / whole foods
    Double caloriesKcal,
    Double proteinG,
    Double carbsG,
    Double fatG,
    String description
) {}
