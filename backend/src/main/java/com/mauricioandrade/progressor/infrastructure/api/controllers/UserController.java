package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.RegisterPersonalTrainerRequest;
import com.mauricioandrade.progressor.core.application.dto.RegisterStudentRequest;
import com.mauricioandrade.progressor.core.application.usecases.RegisterPersonalTrainerUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RegisterStudentUseCase;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final RegisterPersonalTrainerUseCase registerPersonalTrainerUseCase;
  private final RegisterStudentUseCase registerStudentUseCase;

  public UserController(RegisterPersonalTrainerUseCase ptUseCase,
      RegisterStudentUseCase studentUseCase) {
    this.registerPersonalTrainerUseCase = ptUseCase;
    this.registerStudentUseCase = studentUseCase;
  }

  @PostMapping("/register/personal")
  public ResponseEntity<Map<String, UUID>> registerPersonalTrainer(
      @RequestBody RegisterPersonalTrainerRequest request) {
    UUID id = registerPersonalTrainerUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
  }

  @PostMapping("/register/student")
  public ResponseEntity<Map<String, UUID>> registerStudent(
      @RequestBody RegisterStudentRequest request) {
    UUID id = registerStudentUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
  }
}