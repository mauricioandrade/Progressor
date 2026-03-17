package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workout_logs")
public class WorkoutLogEntity {

  @Id
  private UUID id;
  private UUID studentId;
  private UUID exerciseId;
  private String exerciseName;
  private Double actualWeight;
  private Integer actualReps;
  private LocalDateTime completedAt;
  private Double tonnageAchieved;
  private boolean personalRecord;

  public WorkoutLogEntity() {
  }

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getStudentId() { return studentId; }
  public void setStudentId(UUID studentId) { this.studentId = studentId; }
  public UUID getExerciseId() { return exerciseId; }
  public void setExerciseId(UUID exerciseId) { this.exerciseId = exerciseId; }
  public String getExerciseName() { return exerciseName; }
  public void setExerciseName(String exerciseName) { this.exerciseName = exerciseName; }
  public Double getActualWeight() { return actualWeight; }
  public void setActualWeight(Double actualWeight) { this.actualWeight = actualWeight; }
  public Integer getActualReps() { return actualReps; }
  public void setActualReps(Integer actualReps) { this.actualReps = actualReps; }
  public LocalDateTime getCompletedAt() { return completedAt; }
  public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
  public Double getTonnageAchieved() { return tonnageAchieved; }
  public void setTonnageAchieved(Double tonnageAchieved) { this.tonnageAchieved = tonnageAchieved; }
  public boolean isPersonalRecord() { return personalRecord; }
  public void setPersonalRecord(boolean personalRecord) { this.personalRecord = personalRecord; }
}
