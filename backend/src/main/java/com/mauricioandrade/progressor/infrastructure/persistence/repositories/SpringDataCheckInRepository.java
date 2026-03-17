package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutCheckInEntity;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataCheckInRepository extends JpaRepository<WorkoutCheckInEntity, UUID> {

  boolean existsByStudentIdAndDate(UUID studentId, LocalDate date);

  List<WorkoutCheckInEntity> findByStudentId(UUID studentId);

  Optional<WorkoutCheckInEntity> findTopByStudentIdOrderByDateDesc(UUID studentId);

  @Query("SELECT c.studentId, MAX(c.date) FROM WorkoutCheckInEntity c WHERE c.studentId IN :studentIds GROUP BY c.studentId")
  List<Object[]> findLastDatesByStudentIds(@Param("studentIds") Collection<UUID> studentIds);
}
