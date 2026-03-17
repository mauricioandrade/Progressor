package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record ProgressPhotoResponse(
    UUID id,
    LocalDate takenAt,
    String description,
    String photoBase64,
    String professionalFeedback,
    UUID professionalId,
    String professionalName,
    String professionalRole,
    String studentNotes) {

}
