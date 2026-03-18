package com.mauricioandrade.progressor.infrastructure.persistence.mappers;

import com.mauricioandrade.progressor.core.domain.connection.ConnectionRequest;
import com.mauricioandrade.progressor.core.domain.connection.ConnectionStatus;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.ConnectionRequestEntity;

public class ConnectionRequestMapper {

  public static ConnectionRequestEntity toEntity(ConnectionRequest domain) {
    ConnectionRequestEntity entity = new ConnectionRequestEntity();
    entity.setId(domain.getId());
    entity.setProfessionalId(domain.getProfessionalId());
    entity.setStudentId(domain.getStudentId());
    entity.setProfessionalName(domain.getProfessionalName());
    entity.setProfessionalRole(domain.getProfessionalRole().name());
    entity.setStatus(domain.getStatus().name());
    entity.setCreatedAt(domain.getCreatedAt());
    return entity;
  }

  public static ConnectionRequest toDomain(ConnectionRequestEntity entity) {
    return new ConnectionRequest(
        entity.getId(),
        entity.getProfessionalId(),
        entity.getStudentId(),
        entity.getProfessionalName(),
        ProfessionalRole.valueOf(entity.getProfessionalRole()),
        ConnectionStatus.valueOf(entity.getStatus()),
        entity.getCreatedAt()
    );
  }
}
