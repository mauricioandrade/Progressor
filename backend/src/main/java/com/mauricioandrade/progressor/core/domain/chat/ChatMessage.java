package com.mauricioandrade.progressor.core.domain.chat;

import java.time.Instant;
import java.util.UUID;

public class ChatMessage {

  private final UUID id;
  private final UUID senderId;
  private final UUID receiverId;
  private final String content;
  private final byte[] imageData;
  private final boolean hasImage;
  private final Instant sentAt;
  private final Instant readAt;

  /** Full constructor — used when imageData is actually loaded (e.g. image endpoint). */
  public ChatMessage(UUID id, UUID senderId, UUID receiverId, String content, byte[] imageData,
      Instant sentAt, Instant readAt) {
    this.hasImage = imageData != null;
    if (content == null && !hasImage) {
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

  /** Lightweight constructor — for list queries that skip loading image bytes. */
  public ChatMessage(UUID id, UUID senderId, UUID receiverId, String content, boolean hasImage,
      Instant sentAt, Instant readAt) {
    if (content == null && !hasImage) {
      throw new IllegalArgumentException("Either content or imageData must be present");
    }
    this.id = id;
    this.senderId = senderId;
    this.receiverId = receiverId;
    this.content = content;
    this.imageData = null;
    this.hasImage = hasImage;
    this.sentAt = sentAt;
    this.readAt = readAt;
  }

  public UUID getId() { return id; }
  public UUID getSenderId() { return senderId; }
  public UUID getReceiverId() { return receiverId; }
  public String getContent() { return content; }
  public byte[] getImageData() { return imageData; }
  public boolean isHasImage() { return hasImage; }
  public Instant getSentAt() { return sentAt; }
  public Instant getReadAt() { return readAt; }
}
