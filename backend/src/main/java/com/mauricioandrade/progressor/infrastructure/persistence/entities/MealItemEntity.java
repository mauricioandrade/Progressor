package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "meal_items")
public class MealItemEntity {

  @Id
  private UUID id;
  private String mealTime;
  private String name;
  private String foodDescription;
  private Double caloriesKcal;
  private Double proteinG;
  private Double carbsG;
  private Double fatG;
  private Double quantity;
  private String baseUnit;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "meal_plan_id")
  private MealPlanEntity mealPlan;

  public MealItemEntity() {}

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public String getMealTime() { return mealTime; }
  public void setMealTime(String mealTime) { this.mealTime = mealTime; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getFoodDescription() { return foodDescription; }
  public void setFoodDescription(String foodDescription) { this.foodDescription = foodDescription; }
  public Double getCaloriesKcal() { return caloriesKcal; }
  public void setCaloriesKcal(Double caloriesKcal) { this.caloriesKcal = caloriesKcal; }
  public Double getProteinG() { return proteinG; }
  public void setProteinG(Double proteinG) { this.proteinG = proteinG; }
  public Double getCarbsG() { return carbsG; }
  public void setCarbsG(Double carbsG) { this.carbsG = carbsG; }
  public Double getFatG() { return fatG; }
  public void setFatG(Double fatG) { this.fatG = fatG; }
  public Double getQuantity() { return quantity; }
  public void setQuantity(Double quantity) { this.quantity = quantity; }
  public String getBaseUnit() { return baseUnit; }
  public void setBaseUnit(String baseUnit) { this.baseUnit = baseUnit; }
  public MealPlanEntity getMealPlan() { return mealPlan; }
  public void setMealPlan(MealPlanEntity mealPlan) { this.mealPlan = mealPlan; }
}
