package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.DailyAdherenceResponse;
import com.mauricioandrade.progressor.core.application.ports.MealConsumptionLogRepository;
import com.mauricioandrade.progressor.core.application.ports.MealPlanRepository;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class GetStudentAdherenceUseCase {

  private final MealConsumptionLogRepository consumptionRepository;
  private final MealPlanRepository mealPlanRepository;

  public GetStudentAdherenceUseCase(MealConsumptionLogRepository consumptionRepository,
      MealPlanRepository mealPlanRepository) {
    this.consumptionRepository = consumptionRepository;
    this.mealPlanRepository = mealPlanRepository;
  }

  public List<DailyAdherenceResponse> execute(UUID studentId, int days) {
    int totalItems = mealPlanRepository.findByStudentId(studentId)
        .map(plan -> plan.getItems().size())
        .orElse(0);

    List<DailyAdherenceResponse> result = new ArrayList<>();
    LocalDate today = LocalDate.now();
    for (int i = days - 1; i >= 0; i--) {
      LocalDate date = today.minusDays(i);
      Set<UUID> consumed = consumptionRepository.findConsumedItemIds(studentId, date);
      result.add(new DailyAdherenceResponse(date, totalItems, consumed.size()));
    }
    return result;
  }
}
