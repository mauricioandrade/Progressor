package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "workout_check_ins",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "date"}))
public class WorkoutCheckInEntity {

  @Id
  private UUID id;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(nullable = false)
  private LocalDate date;

  public WorkoutCheckInEntity() {
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

  public LocalDate getDate() {
    return date;
  }

  public void setDate(LocalDate date) {
    this.date = date;
  }
}
