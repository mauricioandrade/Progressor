package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.UUID;

@Entity
@Table(name = "body_measurements")
public class BodyMeasurementEntity {

  @Id
  private UUID id;

  private UUID studentId;
  private LocalDate recordedAt;

  private Double weight;
  private Double bodyFatPercentage;
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

  public BodyMeasurementEntity() {
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

  public LocalDate getRecordedAt() {
    return recordedAt;
  }

  public void setRecordedAt(LocalDate recordedAt) {
    this.recordedAt = recordedAt;
  }

  public Double getWeight() {
    return weight;
  }

  public void setWeight(Double weight) {
    this.weight = weight;
  }

  public Double getBodyFatPercentage() {
    return bodyFatPercentage;
  }

  public void setBodyFatPercentage(Double bodyFatPercentage) {
    this.bodyFatPercentage = bodyFatPercentage;
  }

  public Double getRightBicep() {
    return rightBicep;
  }

  public void setRightBicep(Double rightBicep) {
    this.rightBicep = rightBicep;
  }

  public Double getLeftBicep() {
    return leftBicep;
  }

  public void setLeftBicep(Double leftBicep) {
    this.leftBicep = leftBicep;
  }

  public Double getChest() {
    return chest;
  }

  public void setChest(Double chest) {
    this.chest = chest;
  }

  public Double getWaist() {
    return waist;
  }

  public void setWaist(Double waist) {
    this.waist = waist;
  }

  public Double getAbdomen() {
    return abdomen;
  }

  public void setAbdomen(Double abdomen) {
    this.abdomen = abdomen;
  }

  public Double getHips() {
    return hips;
  }

  public void setHips(Double hips) {
    this.hips = hips;
  }

  public Double getLeftThigh() {
    return leftThigh;
  }

  public void setLeftThigh(Double leftThigh) {
    this.leftThigh = leftThigh;
  }

  public Double getRightThigh() {
    return rightThigh;
  }

  public void setRightThigh(Double rightThigh) {
    this.rightThigh = rightThigh;
  }

  public Double getRightCalf() {
    return rightCalf;
  }

  public void setRightCalf(Double rightCalf) {
    this.rightCalf = rightCalf;
  }

  public Double getLeftCalf() {
    return leftCalf;
  }

  public void setLeftCalf(Double leftCalf) {
    this.leftCalf = leftCalf;
  }
}