package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record UpdateWaterIntakeRequest(@NotNull @Min(1) Integer amount) {}
