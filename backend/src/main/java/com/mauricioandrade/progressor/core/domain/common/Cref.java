package com.mauricioandrade.progressor.core.domain.common;

public record Cref(String value) {

  public Cref {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("CREF cannot be empty");
    }
  }
}