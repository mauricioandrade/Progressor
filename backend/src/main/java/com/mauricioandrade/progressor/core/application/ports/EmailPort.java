package com.mauricioandrade.progressor.core.application.ports;

public interface EmailPort {

  void sendPasswordReset(String to, String firstName, String resetLink);

  void sendInactivityAlert(String to, String firstName);
}
