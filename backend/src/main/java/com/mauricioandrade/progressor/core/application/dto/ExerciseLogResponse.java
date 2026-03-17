package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ExerciseLogResponse(UUID id, UUID exerciseId, String exerciseName,
    Double actualWeight, Integer actualReps, LocalDateTime completedAt, Double tonnageAchieved,
    boolean personalRecord) {

}
