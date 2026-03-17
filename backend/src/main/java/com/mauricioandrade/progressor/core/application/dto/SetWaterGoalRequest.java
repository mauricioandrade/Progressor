package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record SetWaterGoalRequest(@NotNull @Min(1) Integer goal) {}
