package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RecordMeasurementRequest(
    @NotNull UUID studentId,
    Double weight,
    Double bodyFatPercentage,
    Double rightBicep,
    Double leftBicep,
    Double chest,
    Double waist,
    Double abdomen,
    Double hips,
    Double leftThigh,
    Double rightThigh,
    Double rightCalf,
    Double leftCalf) {

}
