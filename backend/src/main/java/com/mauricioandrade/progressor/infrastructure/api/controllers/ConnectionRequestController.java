package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.ConnectionRequestResponse;
import com.mauricioandrade.progressor.core.application.dto.RespondToConnectionRequestRequest;
import com.mauricioandrade.progressor.core.application.usecases.GetPendingConnectionRequestsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RespondToConnectionRequestUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SendConnectionRequestUseCase;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/connections")
public class ConnectionRequestController {

  private final SendConnectionRequestUseCase sendConnectionRequestUseCase;
  private final RespondToConnectionRequestUseCase respondToConnectionRequestUseCase;
  private final GetPendingConnectionRequestsUseCase getPendingConnectionRequestsUseCase;

  public ConnectionRequestController(SendConnectionRequestUseCase sendConnectionRequestUseCase,
      RespondToConnectionRequestUseCase respondToConnectionRequestUseCase,
      GetPendingConnectionRequestsUseCase getPendingConnectionRequestsUseCase) {
    this.sendConnectionRequestUseCase = sendConnectionRequestUseCase;
    this.respondToConnectionRequestUseCase = respondToConnectionRequestUseCase;
    this.getPendingConnectionRequestsUseCase = getPendingConnectionRequestsUseCase;
  }

  /**
   * Called by a professional to invite a student by email.
   * Role must be COACH or NUTRI.
   */
  @PostMapping("/invite")
  public ResponseEntity<Void> sendInvite(
      @RequestParam String studentEmail,
      @RequestParam ProfessionalRole role,
      @AuthenticationPrincipal UserEntity currentUser) {
    sendConnectionRequestUseCase.execute(currentUser.getId(), studentEmail, role);
    return ResponseEntity.status(HttpStatus.CREATED).build();
  }

  /**
   * Called by the authenticated student to see their pending invitations.
   */
  @GetMapping("/pending")
  public ResponseEntity<List<ConnectionRequestResponse>> getPending(
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(getPendingConnectionRequestsUseCase.execute(currentUser.getId()));
  }

  /**
   * Called by the authenticated student to accept or reject an invitation.
   */
  @PostMapping("/respond")
  public ResponseEntity<Void> respond(
      @Valid @RequestBody RespondToConnectionRequestRequest request,
      @AuthenticationPrincipal UserEntity currentUser) {
    respondToConnectionRequestUseCase.execute(request.requestId(), currentUser.getId(),
        request.accepted());
    return ResponseEntity.ok().build();
  }
}
