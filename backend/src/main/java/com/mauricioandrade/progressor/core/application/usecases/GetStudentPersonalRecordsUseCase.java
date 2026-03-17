package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ExerciseLogResponse;
import com.mauricioandrade.progressor.core.application.ports.WorkoutLogRepository;
import java.util.List;
import java.util.UUID;

public class GetStudentPersonalRecordsUseCase {

  private final WorkoutLogRepository logRepository;

  public GetStudentPersonalRecordsUseCase(WorkoutLogRepository logRepository) {
    this.logRepository = logRepository;
  }

  public List<ExerciseLogResponse> execute(UUID studentId) {
    return logRepository.findLatestPrsByStudentId(studentId, 3).stream()
        .map(log -> new ExerciseLogResponse(log.getId(), log.getExerciseId(), log.getExerciseName(),
            log.getActualWeight(), log.getActualReps(), log.getCompletedAt(),
            log.getTonnageAchieved(), log.isPersonalRecord()))
        .toList();
  }
}
