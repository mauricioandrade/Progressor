package com.mauricioandrade.progressor.core.application.dto;

import java.util.UUID;

public record RecordMeasurementRequest(UUID studentId, Double rightBicep, Double leftBicep,
                                       Double chest, Double waist, Double abdomen, Double hips,
                                       Double leftThigh, Double rightThigh, Double rightCalf,
                                       Double leftCalf) {

}