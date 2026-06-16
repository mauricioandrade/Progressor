package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.ChatRepository;
import com.mauricioandrade.progressor.core.domain.chat.ChatMessage;
import com.mauricioandrade.progressor.core.domain.chat.ConversationSummary;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.ChatMessageEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataChatRepository;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ChatRepositoryAdapter implements ChatRepository {

  private final SpringDataChatRepository repository;

  public ChatRepositoryAdapter(SpringDataChatRepository repository) {
    this.repository = repository;
  }

  @Override
  public ChatMessage save(ChatMessage message) {
    return toDomain(repository.save(toEntity(message)));
  }

  @Override
  public List<ChatMessage> findConversation(UUID userA, UUID userB, Instant since) {
    Instant from = since != null ? since : Instant.EPOCH;
    return repository.findConversation(userA, userB, from).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public List<ConversationSummary> findConversationsByUser(UUID userId) {
    return repository.findConversationSummaries(userId).stream()
        .map(row -> new ConversationSummary(
            (UUID) row[0],
            (String) row[1],
            (String) row[2],
            row[3] != null ? ((java.sql.Timestamp) row[3]).toInstant() : null,
            ((Number) row[4]).intValue()
        ))
        .toList();
  }

  @Override
  public int countUnread(UUID receiverId, UUID senderId) {
    return repository.countUnread(receiverId, senderId);
  }

  @Override
  public void markAsRead(UUID receiverId, UUID senderId) {
    repository.markAsRead(receiverId, senderId);
  }

  @Override
  public Optional<ChatMessage> findById(UUID id) {
    return repository.findById(id).map(this::toDomain);
  }

  private ChatMessageEntity toEntity(ChatMessage m) {
    ChatMessageEntity e = new ChatMessageEntity();
    e.setId(m.getId());
    e.setSenderId(m.getSenderId());
    e.setReceiverId(m.getReceiverId());
    e.setContent(m.getContent());
    e.setImageData(m.getImageData());
    e.setSentAt(m.getSentAt());
    e.setReadAt(m.getReadAt());
    return e;
  }

  private ChatMessage toDomain(ChatMessageEntity e) {
    return new ChatMessage(e.getId(), e.getSenderId(), e.getReceiverId(),
        e.getContent(), e.getImageData(), e.getSentAt(), e.getReadAt());
  }
}
