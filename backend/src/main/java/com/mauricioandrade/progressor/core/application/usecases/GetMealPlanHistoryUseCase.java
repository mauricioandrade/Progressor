package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.MealPlanSummaryResponse;
import com.mauricioandrade.progressor.core.application.ports.MealPlanRepository;
import java.util.List;
import java.util.UUID;

public class GetMealPlanHistoryUseCase {

  private final MealPlanRepository mealPlanRepository;

  public GetMealPlanHistoryUseCase(MealPlanRepository mealPlanRepository) {
    this.mealPlanRepository = mealPlanRepository;
  }

  public List<MealPlanSummaryResponse> execute(UUID studentId) {
    return mealPlanRepository.findAllByStudentId(studentId).stream()
        .map(p -> new MealPlanSummaryResponse(p.getId(), p.getName(), p.getGoal(), p.isCheatMeal()))
        .toList();
  }
}
