package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import java.util.UUID;

public class RespondToConnectionRequestUseCase {

  private final ConnectionRequestRepository connectionRequestRepository;
  private final UserRepository userRepository;

  public RespondToConnectionRequestUseCase(
      ConnectionRequestRepository connectionRequestRepository, UserRepository userRepository) {
    this.connectionRequestRepository = connectionRequestRepository;
    this.userRepository = userRepository;
  }

  public void execute(UUID requestId, UUID studentId, boolean accepted) {
    var request = connectionRequestRepository.findById(requestId)
        .orElseThrow(() -> new IllegalArgumentException("Connection request not found"));

    if (!request.getStudentId().equals(studentId)) {
      throw new IllegalArgumentException("Request does not belong to this student");
    }

    if (accepted) {
      request.accept();
      connectionRequestRepository.updateStatus(requestId, request.getStatus());

      var student = userRepository.findStudentById(studentId)
          .orElseThrow(() -> new IllegalArgumentException("Student not found"));

      if (request.getProfessionalRole() == ProfessionalRole.COACH) {
        student.assignPersonalTrainer(request.getProfessionalId());
      } else {
        student.assignNutritionist(request.getProfessionalId());
      }
      userRepository.update(student);
    } else {
      request.reject();
      connectionRequestRepository.updateStatus(requestId, request.getStatus());
    }
  }
}
