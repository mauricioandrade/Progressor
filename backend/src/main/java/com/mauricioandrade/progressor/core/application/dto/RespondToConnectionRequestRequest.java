package com.mauricioandrade.progressor.core.application.dto;

import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public record RespondToConnectionRequestRequest(
    @NotNull UUID requestId,
    @NotNull boolean accepted) {

}
