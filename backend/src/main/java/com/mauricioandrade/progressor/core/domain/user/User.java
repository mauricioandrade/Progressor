package com.mauricioandrade.progressor.core.domain.user;

import com.mauricioandrade.progressor.core.domain.common.Email;
import java.time.LocalDate;
import java.util.UUID;

public abstract class User {

  private final UUID id;
  private String firstName;
  private String lastName;
  private Email email;
  private String password;
  private LocalDate birthDate;
  private byte[] avatar;

  protected User(UUID id, String firstName, String lastName, Email email, String password,
      LocalDate birthDate) {
    if (firstName == null || firstName.isBlank()) {
      throw new IllegalArgumentException("First name is required");
    }
    if (lastName == null || lastName.isBlank()) {
      throw new IllegalArgumentException("Last name is required");
    }
    if (password == null || password.isBlank()) {
      throw new IllegalArgumentException("Password is required");
    }
    if (birthDate == null || birthDate.isAfter(LocalDate.now())) {
      throw new IllegalArgumentException("Invalid birth date");
    }

    this.id = id != null ? id : UUID.randomUUID();
    this.firstName = firstName;
    this.lastName = lastName;
    this.email = email;
    this.password = password;
    this.birthDate = birthDate;
  }

  public void updateAvatar(byte[] avatar) {
    if (avatar == null || avatar.length == 0) {
      throw new IllegalArgumentException("Avatar cannot be empty");
    }
    this.avatar = avatar;
  }

  public UUID getId() {
    return id;
  }

  public String getFirstName() {
    return firstName;
  }

  public String getLastName() {
    return lastName;
  }

  public Email getEmail() {
    return email;
  }

  public String getPassword() {
    return password;
  }

  public LocalDate getBirthDate() {
    return birthDate;
  }

  public byte[] getAvatar() {
    return avatar;
  }
}