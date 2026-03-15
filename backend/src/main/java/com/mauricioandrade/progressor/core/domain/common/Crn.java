package com.mauricioandrade.progressor.core.domain.common;

public record Crn(String value) {

  public Crn {
    if (value == null || value.trim().isEmpty()) {
      throw new IllegalArgumentException("CRN cannot be empty");
    }
  }

}
