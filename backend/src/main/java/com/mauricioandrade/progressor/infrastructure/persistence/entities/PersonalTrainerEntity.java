package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "personal_trainers")
public class PersonalTrainerEntity extends UserEntity {

  private String cref;

  public PersonalTrainerEntity() {
  }

  public String getCref() {
    return cref;
  }

  public void setCref(String cref) {
    this.cref = cref;
  }
}