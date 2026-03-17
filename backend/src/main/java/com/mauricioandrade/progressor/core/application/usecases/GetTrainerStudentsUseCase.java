package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.StudentSummaryResponse;
import com.mauricioandrade.progressor.core.application.ports.CheckInRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import java.util.List;
import java.util.UUID;

public class GetTrainerStudentsUseCase {

  private final UserRepository userRepository;
  private final CheckInRepository checkInRepository;

  public GetTrainerStudentsUseCase(UserRepository userRepository,
      CheckInRepository checkInRepository) {
    this.userRepository = userRepository;
    this.checkInRepository = checkInRepository;
  }

  public List<StudentSummaryResponse> execute(UUID trainerId) {
    var students = userRepository.findStudentsByTrainerId(trainerId);
    var ids = students.stream().map(s -> s.getId()).toList();
    var lastCheckIns = checkInRepository.findLastDatesByStudentIds(ids);
    return students.stream()
        .map(s -> new StudentSummaryResponse(
            s.getId(),
            s.getFirstName() + " " + s.getLastName(),
            s.getEmail().value(),
            lastCheckIns.get(s.getId())))
        .toList();
  }
}
