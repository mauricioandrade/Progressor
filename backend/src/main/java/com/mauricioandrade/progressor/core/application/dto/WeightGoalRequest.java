package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record WeightGoalRequest(@NotNull @Positive Double goal) {

}
