package com.mauricioandrade.progressor.core.application.dto;

import java.util.List;
import java.util.Set;
import java.util.UUID;

public record MealConsumptionResponse(Set<UUID> consumedItemIds, List<ExtraFoodLogResponse> extraItems) {}
