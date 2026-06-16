package com.mauricioandrade.progressor.core.domain.chat;

import java.time.Instant;
import java.util.UUID;

public class ConversationSummary {

  private final UUID partnerId;
  private final String partnerName;
  private final String lastMessageContent;
  private final Instant lastMessageAt;
  private final int unreadCount;

  public ConversationSummary(UUID partnerId, String partnerName, String lastMessageContent,
      Instant lastMessageAt, int unreadCount) {
    this.partnerId = partnerId;
    this.partnerName = partnerName;
    this.lastMessageContent = lastMessageContent;
    this.lastMessageAt = lastMessageAt;
    this.unreadCount = unreadCount;
  }

  public UUID getPartnerId() { return partnerId; }
  public String getPartnerName() { return partnerName; }
  public String getLastMessageContent() { return lastMessageContent; }
  public Instant getLastMessageAt() { return lastMessageAt; }
  public int getUnreadCount() { return unreadCount; }
}
