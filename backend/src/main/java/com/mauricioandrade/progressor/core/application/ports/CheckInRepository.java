package com.mauricioandrade.progressor.core.application.ports;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface CheckInRepository {

  void saveIfNotExists(UUID studentId, LocalDate date);

  List<LocalDate> findDatesByStudentId(UUID studentId);

  Optional<LocalDate> findLastDateByStudentId(UUID studentId);

  Map<UUID, LocalDate> findLastDatesByStudentIds(Collection<UUID> studentIds);
}
