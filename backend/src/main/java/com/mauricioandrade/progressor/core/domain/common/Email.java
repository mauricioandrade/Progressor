package com.mauricioandrade.progressor.core.domain.common;

public record Email(String value) {

  public Email {
    if (value == null || !value.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
      throw new IllegalArgumentException("Invalid email format");
    }
  }
}