package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProfessionalDashboardStudentSummary(
    UUID studentId,
    String studentName,
    String studentEmail,
    LocalDate lastCheckIn,
    Integer checkInsLast7Days,
    Integer lastFeedbackRating,
    String lastFeedbackComment,
    LocalDate lastFeedbackDate,
    LocalDate lastFoodLogDate,
    Integer todayAdherencePct
) {}
