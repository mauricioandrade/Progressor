package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record ConnectionRequestResponse(
    UUID id,
    UUID professionalId,
    String professionalName,
    String professionalRole,
    String status,
    LocalDateTime createdAt) {

}
