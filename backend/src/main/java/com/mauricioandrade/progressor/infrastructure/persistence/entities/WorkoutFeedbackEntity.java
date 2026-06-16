package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "workout_feedbacks")
public class WorkoutFeedbackEntity {

  @Id
  private UUID id;

  @Column(name = "student_id", nullable = false)
  private UUID studentId;

  @Column(name = "trainer_id")
  private UUID trainerId;

  @Column(nullable = false)
  private int rating;

  @Column(columnDefinition = "TEXT")
  private String comment;

  @Column(name = "feedback_date", nullable = false)
  private LocalDate feedbackDate;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getStudentId() { return studentId; }
  public void setStudentId(UUID studentId) { this.studentId = studentId; }
  public UUID getTrainerId() { return trainerId; }
  public void setTrainerId(UUID trainerId) { this.trainerId = trainerId; }
  public int getRating() { return rating; }
  public void setRating(int rating) { this.rating = rating; }
  public String getComment() { return comment; }
  public void setComment(String comment) { this.comment = comment; }
  public LocalDate getFeedbackDate() { return feedbackDate; }
  public void setFeedbackDate(LocalDate feedbackDate) { this.feedbackDate = feedbackDate; }
  public LocalDateTime getCreatedAt() { return createdAt; }
  public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
