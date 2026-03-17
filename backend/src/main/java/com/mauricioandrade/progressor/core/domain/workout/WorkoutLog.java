package com.mauricioandrade.progressor.core.domain.workout;

import java.time.LocalDateTime;
import java.util.UUID;

public class WorkoutLog {

  private final UUID id;
  private final UUID studentId;
  private final UUID exerciseId;
  private final String exerciseName;
  private final Double actualWeight;
  private final Integer actualReps;
  private final LocalDateTime completedAt;
  private final Double tonnageAchieved;
  private final boolean personalRecord;

  public WorkoutLog(UUID id, UUID studentId, UUID exerciseId, String exerciseName,
      Double actualWeight, Integer actualReps, LocalDateTime completedAt, Double tonnageAchieved,
      boolean personalRecord) {
    if (studentId == null) {
      throw new IllegalArgumentException("Student ID is required");
    }
    if (exerciseId == null) {
      throw new IllegalArgumentException("Exercise ID is required");
    }
    if (actualReps == null || actualReps <= 0) {
      throw new IllegalArgumentException("Actual reps must be positive");
    }
    this.id = id != null ? id : UUID.randomUUID();
    this.studentId = studentId;
    this.exerciseId = exerciseId;
    this.exerciseName = exerciseName;
    this.actualWeight = actualWeight;
    this.actualReps = actualReps;
    this.completedAt = completedAt != null ? completedAt : LocalDateTime.now();
    this.tonnageAchieved = tonnageAchieved != null ? tonnageAchieved : 0.0;
    this.personalRecord = personalRecord;
  }

  public UUID getId() { return id; }
  public UUID getStudentId() { return studentId; }
  public UUID getExerciseId() { return exerciseId; }
  public String getExerciseName() { return exerciseName; }
  public Double getActualWeight() { return actualWeight; }
  public Integer getActualReps() { return actualReps; }
  public LocalDateTime getCompletedAt() { return completedAt; }
  public Double getTonnageAchieved() { return tonnageAchieved; }
  public boolean isPersonalRecord() { return personalRecord; }
}
