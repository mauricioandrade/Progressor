package com.mauricioandrade.progressor.infrastructure.persistence.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "chat_messages")
public class ChatMessageEntity {

  @Id
  private UUID id;

  @Column(name = "sender_id", nullable = false)
  private UUID senderId;

  @Column(name = "receiver_id", nullable = false)
  private UUID receiverId;

  @Column(columnDefinition = "TEXT")
  private String content;

  @Column(name = "image_data", columnDefinition = "bytea")
  private byte[] imageData;

  @Column(name = "sent_at", nullable = false)
  private Instant sentAt;

  @Column(name = "read_at")
  private Instant readAt;

  public UUID getId() { return id; }
  public void setId(UUID id) { this.id = id; }
  public UUID getSenderId() { return senderId; }
  public void setSenderId(UUID senderId) { this.senderId = senderId; }
  public UUID getReceiverId() { return receiverId; }
  public void setReceiverId(UUID receiverId) { this.receiverId = receiverId; }
  public String getContent() { return content; }
  public void setContent(String content) { this.content = content; }
  public byte[] getImageData() { return imageData; }
  public void setImageData(byte[] imageData) { this.imageData = imageData; }
  public Instant getSentAt() { return sentAt; }
  public void setSentAt(Instant sentAt) { this.sentAt = sentAt; }
  public Instant getReadAt() { return readAt; }
  public void setReadAt(Instant readAt) { this.readAt = readAt; }
}
