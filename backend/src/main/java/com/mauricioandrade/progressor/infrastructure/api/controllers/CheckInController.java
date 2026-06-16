package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.usecases.CheckInStudentUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentFrequencyUseCase;
import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import com.mauricioandrade.progressor.infrastructure.security.OwnershipValidator;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/checkins")
public class CheckInController {

  private final CheckInStudentUseCase checkInStudentUseCase;
  private final GetStudentFrequencyUseCase getStudentFrequencyUseCase;
  private final OwnershipValidator ownershipValidator;

  public CheckInController(CheckInStudentUseCase checkInStudentUseCase,
      GetStudentFrequencyUseCase getStudentFrequencyUseCase,
      OwnershipValidator ownershipValidator) {
    this.checkInStudentUseCase = checkInStudentUseCase;
    this.getStudentFrequencyUseCase = getStudentFrequencyUseCase;
    this.ownershipValidator = ownershipValidator;
  }

  @PostMapping("/my")
  public ResponseEntity<Void> checkIn(@AuthenticationPrincipal UserPrincipal currentUser) {
    checkInStudentUseCase.execute(currentUser.getId());
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/my")
  public ResponseEntity<List<LocalDate>> getMyFrequency(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(getStudentFrequencyUseCase.execute(currentUser.getId()));
  }

  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<LocalDate>> getStudentFrequency(@PathVariable UUID studentId,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    ownershipValidator.assertProfessionalOwnsStudent(currentUser.getId(),
        currentUser.getRole(),
        studentId);
    return ResponseEntity.ok(getStudentFrequencyUseCase.execute(studentId));
  }
}
