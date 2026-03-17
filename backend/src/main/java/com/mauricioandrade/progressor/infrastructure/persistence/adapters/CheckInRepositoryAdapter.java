package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.CheckInRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutCheckInEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataCheckInRepository;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class CheckInRepositoryAdapter implements CheckInRepository {

  private final SpringDataCheckInRepository springDataRepository;

  public CheckInRepositoryAdapter(SpringDataCheckInRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void saveIfNotExists(UUID studentId, LocalDate date) {
    if (!springDataRepository.existsByStudentIdAndDate(studentId, date)) {
      WorkoutCheckInEntity entity = new WorkoutCheckInEntity();
      entity.setId(UUID.randomUUID());
      entity.setStudentId(studentId);
      entity.setDate(date);
      springDataRepository.save(entity);
    }
  }

  @Override
  public List<LocalDate> findDatesByStudentId(UUID studentId) {
    return springDataRepository.findByStudentId(studentId).stream()
        .map(WorkoutCheckInEntity::getDate)
        .toList();
  }

  @Override
  public Optional<LocalDate> findLastDateByStudentId(UUID studentId) {
    return springDataRepository.findTopByStudentIdOrderByDateDesc(studentId)
        .map(WorkoutCheckInEntity::getDate);
  }

  @Override
  public Map<UUID, LocalDate> findLastDatesByStudentIds(Collection<UUID> studentIds) {
    if (studentIds.isEmpty()) return Map.of();
    return springDataRepository.findLastDatesByStudentIds(studentIds).stream()
        .collect(java.util.stream.Collectors.toMap(
            row -> (UUID) row[0],
            row -> (LocalDate) row[1]
        ));
  }
}
