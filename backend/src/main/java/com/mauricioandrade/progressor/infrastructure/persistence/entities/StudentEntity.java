package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "students")
public class StudentEntity extends UserEntity {

  private UUID personalTrainerId;
  private UUID nutritionistId;

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
}