package com.mauricioandrade.progressor.infrastructure.reports;

public class MealPlanRow {

  private String mealTime;
  private String mealTimeLabel;
  private String foodName;
  private String quantity;
  private String calories;
  private String protein;
  private String carbs;
  private String fat;

  public MealPlanRow(String mealTime, String mealTimeLabel, String foodName,
      String quantity, String calories, String protein, String carbs, String fat) {
    this.mealTime = mealTime;
    this.mealTimeLabel = mealTimeLabel;
    this.foodName = foodName;
    this.quantity = quantity;
    this.calories = calories;
    this.protein = protein;
    this.carbs = carbs;
    this.fat = fat;
  }

  public String getMealTime() { return mealTime; }
  public String getMealTimeLabel() { return mealTimeLabel; }
  public String getFoodName() { return foodName; }
  public String getQuantity() { return quantity; }
  public String getCalories() { return calories; }
  public String getProtein() { return protein; }
  public String getCarbs() { return carbs; }
  public String getFat() { return fat; }
}
