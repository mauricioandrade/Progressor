package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.domain.connection.ConnectionRequest;
import com.mauricioandrade.progressor.core.domain.connection.ConnectionStatus;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import com.mauricioandrade.progressor.infrastructure.persistence.mappers.ConnectionRequestMapper;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataConnectionRequestRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class ConnectionRequestRepositoryAdapter implements ConnectionRequestRepository {

  private final SpringDataConnectionRequestRepository springDataRepository;

  public ConnectionRequestRepositoryAdapter(
      SpringDataConnectionRequestRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void save(ConnectionRequest request) {
    springDataRepository.save(ConnectionRequestMapper.toEntity(request));
  }

  @Override
  public void updateStatus(UUID id, ConnectionStatus status) {
    springDataRepository.findById(id).ifPresent(entity -> {
      entity.setStatus(status.name());
      springDataRepository.save(entity);
    });
  }

  @Override
  public Optional<ConnectionRequest> findById(UUID id) {
    return springDataRepository.findById(id).map(ConnectionRequestMapper::toDomain);
  }

  @Override
  public List<ConnectionRequest> findByStudentIdAndStatus(UUID studentId, ConnectionStatus status) {
    return springDataRepository.findByStudentIdAndStatus(studentId, status.name())
        .stream().map(ConnectionRequestMapper::toDomain).toList();
  }

  @Override
  public List<ConnectionRequest> findByProfessionalId(UUID professionalId) {
    return springDataRepository.findByProfessionalId(professionalId)
        .stream().map(ConnectionRequestMapper::toDomain).toList();
  }

  @Override
  public boolean existsPendingRequest(UUID professionalId, UUID studentId, ProfessionalRole role) {
    return springDataRepository.existsPendingRequest(professionalId, studentId, role.name());
  }
}
