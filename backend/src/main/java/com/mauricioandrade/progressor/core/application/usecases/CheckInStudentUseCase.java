package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.CheckInRepository;
import java.time.LocalDate;
import java.util.UUID;

public class CheckInStudentUseCase {

  private final CheckInRepository checkInRepository;

  public CheckInStudentUseCase(CheckInRepository checkInRepository) {
    this.checkInRepository = checkInRepository;
  }

  public void execute(UUID studentId) {
    checkInRepository.saveIfNotExists(studentId, LocalDate.now());
  }
}
