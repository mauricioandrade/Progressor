package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ExerciseLogResponse;
import com.mauricioandrade.progressor.core.application.dto.LogExerciseRequest;
import com.mauricioandrade.progressor.core.application.ports.WorkoutLogRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutLog;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class LogExerciseExecutionUseCase {

  private final WorkoutLogRepository logRepository;

  public LogExerciseExecutionUseCase(WorkoutLogRepository logRepository) {
    this.logRepository = logRepository;
  }

  public ExerciseLogResponse execute(LogExerciseRequest request, UUID studentId) {
    double tonnage = request.actualWeight() != null
        ? request.actualReps() * request.actualWeight()
        : 0.0;

    List<WorkoutLog> previousLogs = logRepository.findByStudentIdAndExerciseId(studentId,
        request.exerciseId());

    double maxPreviousTonnage = previousLogs.stream()
        .mapToDouble(WorkoutLog::getTonnageAchieved)
        .max()
        .orElse(-1.0);

    boolean isPr = tonnage > maxPreviousTonnage;

    WorkoutLog log = new WorkoutLog(null, studentId, request.exerciseId(), request.exerciseName(),
        request.actualWeight(), request.actualReps(), LocalDateTime.now(), tonnage, isPr);

    logRepository.save(log);

    return new ExerciseLogResponse(log.getId(), log.getExerciseId(), log.getExerciseName(),
        log.getActualWeight(), log.getActualReps(), log.getCompletedAt(), log.getTonnageAchieved(),
        log.isPersonalRecord());
  }
}
