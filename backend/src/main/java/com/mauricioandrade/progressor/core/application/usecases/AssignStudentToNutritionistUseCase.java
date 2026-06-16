package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import java.util.UUID;

public class AssignStudentToNutritionistUseCase {

  private final UserRepository userRepository;
  private final ConnectionRequestRepository connectionRequestRepository;

  public AssignStudentToNutritionistUseCase(UserRepository userRepository,
      ConnectionRequestRepository connectionRequestRepository) {
    this.userRepository = userRepository;
    this.connectionRequestRepository = connectionRequestRepository;
  }

  public void execute(UUID studentId, UUID nutritionistId) {
    if (!connectionRequestRepository.existsAcceptedConnection(nutritionistId, studentId, ProfessionalRole.NUTRI)) {
      throw new IllegalStateException("Conexão aceita não encontrada entre nutricionista e aluno");
    }

    var student = userRepository.findStudentById(studentId)
        .orElseThrow(() -> new IllegalArgumentException("Student not found"));
    if (!userRepository.existsById(nutritionistId)) throw new IllegalArgumentException("Nutritionist not found");
    student.assignNutritionist(nutritionistId);
    userRepository.update(student);
  }
}
