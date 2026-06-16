package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.MealConsumptionResponse;
import com.mauricioandrade.progressor.core.application.ports.MealConsumptionLogRepository;
import java.time.LocalDate;
import java.util.UUID;

public class ToggleMealConsumptionUseCase {

  private final MealConsumptionLogRepository repository;

  public ToggleMealConsumptionUseCase(MealConsumptionLogRepository repository) {
    this.repository = repository;
  }

  public MealConsumptionResponse execute(UUID studentId, UUID mealItemId) {
    LocalDate today = LocalDate.now();
    if (repository.isConsumed(studentId, mealItemId, today)) {
      repository.remove(studentId, mealItemId, today);
    } else {
      repository.log(studentId, mealItemId, today);
    }
    return new MealConsumptionResponse(
        repository.findConsumedItemIds(studentId, today),
        repository.findExtraItems(studentId, today));
  }
}
