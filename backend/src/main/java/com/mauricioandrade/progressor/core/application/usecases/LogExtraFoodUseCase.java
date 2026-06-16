package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.MealConsumptionResponse;
import com.mauricioandrade.progressor.core.application.ports.MealConsumptionLogRepository;
import java.time.LocalDate;
import java.util.UUID;

public class LogExtraFoodUseCase {

  private final MealConsumptionLogRepository repository;

  public LogExtraFoodUseCase(MealConsumptionLogRepository repository) {
    this.repository = repository;
  }

  public MealConsumptionResponse execute(UUID studentId, String foodName, Double caloriesKcal,
      Double proteinG, Double carbsG, Double fatG, Double quantity, String baseUnit) {
    LocalDate today = LocalDate.now();
    repository.logExtra(studentId, today, foodName, caloriesKcal, proteinG, carbsG, fatG,
        quantity, baseUnit);
    return new MealConsumptionResponse(
        repository.findConsumedItemIds(studentId, today),
        repository.findExtraItems(studentId, today));
  }
}
