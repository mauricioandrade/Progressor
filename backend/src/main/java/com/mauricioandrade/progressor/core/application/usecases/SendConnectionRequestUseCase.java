package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.common.Email;
import com.mauricioandrade.progressor.core.domain.connection.ConnectionRequest;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import java.util.UUID;

public class SendConnectionRequestUseCase {

  private final ConnectionRequestRepository connectionRequestRepository;
  private final UserRepository userRepository;

  public SendConnectionRequestUseCase(ConnectionRequestRepository connectionRequestRepository,
      UserRepository userRepository) {
    this.connectionRequestRepository = connectionRequestRepository;
    this.userRepository = userRepository;
  }

  public void execute(UUID professionalId, String studentEmail, ProfessionalRole role) {
    var professional = userRepository.findById(professionalId)
        .orElseThrow(() -> new IllegalArgumentException("Professional not found"));

    var student = userRepository.findStudentByEmail(new Email(studentEmail))
        .orElseThrow(() -> new IllegalArgumentException("Student not found with email: " + studentEmail));

    if (connectionRequestRepository.existsPendingRequest(professionalId, student.getId(), role)) {
      throw new IllegalStateException("A pending invitation already exists for this student");
    }

    String professionalName = professional.getFirstName() + " " + professional.getLastName();
    var request = new ConnectionRequest(null, professionalId, student.getId(), professionalName, role);
    connectionRequestRepository.save(request);
  }
}
