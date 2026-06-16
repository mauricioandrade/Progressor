package com.mauricioandrade.progressor.core.domain.workout;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public class WorkoutFeedback {

  private final UUID id;
  private final UUID studentId;
  private final UUID trainerId;
  private final int rating;
  private final String comment;
  private final LocalDate feedbackDate;
  private final LocalDateTime createdAt;

  public WorkoutFeedback(UUID id, UUID studentId, UUID trainerId, int rating, String comment,
      LocalDate feedbackDate, LocalDateTime createdAt) {
    if (rating < 1 || rating > 5) throw new IllegalArgumentException("Rating must be 1-5");
    this.id = id;
    this.studentId = studentId;
    this.trainerId = trainerId;
    this.rating = rating;
    this.comment = comment;
    this.feedbackDate = feedbackDate;
    this.createdAt = createdAt;
  }

  public UUID getId() { return id; }
  public UUID getStudentId() { return studentId; }
  public UUID getTrainerId() { return trainerId; }
  public int getRating() { return rating; }
  public String getComment() { return comment; }
  public LocalDate getFeedbackDate() { return feedbackDate; }
  public LocalDateTime getCreatedAt() { return createdAt; }
}
