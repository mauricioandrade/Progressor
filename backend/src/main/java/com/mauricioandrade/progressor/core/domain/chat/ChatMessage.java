package com.mauricioandrade.progressor.core.domain.chat;

import java.time.Instant;
import java.util.UUID;

public class ChatMessage {

  private final UUID id;
  private final UUID senderId;
  private final UUID receiverId;
  private final String content;
  private final byte[] imageData;
  private final Instant sentAt;
  private final Instant readAt;

  public ChatMessage(UUID id, UUID senderId, UUID receiverId, String content, byte[] imageData,
      Instant sentAt, Instant readAt) {
    if (content == null && imageData == null) {
      throw new IllegalArgumentException("Either content or imageData must be present");
    }
    this.id = id;
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.content = content;
    this.imageData = imageData;
    this.sentAt = sentAt;
    this.readAt = readAt;
  }

  public UUID getId() { return id; }
  public UUID getSenderId() { return senderId; }
  public UUID getReceiverId() { return receiverId; }
  public String getContent() { return content; }
  public byte[] getImageData() { return imageData; }
  public Instant getSentAt() { return sentAt; }
  public Instant getReadAt() { return readAt; }
}
