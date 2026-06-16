package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.MealConsumptionResponse;
import com.mauricioandrade.progressor.core.application.ports.MealConsumptionLogRepository;
import java.time.LocalDate;
import java.util.UUID;

public class GetTodayConsumptionUseCase {

  private final MealConsumptionLogRepository repository;

  public GetTodayConsumptionUseCase(MealConsumptionLogRepository repository) {
    this.repository = repository;
  }

  public MealConsumptionResponse execute(UUID studentId) {
    LocalDate today = LocalDate.now();
    return new MealConsumptionResponse(
        repository.findConsumedItemIds(studentId, today),
        repository.findExtraItems(studentId, today));
  }
}
