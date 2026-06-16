package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ChatMessageResponse;
import com.mauricioandrade.progressor.core.application.ports.ChatRepository;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class GetConversationUseCase {

  private final ChatRepository chatRepository;

  public GetConversationUseCase(ChatRepository chatRepository) {
    this.chatRepository = chatRepository;
  }

  public List<ChatMessageResponse> execute(UUID currentUserId, UUID partnerId, Instant since) {
    chatRepository.markAsRead(currentUserId, partnerId);
    return chatRepository.findConversation(currentUserId, partnerId, since).stream()
        .map(m -> new ChatMessageResponse(m.getId(), m.getSenderId(), m.getReceiverId(),
            m.getContent(), m.isHasImage(), m.getSentAt(), m.getReadAt()))
        .toList();
  }
}
