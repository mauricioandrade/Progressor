package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "students")
public class StudentEntity extends UserEntity {

  private UUID personalTrainerId;
  private UUID nutritionistId;

  @Column(name = "daily_water_goal")
  private Integer dailyWaterGoal = 0;

  @Column(name = "current_water_intake")
  private Integer currentWaterIntake = 0;

  public StudentEntity() {
  }

  public UUID getPersonalTrainerId() {
    return personalTrainerId;
  }

  public void setPersonalTrainerId(UUID personalTrainerId) {
    this.personalTrainerId = personalTrainerId;
  }

  public UUID getNutritionistId() {
    return nutritionistId;
  }

  public void setNutritionistId(UUID nutritionistId) {
    this.nutritionistId = nutritionistId;
  }

  public Integer getDailyWaterGoal() {
    return dailyWaterGoal;
  }

  public void setDailyWaterGoal(Integer dailyWaterGoal) {
    this.dailyWaterGoal = dailyWaterGoal;
  }

  public Integer getCurrentWaterIntake() {
    return currentWaterIntake;
  }

  public void setCurrentWaterIntake(Integer currentWaterIntake) {
    this.currentWaterIntake = currentWaterIntake;
  }
}