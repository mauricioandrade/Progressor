package com.mauricioandrade.progressor.core.domain.user;

import com.mauricioandrade.progressor.core.domain.common.Email;
import java.time.LocalDate;
import java.util.UUID;

public class Student extends User {

  private UUID personalTrainerId;
  private UUID nutritionistId;

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
}