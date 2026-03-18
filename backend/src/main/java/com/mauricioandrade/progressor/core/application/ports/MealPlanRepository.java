package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.nutrition.MealPlan;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MealPlanRepository {

  void save(MealPlan mealPlan);

  Optional<MealPlan> findByStudentId(UUID studentId);

  Optional<MealPlan> findById(UUID id);

  void deleteById(UUID id);

  void updateMetadata(UUID id, String name, String goal, boolean cheatMeal);

  List<MealPlan> findAllByStudentId(UUID studentId);
}
