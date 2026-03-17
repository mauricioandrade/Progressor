package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.ProgressReportGenerator;
import java.util.Locale;
import java.util.UUID;

public class GenerateProgressReportUseCase {

  private final ProgressReportGenerator reportGenerator;

  public GenerateProgressReportUseCase(ProgressReportGenerator reportGenerator) {
    this.reportGenerator = reportGenerator;
  }

  public byte[] execute(UUID studentId, Locale locale) {
    return reportGenerator.generate(studentId, locale);
  }
}
