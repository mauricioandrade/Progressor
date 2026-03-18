package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record WorkoutPlanResponse(
    UUID id,
    UUID studentId,
    UUID trainerId,
    String name,
    LocalDateTime createdAt,
    List<WorkoutBlockResponse> blocks) {

}
