package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.nutrition.MealPlan;
import java.util.Optional;
import java.util.UUID;

public interface MealPlanRepository {

  void save(MealPlan mealPlan);

  Optional<MealPlan> findByStudentId(UUID studentId);
}
