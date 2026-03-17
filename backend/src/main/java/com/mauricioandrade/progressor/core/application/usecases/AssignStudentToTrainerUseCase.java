package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import java.util.UUID;

public class AssignStudentToTrainerUseCase {

  private final UserRepository userRepository;

  public AssignStudentToTrainerUseCase(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public void execute(UUID studentId, UUID trainerId) {
    var student = userRepository.findStudentById(studentId)
        .orElseThrow(() -> new IllegalArgumentException("Student not found"));

    if (!userRepository.existsById(trainerId)) {
      throw new IllegalArgumentException("Trainer not found");
    }

    student.assignPersonalTrainer(trainerId);
    userRepository.update(student);
  }
}
