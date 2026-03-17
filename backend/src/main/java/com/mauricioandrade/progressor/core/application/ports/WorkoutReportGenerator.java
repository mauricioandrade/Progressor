package com.mauricioandrade.progressor.core.application.ports;

import java.util.Locale;
import java.util.UUID;

public interface WorkoutReportGenerator {

  byte[] generateWorkoutSheet(UUID studentId, Locale locale);
}
