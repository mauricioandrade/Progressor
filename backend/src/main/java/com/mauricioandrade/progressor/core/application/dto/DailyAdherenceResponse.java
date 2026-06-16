package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDate;

public record DailyAdherenceResponse(LocalDate date, int totalItems, int consumedItems) {

  public double adherencePct() {
    return totalItems > 0 ? (consumedItems * 100.0) / totalItems : 0;
  }
}
