package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.connection.ConnectionRequest;
import com.mauricioandrade.progressor.core.domain.connection.ConnectionStatus;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ConnectionRequestRepository {

  void save(ConnectionRequest request);

  void updateStatus(UUID id, ConnectionStatus status);

  Optional<ConnectionRequest> findById(UUID id);

  List<ConnectionRequest> findByStudentIdAndStatus(UUID studentId, ConnectionStatus status);

  List<ConnectionRequest> findByProfessionalId(UUID professionalId);

  boolean existsPendingRequest(UUID professionalId, UUID studentId, ProfessionalRole role);
}
