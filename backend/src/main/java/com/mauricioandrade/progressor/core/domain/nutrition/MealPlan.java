package com.mauricioandrade.progressor.core.domain.nutrition;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class MealPlan {

  private final UUID id;
  private final UUID studentId;
  private final UUID nutritionistId;
  private final String name;
  private final String goal;
  private final List<MealItem> items;
  private final boolean cheatMeal;

  public MealPlan(UUID id, UUID studentId, UUID nutritionistId, String name, String goal,
      List<MealItem> items) {
    this(id, studentId, nutritionistId, name, goal, items, false);
  }

  public MealPlan(UUID id, UUID studentId, UUID nutritionistId, String name, String goal,
      List<MealItem> items, boolean cheatMeal) {
    this.id = id;
    this.studentId = studentId;
    this.nutritionistId = nutritionistId;
    this.name = name;
    this.goal = goal;
    this.items = new ArrayList<>(items);
    this.cheatMeal = cheatMeal;
  }

  public UUID getId() { return id; }
  public UUID getStudentId() { return studentId; }
  public UUID getNutritionistId() { return nutritionistId; }
  public String getName() { return name; }
  public String getGoal() { return goal; }
  public boolean isCheatMeal() { return cheatMeal; }
  public List<MealItem> getItems() { return Collections.unmodifiableList(items); }
}
