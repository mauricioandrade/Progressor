package com.mauricioandrade.progressor.infrastructure.push;

import com.mauricioandrade.progressor.core.application.ports.PushNotificationPort;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import java.util.UUID;
import org.springframework.stereotype.Component;

@Component
public class ExpoPushNotificationAdapter implements PushNotificationPort {

  private final SpringDataUserRepository userRepository;
  private final ExpoPushService pushService;

  public ExpoPushNotificationAdapter(SpringDataUserRepository userRepository,
      ExpoPushService pushService) {
    this.userRepository = userRepository;
    this.pushService = pushService;
  }

  @Override
  public void sendToStudent(UUID studentId, String title, String body) {
    userRepository.findById(studentId)
        .map(u -> u.getPushToken())
        .filter(token -> token != null && !token.isBlank())
        .ifPresent(token -> pushService.send(token, title, body));
  }
}
