package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.chat.ChatMessage;
import com.mauricioandrade.progressor.core.domain.chat.ConversationSummary;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ChatRepository {
  ChatMessage save(ChatMessage message);
  List<ChatMessage> findConversation(UUID userA, UUID userB, Instant since);
  List<ConversationSummary> findConversationsByUser(UUID userId);
  int countUnread(UUID receiverId, UUID senderId);
  void markAsRead(UUID receiverId, UUID senderId);
  Optional<ChatMessage> findById(UUID id);
}
