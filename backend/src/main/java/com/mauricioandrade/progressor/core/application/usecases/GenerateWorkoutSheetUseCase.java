package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.WorkoutReportGenerator;
import java.util.Locale;
import java.util.UUID;

public class GenerateWorkoutSheetUseCase {

  private final WorkoutReportGenerator reportGenerator;

  public GenerateWorkoutSheetUseCase(WorkoutReportGenerator reportGenerator) {
    this.reportGenerator = reportGenerator;
  }

  public byte[] execute(UUID studentId, Locale locale) {
    return reportGenerator.generateWorkoutSheet(studentId, locale);
  }
}
