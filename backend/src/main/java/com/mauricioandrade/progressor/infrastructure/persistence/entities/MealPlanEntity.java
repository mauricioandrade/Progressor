package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "meal_plans")
public class MealPlanEntity {

  @Id
  private UUID id;
  private UUID studentId;
  private UUID nutritionistId;
  private String name;
  private String goal;
  private boolean cheatMeal = false;

  @Column(name = "created_at")
  private LocalDateTime createdAt;

  @OneToMany(mappedBy = "mealPlan", cascade = CascadeType.ALL, fetch = FetchType.EAGER, orphanRemoval = true)
  private List<MealItemEntity> items = new ArrayList<>();

  public MealPlanEntity() {}

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getStudentId() { return studentId; }
  public void setStudentId(UUID studentId) { this.studentId = studentId; }
  public UUID getNutritionistId() { return nutritionistId; }
  public void setNutritionistId(UUID nutritionistId) { this.nutritionistId = nutritionistId; }
  public String getName() { return name; }
  public void setName(String name) { this.name = name; }
  public String getGoal() { return goal; }
  public void setGoal(String goal) { this.goal = goal; }
  public boolean isCheatMeal() { return cheatMeal; }
  public void setCheatMeal(boolean cheatMeal) { this.cheatMeal = cheatMeal; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
  public List<MealItemEntity> getItems() { return items; }
  public void setItems(List<MealItemEntity> items) { this.items = items; }
}
