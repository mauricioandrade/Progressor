package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDate;
import java.util.UUID;

public record StudentSummaryResponse(UUID id, String name, String email, LocalDate lastCheckIn) {

}
