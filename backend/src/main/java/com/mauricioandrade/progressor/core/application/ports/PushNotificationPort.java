package com.mauricioandrade.progressor.core.application.ports;

import java.util.UUID;

public interface PushNotificationPort {
  void sendToStudent(UUID studentId, String title, String body);
}
