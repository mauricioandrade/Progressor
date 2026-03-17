package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.ProgressPhotoEntity;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataProgressPhotoRepository
    extends JpaRepository<ProgressPhotoEntity, UUID> {

  List<ProgressPhotoEntity> findByStudentIdOrderByTakenAtDesc(UUID studentId);

  @Modifying
  @Query("DELETE FROM ProgressPhotoEntity p WHERE p.id = :id AND p.studentId = :studentId")
  void deleteByIdAndStudentId(@Param("id") UUID id, @Param("studentId") UUID studentId);
}
