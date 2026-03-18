package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "workout_blocks")
public class WorkoutBlockEntity {

  @Id
  private UUID id;

  private UUID workoutPlanId;
  private String name;
  private int position;

  public WorkoutBlockEntity() {
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public UUID getWorkoutPlanId() {
    return workoutPlanId;
  }

  public void setWorkoutPlanId(UUID workoutPlanId) {
    this.workoutPlanId = workoutPlanId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getPosition() {
    return position;
  }

  public void setPosition(int position) {
    this.position = position;
  }
}
