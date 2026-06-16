package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

public record WorkoutFeedbackResponse(
    UUID id,
    UUID studentId,
    int rating,
    String comment,
    LocalDate feedbackDate,
    LocalDateTime createdAt
) {}
