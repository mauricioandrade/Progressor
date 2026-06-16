package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.ChatMessageResponse;
import com.mauricioandrade.progressor.core.application.dto.ConversationSummaryResponse;
import com.mauricioandrade.progressor.core.application.dto.SendMessageRequest;
import com.mauricioandrade.progressor.core.application.usecases.GetConversationUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetConversationsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SendMessageUseCase;
import com.mauricioandrade.progressor.core.application.ports.ChatRepository;
import com.mauricioandrade.progressor.infrastructure.security.ImageValidator;
import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/chat")
public class ChatController {

  private final SendMessageUseCase sendMessageUseCase;
  private final GetConversationUseCase getConversationUseCase;
  private final GetConversationsUseCase getConversationsUseCase;
  private final ChatRepository chatRepository;
  private final ImageValidator imageValidator;

  public ChatController(SendMessageUseCase sendMessageUseCase,
      GetConversationUseCase getConversationUseCase,
      GetConversationsUseCase getConversationsUseCase,
      ChatRepository chatRepository,
      ImageValidator imageValidator) {
    this.sendMessageUseCase = sendMessageUseCase;
    this.getConversationUseCase = getConversationUseCase;
    this.getConversationsUseCase = getConversationsUseCase;
    this.chatRepository = chatRepository;
    this.imageValidator = imageValidator;
  }

  @PostMapping(value = "/messages", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ChatMessageResponse> sendMessage(
      @RequestParam(value = "receiverId") UUID receiverId,
      @RequestParam(value = "content", required = false) String content,
      @RequestParam(value = "image", required = false) MultipartFile image,
      @AuthenticationPrincipal UserPrincipal currentUser) throws Exception {
    byte[] imageData = image != null && !image.isEmpty() ? imageValidator.validateAndRead(image) : null;
    SendMessageRequest request = new SendMessageRequest(receiverId, content, imageData);
    return ResponseEntity.ok(
        sendMessageUseCase.execute(currentUser.getId(), currentUser.getRole(), request));
  }

  @GetMapping("/messages")
  public ResponseEntity<List<ChatMessageResponse>> getMessages(
      @RequestParam UUID partnerId,
      @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant since,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(
        getConversationUseCase.execute(currentUser.getId(), partnerId, since));
  }

  @GetMapping("/conversations")
  public ResponseEntity<List<ConversationSummaryResponse>> getConversations(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(getConversationsUseCase.execute(currentUser.getId()));
  }

  @GetMapping(value = "/messages/{id}/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
  public ResponseEntity<byte[]> getImage(
      @PathVariable UUID id,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return chatRepository.findById(id)
        .map(msg -> {
          boolean canAccess = currentUser.getId().equals(msg.getSenderId())
              || currentUser.getId().equals(msg.getReceiverId());
          if (!canAccess) {
            throw new AccessDeniedException("Acesso negado a esta mensagem");
          }
          byte[] data = msg.getImageData();
          if (data == null || data.length == 0) {
            return ResponseEntity.notFound().<byte[]>build();
          }
          return ResponseEntity.ok()
              .contentType(MediaType.APPLICATION_OCTET_STREAM)
              .body(data);
        })
        .orElse(ResponseEntity.notFound().build());
  }
}
