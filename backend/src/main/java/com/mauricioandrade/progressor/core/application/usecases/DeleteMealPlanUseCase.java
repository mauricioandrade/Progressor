package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.MealPlanRepository;
import java.util.UUID;

public class DeleteMealPlanUseCase {

  private final MealPlanRepository mealPlanRepository;

  public DeleteMealPlanUseCase(MealPlanRepository mealPlanRepository) {
    this.mealPlanRepository = mealPlanRepository;
  }

  public void execute(UUID planId) {
    mealPlanRepository.findById(planId)
        .orElseThrow(() -> new IllegalArgumentException("Meal plan not found: " + planId));
    mealPlanRepository.deleteById(planId);
  }
}
