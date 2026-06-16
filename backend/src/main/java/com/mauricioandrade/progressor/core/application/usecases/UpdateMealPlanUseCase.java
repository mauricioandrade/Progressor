package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.UpdateMealPlanRequest;
import com.mauricioandrade.progressor.core.application.ports.MealPlanRepository;
import com.mauricioandrade.progressor.core.application.ports.PushNotificationPort;
import com.mauricioandrade.progressor.core.domain.nutrition.MealItem;
import com.mauricioandrade.progressor.core.domain.nutrition.MealPlan;
import java.util.List;
import java.util.UUID;

public class UpdateMealPlanUseCase {

  private final MealPlanRepository mealPlanRepository;
  private final PushNotificationPort pushNotification;

  public UpdateMealPlanUseCase(MealPlanRepository mealPlanRepository,
      PushNotificationPort pushNotification) {
    this.mealPlanRepository = mealPlanRepository;
    this.pushNotification = pushNotification;
  }

  public void execute(UUID planId, UpdateMealPlanRequest request) {
    MealPlan existing = mealPlanRepository.findById(planId)
        .orElseThrow(() -> new IllegalArgumentException("Meal plan not found: " + planId));
    boolean cheat = request.cheatMeal() != null && request.cheatMeal();
    List<MealItem> items = (request.items() != null && !request.items().isEmpty())
        ? request.items().stream()
            .map(i -> new MealItem(UUID.randomUUID(), i.mealTime(), i.name(), i.foodDescription(),
                i.caloriesKcal(), i.proteinG(), i.carbsG(), i.fatG(), i.quantity(), i.baseUnit()))
            .toList()
        : existing.getItems();
    MealPlan updated = new MealPlan(existing.getId(), existing.getStudentId(),
        existing.getNutritionistId(), request.name(), request.goal(), items, cheat);
    mealPlanRepository.save(updated);
    pushNotification.sendToStudent(existing.getStudentId(),
        "Plano alimentar atualizado! 🥗", "Seu nutricionista atualizou seu plano alimentar.");
  }
}
