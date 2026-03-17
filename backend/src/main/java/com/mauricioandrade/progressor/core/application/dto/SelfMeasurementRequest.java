package com.mauricioandrade.progressor.core.application.dto;

public record SelfMeasurementRequest(
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
