package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.MealItemResponse;
import com.mauricioandrade.progressor.core.application.dto.MealPlanResponse;
import com.mauricioandrade.progressor.core.application.ports.MealPlanRepository;
import java.util.Optional;
import java.util.UUID;

public class GetStudentMealPlanUseCase {

  private final MealPlanRepository mealPlanRepository;

  public GetStudentMealPlanUseCase(MealPlanRepository mealPlanRepository) {
    this.mealPlanRepository = mealPlanRepository;
  }

  public Optional<MealPlanResponse> execute(UUID studentId) {
    return mealPlanRepository.findByStudentId(studentId).map(plan ->
        new MealPlanResponse(plan.getId(), plan.getStudentId(), plan.getName(), plan.getGoal(),
            plan.getItems().stream().map(i -> new MealItemResponse(i.getId(), i.getMealTime(),
                i.getName(), i.getFoodDescription(), i.getCaloriesKcal(), i.getProteinG(),
                i.getCarbsG(), i.getFatG(), i.getQuantity(), i.getBaseUnit())).toList(),
            plan.isCheatMeal()));
  }
}
