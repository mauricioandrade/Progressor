package com.mauricioandrade.progressor.infrastructure.api.dto;

public record LoginResponse(String userId, String email, String role, String expiresAt) {

}