package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.ChatMessageEntity;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface SpringDataChatRepository extends JpaRepository<ChatMessageEntity, UUID> {

  @Query("""
      SELECT m FROM ChatMessageEntity m
      WHERE ((m.senderId = :userA AND m.receiverId = :userB)
          OR (m.senderId = :userB AND m.receiverId = :userA))
        AND m.sentAt >= :since
      ORDER BY m.sentAt ASC
      """)
  List<ChatMessageEntity> findConversation(
      @Param("userA") UUID userA,
      @Param("userB") UUID userB,
      @Param("since") Instant since);

  @Query(value = """
      WITH ranked AS (
          SELECT
              CASE WHEN sender_id = :userId THEN receiver_id ELSE sender_id END AS partner_id,
              content AS last_content,
              sent_at AS last_sent_at,
              ROW_NUMBER() OVER (
                  PARTITION BY LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id)
                  ORDER BY sent_at DESC
              ) AS rn
          FROM chat_messages
          WHERE sender_id = :userId OR receiver_id = :userId
      ),
      latest AS (
          SELECT partner_id, last_content, last_sent_at FROM ranked WHERE rn = 1
      )
      SELECT
          l.partner_id,
          u.first_name || ' ' || u.last_name AS partner_name,
          l.last_content,
          l.last_sent_at,
          (SELECT COUNT(*) FROM chat_messages
           WHERE receiver_id = :userId AND sender_id = l.partner_id AND read_at IS NULL) AS unread_count
      FROM latest l
      JOIN app_users u ON u.id = l.partner_id
      ORDER BY l.last_sent_at DESC
      """, nativeQuery = true)
  List<Object[]> findConversationSummaries(@Param("userId") UUID userId);

  @Query(value = """
      SELECT COUNT(*) FROM chat_messages
      WHERE receiver_id = :receiverId AND sender_id = :senderId AND read_at IS NULL
      """, nativeQuery = true)
  int countUnread(@Param("receiverId") UUID receiverId, @Param("senderId") UUID senderId);

  @Modifying
  @Transactional
  @Query(value = """
      UPDATE chat_messages SET read_at = NOW()
      WHERE receiver_id = :receiverId AND sender_id = :senderId AND read_at IS NULL
      """, nativeQuery = true)
  void markAsRead(@Param("receiverId") UUID receiverId, @Param("senderId") UUID senderId);
}
