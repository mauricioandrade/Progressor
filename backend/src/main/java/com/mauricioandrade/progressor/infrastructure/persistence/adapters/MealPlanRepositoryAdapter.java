package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.MealPlanRepository;
import com.mauricioandrade.progressor.core.domain.nutrition.MealItem;
import com.mauricioandrade.progressor.core.domain.nutrition.MealPlan;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.MealItemEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.MealPlanEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataMealPlanRepository;
import java.util.ArrayList;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class MealPlanRepositoryAdapter implements MealPlanRepository {

  private final SpringDataMealPlanRepository springDataRepository;

  public MealPlanRepositoryAdapter(SpringDataMealPlanRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void save(MealPlan mealPlan) {
    MealPlanEntity entity = new MealPlanEntity();
    entity.setId(mealPlan.getId());
    entity.setStudentId(mealPlan.getStudentId());
    entity.setNutritionistId(mealPlan.getNutritionistId());
    entity.setName(mealPlan.getName());
    entity.setGoal(mealPlan.getGoal());
    entity.setCheatMeal(mealPlan.isCheatMeal());
    var items = mealPlan.getItems().stream().map(i -> {
      MealItemEntity ie = new MealItemEntity();
      ie.setId(i.getId());
      ie.setMealTime(i.getMealTime());
      ie.setName(i.getName());
      ie.setFoodDescription(i.getFoodDescription());
      ie.setCaloriesKcal(i.getCaloriesKcal());
      ie.setProteinG(i.getProteinG());
      ie.setCarbsG(i.getCarbsG());
      ie.setFatG(i.getFatG());
      ie.setQuantity(i.getQuantity());
      ie.setBaseUnit(i.getBaseUnit());
      ie.setMealPlan(entity);
      return ie;
    }).toList();
    entity.setItems(new ArrayList<>(items));
    springDataRepository.save(entity);
  }

  @Override
  public Optional<MealPlan> findByStudentId(UUID studentId) {
    return springDataRepository.findTopByStudentIdOrderByIdDesc(studentId).map(e -> {
      var items = e.getItems().stream().map(i -> new MealItem(i.getId(), i.getMealTime(), i.getName(),
          i.getFoodDescription(), i.getCaloriesKcal(), i.getProteinG(), i.getCarbsG(), i.getFatG(),
          i.getQuantity(), i.getBaseUnit())).toList();
      return new MealPlan(e.getId(), e.getStudentId(), e.getNutritionistId(), e.getName(),
          e.getGoal(), items, e.isCheatMeal());
    });
  }
}
