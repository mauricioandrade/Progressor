package com.mauricioandrade.progressor.core.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ConversationSummaryResponse(
    UUID partnerId,
    String partnerName,
    String lastMessageContent,
    Instant lastMessageAt,
    int unreadCount
) {}
