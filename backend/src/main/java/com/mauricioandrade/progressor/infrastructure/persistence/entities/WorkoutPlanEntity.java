package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workout_plans")
public class WorkoutPlanEntity {

  @Id
  private UUID id;

  private UUID studentId;
  private UUID trainerId;
  private String name;
  private LocalDateTime createdAt;

  public WorkoutPlanEntity() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public void setStudentId(UUID studentId) {
    this.studentId = studentId;
  }

  public UUID getTrainerId() {
    return trainerId;
  }

  public void setTrainerId(UUID trainerId) {
    this.trainerId = trainerId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }
}
