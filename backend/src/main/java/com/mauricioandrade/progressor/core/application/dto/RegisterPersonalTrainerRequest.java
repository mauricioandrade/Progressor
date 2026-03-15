package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDate;

public record RegisterPersonalTrainerRequest(String firstName, String lastName, String email,
                                             String password, LocalDate birthDate, String cref) {

}