package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "meal_consumption_logs")
public class MealConsumptionLogEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "meal_plan_item_id")
  private UUID mealPlanItemId;

  // Extra food fields (null for plan items, populated for free items)
  @Column(name = "food_name")
  private String foodName;

  @Column(name = "calories_kcal")
  private Double caloriesKcal;

  @Column(name = "protein_g")
  private Double proteinG;

  @Column(name = "carbs_g")
  private Double carbsG;

  @Column(name = "fat_g")
  private Double fatG;

  @Column(name = "quantity")
  private Double quantity;

  @Column(name = "base_unit")
  private String baseUnit;

  @Column(name = "log_date", nullable = false)
  private LocalDate logDate;

  @Column(name = "consumed_at", nullable = false)
  private LocalDateTime consumedAt = LocalDateTime.now();

  public MealConsumptionLogEntity() {}

  public MealConsumptionLogEntity(UUID studentId, UUID mealPlanItemId, LocalDate logDate) {
    this.studentId = studentId;
    this.mealPlanItemId = mealPlanItemId;
    this.logDate = logDate;
  }

  public MealConsumptionLogEntity(UUID studentId, LocalDate logDate, String foodName,
      Double caloriesKcal, Double proteinG, Double carbsG, Double fatG,
      Double quantity, String baseUnit) {
    this.studentId = studentId;
    this.logDate = logDate;
    this.foodName = foodName;
    this.caloriesKcal = caloriesKcal;
    this.proteinG = proteinG;
    this.carbsG = carbsG;
    this.fatG = fatG;
    this.quantity = quantity;
    this.baseUnit = baseUnit;
  }

  public UUID getId() { return id; }
  public UUID getStudentId() { return studentId; }
  public UUID getMealPlanItemId() { return mealPlanItemId; }
  public LocalDate getLogDate() { return logDate; }
  public String getFoodName() { return foodName; }
  public Double getCaloriesKcal() { return caloriesKcal; }
  public Double getProteinG() { return proteinG; }
  public Double getCarbsG() { return carbsG; }
  public Double getFatG() { return fatG; }
  public Double getQuantity() { return quantity; }
  public String getBaseUnit() { return baseUnit; }
}
