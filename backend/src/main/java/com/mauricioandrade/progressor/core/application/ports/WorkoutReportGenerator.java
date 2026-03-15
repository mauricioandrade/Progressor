package com.mauricioandrade.progressor.core.application.ports;

import java.util.UUID;

public interface WorkoutReportGenerator {

  byte[] generateWorkoutSheet(UUID studentId);
}