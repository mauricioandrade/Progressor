package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.RegisterStudentRequest;
import com.mauricioandrade.progressor.core.application.ports.PasswordEncoder;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.common.Email;
import com.mauricioandrade.progressor.core.domain.user.Student;
import java.util.UUID;

public class RegisterStudentUseCase {

  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;

  public RegisterStudentUseCase(UserRepository userRepository, PasswordEncoder passwordEncoder) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
  }

  public UUID execute(RegisterStudentRequest request) {
    Email email = new Email(request.email());

    if (userRepository.existsByEmail(email)) {
      throw new IllegalStateException("Email is already registered");
    }

    String hashedPassword = passwordEncoder.encode(request.password());

    Student student = new Student(null, request.firstName(), request.lastName(), email,
        hashedPassword, request.birthDate());

    userRepository.save(student);

    return student.getId();
  }
}