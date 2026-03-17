package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.MealPlanReportGenerator;
import java.util.Locale;
import java.util.UUID;

public class GenerateMealPlanReportUseCase {

  private final MealPlanReportGenerator reportGenerator;

  public GenerateMealPlanReportUseCase(MealPlanReportGenerator reportGenerator) {
    this.reportGenerator = reportGenerator;
  }

  public byte[] execute(UUID studentId, Locale locale) {
    return reportGenerator.generate(studentId, locale);
  }
}
