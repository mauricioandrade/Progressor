package com.mauricioandrade.progressor.core.domain.workout;

import java.util.UUID;

public class WorkoutExercise {

  private final UUID id;
  private String name;
  private Integer sets;
  private Integer repetitions;
  private MeasurementType measurementType;
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

  public WorkoutExercise(UUID id, String name, Integer sets, Integer repetitions,
      MeasurementType measurementType) {
    if (sets == null || sets <= 0) {
      throw new IllegalArgumentException("Sets must be greater than zero");
    }
    if (repetitions == null || repetitions <= 0) {
      throw new IllegalArgumentException("Repetitions must be greater than zero");
    }
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Name is required");
    }
    if (measurementType == null) {
      throw new IllegalArgumentException("Measurement type is required");
    }

    this.id = id != null ? id : UUID.randomUUID();
    this.name = name;
    this.sets = sets;
    this.repetitions = repetitions;
    this.measurementType = measurementType;
    this.tonnage = 0.0;
  }

  public void applyWeight(Double weightInKg) {
    if (this.measurementType != MeasurementType.WEIGHT
        && this.measurementType != MeasurementType.BODYWEIGHT) {
      throw new IllegalStateException("Cannot apply weight to this measurement type");
    }
    this.weightInKg = weightInKg;
    calculateTonnage();
  }

  public void applySpeed(Double speed) {
    if (this.measurementType != MeasurementType.SPEED) {
      throw new IllegalStateException("Cannot apply speed to this measurement type");
    }
    this.speed = speed;
  }

  public void applyTime(Integer timeInSeconds) {
    if (this.measurementType != MeasurementType.TIME
        && this.measurementType != MeasurementType.SPEED) {
      throw new IllegalStateException("Cannot apply time to this measurement type");
    }
    if (timeInSeconds <= 0) {
      throw new IllegalArgumentException("Time must be positive");
    }
    this.timeInSeconds = timeInSeconds;
  }

  public void applyCadence(String cadence) {
    this.cadence = cadence;
  }

  public void applyVideoUrl(String videoUrl) {
    this.videoUrl = videoUrl;
  }

  public void applyRestTime(Integer restTime) {
    this.restTime = restTime;
  }

  private void calculateTonnage() {
    if (this.weightInKg != null) {
      this.tonnage = this.sets * this.repetitions * this.weightInKg;
    }
  }

  public UUID getId() {
    return id;
  }

  public String getName() {
    return name;
  }

  public Integer getSets() {
    return sets;
  }

  public Integer getRepetitions() {
    return repetitions;
  }

  public MeasurementType getMeasurementType() {
    return measurementType;
  }

  public Double getWeightInKg() {
    return weightInKg;
  }

  public Double getSpeed() {
    return speed;
  }

  public Integer getTimeInSeconds() {
    return timeInSeconds;
  }

  public String getCadence() {
    return cadence;
  }

  public Double getTonnage() {
    return tonnage;
  }

  public String getVideoUrl() {
    return videoUrl;
  }

  public Integer getRestTime() {
    return restTime;
  }

  public void applySchedule(String workoutLabel, String scheduledDays) {
    this.workoutLabel = workoutLabel;
    this.scheduledDays = scheduledDays;
  }

  public void assignToBlock(UUID blockId) {
    this.blockId = blockId;
  }

  public String getWorkoutLabel() {
    return workoutLabel;
  }

  public String getScheduledDays() {
    return scheduledDays;
  }

  public UUID getBlockId() {
    return blockId;
  }
}