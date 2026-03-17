package com.mauricioandrade.progressor.core.application.ports;

import java.util.Locale;
import java.util.UUID;

public interface ProgressReportGenerator {

  byte[] generate(UUID studentId, Locale locale);
}
