package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import java.util.UUID;

public class AssignStudentToTrainerUseCase {

  private final UserRepository userRepository;
  private final ConnectionRequestRepository connectionRequestRepository;

  public AssignStudentToTrainerUseCase(UserRepository userRepository,
      ConnectionRequestRepository connectionRequestRepository) {
    this.userRepository = userRepository;
    this.connectionRequestRepository = connectionRequestRepository;
  }

  public void execute(UUID studentId, UUID trainerId) {
    if (!connectionRequestRepository.existsAcceptedConnection(trainerId, studentId, ProfessionalRole.COACH)) {
      throw new IllegalStateException("Conexão aceita não encontrada entre trainer e aluno");
    }

    var student = userRepository.findStudentById(studentId)
        .orElseThrow(() -> new IllegalArgumentException("Student not found"));

    if (!userRepository.existsById(trainerId)) {
      throw new IllegalArgumentException("Trainer not found");
    }

    student.assignPersonalTrainer(trainerId);
    userRepository.update(student);
  }
}
