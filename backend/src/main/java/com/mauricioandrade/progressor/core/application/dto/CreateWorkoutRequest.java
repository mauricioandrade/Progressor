package com.mauricioandrade.progressor.core.application.dto;

import java.util.List;
import java.util.UUID;

public record CreateWorkoutRequest(UUID studentId, List<CreateWorkoutExerciseRequest> exercises) {

}