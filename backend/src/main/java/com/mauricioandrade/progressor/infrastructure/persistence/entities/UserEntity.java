package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Inheritance;
import jakarta.persistence.InheritanceType;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "app_users")
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class UserEntity {

  @Id
  private UUID id;

  private String firstName;
  private String lastName;

  private String email;

  private String password;
  private LocalDate birthDate;

  @Basic(fetch = FetchType.LAZY)
  @Column(columnDefinition = "bytea")
  private byte[] avatar;

  private String resetToken;
  private LocalDateTime resetTokenExpiry;

  private String pushToken;

  public UserEntity() {
  }

  public abstract String getRole();

  public String getPassword() {
    return this.password;
  }

  public UUID getId() {
    return id;
  }

  public void setId(UUID id) {
    this.id = id;
  }

  public String getFirstName() {
    return firstName;
  }

  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public void setPassword(String password) {
    this.password = password;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public void setBirthDate(LocalDate birthDate) {
    this.birthDate = birthDate;
  }

  public byte[] getAvatar() {
    return avatar;
  }

  public void setAvatar(byte[] avatar) {
    this.avatar = avatar;
  }

  public String getResetToken() { return resetToken; }
  public void setResetToken(String resetToken) { this.resetToken = resetToken; }
  public LocalDateTime getResetTokenExpiry() { return resetTokenExpiry; }
  public void setResetTokenExpiry(LocalDateTime resetTokenExpiry) { this.resetTokenExpiry = resetTokenExpiry; }
  public String getPushToken() { return pushToken; }
  public void setPushToken(String pushToken) { this.pushToken = pushToken; }
}