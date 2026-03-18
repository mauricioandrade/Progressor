package com.mauricioandrade.progressor.core.domain.workout;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkoutPlan {

  private final UUID id;
  private final UUID studentId;
  private final UUID trainerId;
  private String name;
  private final LocalDateTime createdAt;

  public WorkoutPlan(UUID id, UUID studentId, UUID trainerId, String name) {
    if (studentId == null) {
      throw new IllegalArgumentException("Student ID is required");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Plan name is required");
    }
    this.id = id != null ? id : UUID.randomUUID();
    this.studentId = studentId;
    this.trainerId = trainerId;
    this.name = name;
    this.createdAt = LocalDateTime.now();
  }

  // Reconstruction constructor (from persistence)
  public WorkoutPlan(UUID id, UUID studentId, UUID trainerId, String name,
      LocalDateTime createdAt) {
    this.id = id;
    this.studentId = studentId;
    this.trainerId = trainerId;
    this.name = name;
    this.createdAt = createdAt;
  }

  public UUID getId() {
    return id;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public UUID getTrainerId() {
    return trainerId;
  }

  public String getName() {
    return name;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }
}
