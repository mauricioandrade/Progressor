package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.CreateMealPlanRequest;
import com.mauricioandrade.progressor.core.application.ports.MealPlanRepository;
import com.mauricioandrade.progressor.core.application.ports.PushNotificationPort;
import com.mauricioandrade.progressor.core.domain.nutrition.MealItem;
import com.mauricioandrade.progressor.core.domain.nutrition.MealPlan;
import java.util.UUID;

public class CreateMealPlanUseCase {

  private final MealPlanRepository mealPlanRepository;
  private final PushNotificationPort pushNotification;

  public CreateMealPlanUseCase(MealPlanRepository mealPlanRepository,
      PushNotificationPort pushNotification) {
    this.mealPlanRepository = mealPlanRepository;
    this.pushNotification = pushNotification;
  }

  public UUID execute(CreateMealPlanRequest request, UUID nutritionistId) {
    var items = request.items().stream()
        .map(i -> new MealItem(UUID.randomUUID(), i.mealTime(), i.name(), i.foodDescription(),
            i.caloriesKcal(), i.proteinG(), i.carbsG(), i.fatG(), i.quantity(), i.baseUnit()))
        .toList();
    boolean cheat = request.cheatMeal() != null && request.cheatMeal();
    var plan = new MealPlan(UUID.randomUUID(), request.studentId(), nutritionistId,
        request.name(), request.goal(), items, cheat);
    mealPlanRepository.save(plan);
    pushNotification.sendToStudent(request.studentId(),
        "Novo plano alimentar! 🥗", "Seu nutricionista criou um novo plano alimentar para você.");
    return plan.getId();
  }
}
