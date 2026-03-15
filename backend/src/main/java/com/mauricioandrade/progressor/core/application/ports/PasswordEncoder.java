package com.mauricioandrade.progressor.core.application.ports;

public interface PasswordEncoder {

  String encode(String rawPassword);
}