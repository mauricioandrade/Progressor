package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ChatMessageResponse;
import com.mauricioandrade.progressor.core.application.dto.SendMessageRequest;
import com.mauricioandrade.progressor.core.application.ports.ChatRepository;
import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.PushNotificationPort;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.chat.ChatMessage;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import com.mauricioandrade.progressor.core.domain.user.Student;
import java.time.Instant;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;

public class SendMessageUseCase {

  private final ChatRepository chatRepository;
  private final UserRepository userRepository;
  private final ConnectionRequestRepository connectionRepo;
  private final PushNotificationPort pushNotificationPort;

  public SendMessageUseCase(ChatRepository chatRepository, UserRepository userRepository,
      ConnectionRequestRepository connectionRepo, PushNotificationPort pushNotificationPort) {
    this.chatRepository = chatRepository;
    this.userRepository = userRepository;
    this.connectionRepo = connectionRepo;
    this.pushNotificationPort = pushNotificationPort;
  }

  public ChatMessageResponse execute(UUID senderId, String senderRole, SendMessageRequest request) {
    validateAuthorization(senderId, senderRole, request.receiverId());
    ChatMessage message = new ChatMessage(UUID.randomUUID(), senderId, request.receiverId(),
        request.content(), request.imageData(), Instant.now(), null);
    ChatMessage saved = chatRepository.save(message);
    String pushBody = request.content() != null ? request.content() : "Imagem";
    pushNotificationPort.sendToStudent(request.receiverId(), "Nova mensagem", pushBody);
    return toResponse(saved);
  }

  private void validateAuthorization(UUID senderId, String senderRole, UUID receiverId) {
    if ("STUDENT".equals(senderRole)) {
      Student student = userRepository.findStudentById(senderId)
          .orElseThrow(() -> new AccessDeniedException("Aluno não encontrado"));
      boolean canSend = receiverId.equals(student.getPersonalTrainerId())
          || receiverId.equals(student.getNutritionistId());
      if (!canSend) {
        throw new AccessDeniedException("Você só pode enviar mensagens para seu trainer ou nutricionista");
      }
    } else {
      ProfessionalRole role = "PERSONALTRAINER".equals(senderRole)
          ? ProfessionalRole.COACH : ProfessionalRole.NUTRI;
      if (!connectionRepo.existsAcceptedConnection(senderId, receiverId, role)) {
        throw new AccessDeniedException("Você só pode enviar mensagens para seus alunos");
      }
    }
  }

  private ChatMessageResponse toResponse(ChatMessage m) {
    return new ChatMessageResponse(m.getId(), m.getSenderId(), m.getReceiverId(),
        m.getContent(), m.getImageData() != null, m.getSentAt(), m.getReadAt());
  }
}
