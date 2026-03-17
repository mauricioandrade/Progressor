package com.mauricioandrade.progressor.core.domain.user;

import com.mauricioandrade.progressor.core.domain.common.Email;
import java.time.LocalDate;
import java.util.UUID;

public class Student extends User {

  private UUID personalTrainerId;
  private UUID nutritionistId;
  private int dailyWaterGoal;
  private int currentWaterIntake;

  public Student(UUID id, String firstName, String lastName, Email email, String password,
      LocalDate birthDate) {
    super(id, firstName, lastName, email, password, birthDate);
  }

  public void assignPersonalTrainer(UUID personalTrainerId) {
    this.personalTrainerId = personalTrainerId;
  }

  public void assignNutritionist(UUID nutritionistId) {
    this.nutritionistId = nutritionistId;
  }

  public UUID getPersonalTrainerId() {
    return personalTrainerId;
  }

  public UUID getNutritionistId() {
    return nutritionistId;
  }

  public int getDailyWaterGoal() {
    return dailyWaterGoal;
  }

  public void setDailyWaterGoal(int dailyWaterGoal) {
    this.dailyWaterGoal = dailyWaterGoal;
  }

  public int getCurrentWaterIntake() {
    return currentWaterIntake;
  }

  public void setCurrentWaterIntake(int currentWaterIntake) {
    this.currentWaterIntake = currentWaterIntake;
  }

  public void addWaterIntake(int amount) {
    this.currentWaterIntake += amount;
  }
}