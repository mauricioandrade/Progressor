package com.mauricioandrade.progressor.core.domain.measurement;

import java.time.LocalDate;
import java.util.UUID;

public class BodyMeasurement {

  private final UUID id;
  private final UUID studentId;
  private final LocalDate recordedAt;

  private Double rightBicep;
  private Double leftBicep;
  private Double chest;
  private Double waist;
  private Double abdomen;
  private Double hips;
  private Double leftThigh;
  private Double rightThigh;
  private Double rightCalf;
  private Double leftCalf;

  public BodyMeasurement(UUID id, UUID studentId, LocalDate recordedAt) {
    if (studentId == null) {
      throw new IllegalArgumentException("Student ID is required");
    }

    this.id = id != null ? id : UUID.randomUUID();
    this.studentId = studentId;
    this.recordedAt = recordedAt != null ? recordedAt : LocalDate.now();
  }

  public void updateMeasurements(Double rightBicep, Double leftBicep, Double chest, Double waist,
      Double abdomen, Double hips, Double leftThigh, Double rightThigh, Double rightCalf,
      Double leftCalf) {

    this.rightBicep = rightBicep;
    this.leftBicep = leftBicep;
    this.chest = chest;
    this.waist = waist;
    this.abdomen = abdomen;
    this.hips = hips;
    this.leftThigh = leftThigh;
    this.rightThigh = rightThigh;
    this.rightCalf = rightCalf;
    this.leftCalf = leftCalf;
  }

  public UUID getId() {
    return id;
  }

  public UUID getStudentId() {
    return studentId;
  }

  public LocalDate getRecordedAt() {
    return recordedAt;
  }

  public Double getRightBicep() {
    return rightBicep;
  }

  public Double getLeftBicep() {
    return leftBicep;
  }

  public Double getChest() {
    return chest;
  }

  public Double getWaist() {
    return waist;
  }

  public Double getAbdomen() {
    return abdomen;
  }

  public Double getHips() {
    return hips;
  }

  public Double getLeftThigh() {
    return leftThigh;
  }

  public Double getRightThigh() {
    return rightThigh;
  }

  public Double getRightCalf() {
    return rightCalf;
  }

  public Double getLeftCalf() {
    return leftCalf;
  }
}