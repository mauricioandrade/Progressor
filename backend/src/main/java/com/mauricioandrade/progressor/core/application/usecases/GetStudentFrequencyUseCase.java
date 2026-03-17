package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.CheckInRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public class GetStudentFrequencyUseCase {

  private final CheckInRepository checkInRepository;

  public GetStudentFrequencyUseCase(CheckInRepository checkInRepository) {
    this.checkInRepository = checkInRepository;
  }

  public List<LocalDate> execute(UUID studentId) {
    return checkInRepository.findDatesByStudentId(studentId);
  }
}
