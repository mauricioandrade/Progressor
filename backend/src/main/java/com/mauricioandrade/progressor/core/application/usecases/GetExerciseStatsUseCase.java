package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ExerciseStatsResponse;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutLogRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class GetExerciseStatsUseCase {

  private final SpringDataWorkoutLogRepository workoutLogRepository;

  public GetExerciseStatsUseCase(SpringDataWorkoutLogRepository workoutLogRepository) {
    this.workoutLogRepository = workoutLogRepository;
  }

  public List<ExerciseStatsResponse> execute(UUID studentId) {
    List<Object[]> lastSessions = workoutLogRepository.findLastSessionPerExercise(studentId);
    List<Object[]> prs = workoutLogRepository.findPrPerExercise(studentId);

    Map<UUID, Double[]> prMap = new HashMap<>();
    for (Object[] row : prs) {
      UUID exerciseId = (UUID) row[0];
      if (!prMap.containsKey(exerciseId)) {
        Double prWeight = row[1] != null ? ((Number) row[1]).doubleValue() : null;
        Integer prReps  = row[2] != null ? ((Number) row[2]).intValue()    : null;
        prMap.put(exerciseId, new Double[]{ prWeight, prReps != null ? prReps.doubleValue() : null });
      }
    }

    return lastSessions.stream().map(row -> {
      UUID exerciseId = (UUID) row[0];
      Double lastWeight = row[1] != null ? ((Number) row[1]).doubleValue() : null;
      Integer lastReps  = row[2] != null ? ((Number) row[2]).intValue()    : null;
      Double[] pr = prMap.getOrDefault(exerciseId, new Double[]{ null, null });
      Integer prReps = pr[1] != null ? pr[1].intValue() : null;
      return new ExerciseStatsResponse(exerciseId, lastWeight, lastReps, pr[0], prReps);
    }).toList();
  }
}
