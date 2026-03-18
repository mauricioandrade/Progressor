package com.mauricioandrade.progressor.core.domain.workout;

import java.util.UUID;

public class WorkoutBlock {

  private final UUID id;
  private final UUID workoutPlanId;
  private String name;
  private int position;

  public WorkoutBlock(UUID id, UUID workoutPlanId, String name, int position) {
    if (workoutPlanId == null) {
      throw new IllegalArgumentException("Workout plan ID is required");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Block name is required");
    }
    if (position < 0) {
      throw new IllegalArgumentException("Position must be non-negative");
    }
    this.id = id != null ? id : UUID.randomUUID();
    this.workoutPlanId = workoutPlanId;
    this.name = name;
    this.position = position;
  }

  public UUID getId() {
    return id;
  }

  public UUID getWorkoutPlanId() {
    return workoutPlanId;
  }

  public String getName() {
    return name;
  }

  public int getPosition() {
    return position;
  }
}
