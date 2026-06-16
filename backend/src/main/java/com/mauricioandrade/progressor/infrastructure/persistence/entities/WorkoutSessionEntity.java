package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workout_sessions")
public class WorkoutSessionEntity {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "session_date", nullable = false)
  private LocalDate sessionDate = LocalDate.now();

  @Column(name = "exercises", nullable = false)
  private int exercises;

  @Column(name = "sets_completed", nullable = false)
  private int setsCompleted;

  @Column(name = "tonnage_kg", nullable = false)
  private double tonnageKg;

  @Column(name = "pr_count", nullable = false)
  private int prCount;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt = LocalDateTime.now();

  public WorkoutSessionEntity() {}

  public WorkoutSessionEntity(UUID studentId, int exercises, int setsCompleted,
      double tonnageKg, int prCount) {
    this.studentId = studentId;
    this.exercises = exercises;
    this.setsCompleted = setsCompleted;
    this.tonnageKg = tonnageKg;
    this.prCount = prCount;
  }

  public UUID getId() { return id; }
  public UUID getStudentId() { return studentId; }
  public LocalDate getSessionDate() { return sessionDate; }
  public int getExercises() { return exercises; }
  public int getSetsCompleted() { return setsCompleted; }
  public double getTonnageKg() { return tonnageKg; }
  public int getPrCount() { return prCount; }
  public LocalDateTime getCreatedAt() { return createdAt; }
}
