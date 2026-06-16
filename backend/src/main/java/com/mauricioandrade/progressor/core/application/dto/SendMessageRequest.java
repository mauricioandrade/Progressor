package com.mauricioandrade.progressor.core.application.dto;

import java.util.UUID;

public record SendMessageRequest(UUID receiverId, String content, byte[] imageData) {}
