package com.mauricioandrade.progressor.core.application.dto;

public record CreateWorkoutExerciseRequest(String name, Integer sets, Integer repetitions,
                                           String measurementType, Double weightInKg, Double speed,
                                           Integer timeInSeconds, String cadence) {

}