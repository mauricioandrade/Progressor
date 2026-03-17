package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record MeasurementResponse(UUID id, LocalDate recordedAt, Double weight,
    Double bodyFatPercentage, Double rightBicep, Double leftBicep, Double chest, Double waist,
    Double abdomen, Double hips, Double leftThigh, Double rightThigh, Double rightCalf,
    Double leftCalf) {

}
