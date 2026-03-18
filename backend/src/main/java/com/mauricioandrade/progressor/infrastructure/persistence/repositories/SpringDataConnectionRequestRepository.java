package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.ConnectionRequestEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataConnectionRequestRepository
    extends JpaRepository<ConnectionRequestEntity, UUID> {

  List<ConnectionRequestEntity> findByStudentIdAndStatus(UUID studentId, String status);

  List<ConnectionRequestEntity> findByProfessionalId(UUID professionalId);

  @Query("SELECT COUNT(r) > 0 FROM ConnectionRequestEntity r WHERE r.professionalId = :professionalId AND r.studentId = :studentId AND r.professionalRole = :role AND r.status = 'PENDING'")
  boolean existsPendingRequest(@Param("professionalId") UUID professionalId,
      @Param("studentId") UUID studentId, @Param("role") String role);
}
