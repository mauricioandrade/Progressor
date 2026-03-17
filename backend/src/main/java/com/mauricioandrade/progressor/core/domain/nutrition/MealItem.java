package com.mauricioandrade.progressor.core.domain.nutrition;

import java.util.UUID;

public class MealItem {

  private final UUID id;
  private final String mealTime;
  private final String name;
  private final String foodDescription;
  private final Double caloriesKcal;
  private final Double proteinG;
  private final Double carbsG;
  private final Double fatG;
  private final Double quantity;
  private final String baseUnit;

  public MealItem(UUID id, String mealTime, String name, String foodDescription,
      Double caloriesKcal, Double proteinG, Double carbsG, Double fatG,
      Double quantity, String baseUnit) {
    this.id = id;
    this.mealTime = mealTime;
    this.name = name;
    this.foodDescription = foodDescription;
    this.caloriesKcal = caloriesKcal;
    this.proteinG = proteinG;
    this.carbsG = carbsG;
    this.fatG = fatG;
    this.quantity = quantity;
    this.baseUnit = baseUnit;
  }

  public UUID getId() { return id; }
  public String getMealTime() { return mealTime; }
  public String getName() { return name; }
  public String getFoodDescription() { return foodDescription; }
  public Double getCaloriesKcal() { return caloriesKcal; }
  public Double getProteinG() { return proteinG; }
  public Double getCarbsG() { return carbsG; }
  public Double getFatG() { return fatG; }
  public Double getQuantity() { return quantity; }
  public String getBaseUnit() { return baseUnit; }
}
