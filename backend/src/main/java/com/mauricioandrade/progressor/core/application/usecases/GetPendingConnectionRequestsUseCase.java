package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ConnectionRequestResponse;
import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.domain.connection.ConnectionStatus;
import java.util.List;
import java.util.UUID;

public class GetPendingConnectionRequestsUseCase {

  private final ConnectionRequestRepository connectionRequestRepository;

  public GetPendingConnectionRequestsUseCase(
      ConnectionRequestRepository connectionRequestRepository) {
    this.connectionRequestRepository = connectionRequestRepository;
  }

  public List<ConnectionRequestResponse> execute(UUID studentId) {
    return connectionRequestRepository.findByStudentIdAndStatus(studentId, ConnectionStatus.PENDING)
        .stream()
        .map(r -> new ConnectionRequestResponse(r.getId(), r.getProfessionalId(),
            r.getProfessionalName(), r.getProfessionalRole().name(), r.getStatus().name(),
            r.getCreatedAt()))
        .toList();
  }
}
