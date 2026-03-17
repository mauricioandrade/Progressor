package com.mauricioandrade.progressor.core.domain.checkin;

import java.time.LocalDate;
import java.util.UUID;

public class WorkoutCheckIn {

  private final UUID id;
  private final UUID studentId;
  private final LocalDate date;

  public WorkoutCheckIn(UUID id, UUID studentId, LocalDate date) {
    if (studentId == null) {
      throw new IllegalArgumentException("studentId is required");
    }
    if (date == null) {
      throw new IllegalArgumentException("date is required");
    }
    this.id = id != null ? id : UUID.randomUUID();
    this.studentId = studentId;
    this.date = date;
  }

  public UUID getId() {
    return id;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public LocalDate getDate() {
    return date;
  }
}
