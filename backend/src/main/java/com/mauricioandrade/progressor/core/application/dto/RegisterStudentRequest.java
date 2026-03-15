package com.mauricioandrade.progressor.core.application.dto;

import java.time.LocalDate;

public record RegisterStudentRequest(String firstName, String lastName, String email,
                                     String password, LocalDate birthDate) {

}