package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ConversationSummaryResponse;
import com.mauricioandrade.progressor.core.application.ports.ChatRepository;
import java.util.List;
import java.util.UUID;

public class GetConversationsUseCase {

  private final ChatRepository chatRepository;

  public GetConversationsUseCase(ChatRepository chatRepository) {
    this.chatRepository = chatRepository;
  }

  public List<ConversationSummaryResponse> execute(UUID userId) {
    return chatRepository.findConversationsByUser(userId).stream()
        .map(s -> new ConversationSummaryResponse(s.getPartnerId(), s.getPartnerName(),
            s.getLastMessageContent(), s.getLastMessageAt(), s.getUnreadCount()))
        .toList();
  }
}
