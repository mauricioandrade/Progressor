package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.util.UUID;

@Entity
@Table(name = "workout_exercises")
public class WorkoutExerciseEntity {

  @Id
  private UUID id;

  private UUID studentId;
  private String name;
  private Integer sets;
  private Integer repetitions;
  private String measurementType;
  private Double weightInKg;
  private Double speed;
  private Integer timeInSeconds;
  private String cadence;
  private Double tonnage;
  private String videoUrl;
  private Integer restTime;
  private String workoutLabel;
  private String scheduledDays;
  private UUID blockId;

  public WorkoutExerciseEntity() {
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

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public Integer getSets() {
    return sets;
  }

  public void setSets(Integer sets) {
    this.sets = sets;
  }

  public Integer getRepetitions() {
    return repetitions;
  }

  public void setRepetitions(Integer repetitions) {
    this.repetitions = repetitions;
  }

  public String getMeasurementType() {
    return measurementType;
  }

  public void setMeasurementType(String measurementType) {
    this.measurementType = measurementType;
  }

  public Double getWeightInKg() {
    return weightInKg;
  }

  public void setWeightInKg(Double weightInKg) {
    this.weightInKg = weightInKg;
  }

  public Double getSpeed() {
    return speed;
  }

  public void setSpeed(Double speed) {
    this.speed = speed;
  }

  public Integer getTimeInSeconds() {
    return timeInSeconds;
  }

  public void setTimeInSeconds(Integer timeInSeconds) {
    this.timeInSeconds = timeInSeconds;
  }

  public String getCadence() {
    return cadence;
  }

  public void setCadence(String cadence) {
    this.cadence = cadence;
  }

  public Double getTonnage() {
    return tonnage;
  }

  public void setTonnage(Double tonnage) {
    this.tonnage = tonnage;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public void setVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public Integer getRestTime() {
    return restTime;
  }

  public void setRestTime(Integer restTime) {
    this.restTime = restTime;
  }

  public String getWorkoutLabel() {
    return workoutLabel;
  }

  public void setWorkoutLabel(String workoutLabel) {
    this.workoutLabel = workoutLabel;
  }

  public String getScheduledDays() {
    return scheduledDays;
  }

  public void setScheduledDays(String scheduledDays) {
    this.scheduledDays = scheduledDays;
  }

  public UUID getBlockId() {
    return blockId;
  }

  public void setBlockId(UUID blockId) {
    this.blockId = blockId;
  }
}