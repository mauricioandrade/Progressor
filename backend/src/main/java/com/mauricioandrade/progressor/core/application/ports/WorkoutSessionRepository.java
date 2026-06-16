package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.application.dto.WorkoutSessionResponse;
import java.util.List;
import java.util.UUID;

public interface WorkoutSessionRepository {

  void save(UUID studentId, int exercises, int sets, double tonnageKg, int prCount);

  List<WorkoutSessionResponse> findRecent(UUID studentId, int limit);
}
