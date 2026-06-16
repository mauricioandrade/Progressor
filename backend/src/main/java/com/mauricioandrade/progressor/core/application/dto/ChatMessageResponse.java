package com.mauricioandrade.progressor.core.application.dto;

import java.time.Instant;
import java.util.UUID;

public record ChatMessageResponse(
    UUID id,
    UUID senderId,
    UUID receiverId,
    String content,
    boolean hasImage,
    Instant sentAt,
    Instant readAt
) {}
