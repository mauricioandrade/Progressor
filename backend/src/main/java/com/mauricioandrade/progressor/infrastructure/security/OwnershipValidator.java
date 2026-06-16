package com.mauricioandrade.progressor.infrastructure.security;

import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import java.util.UUID;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

@Component
public class OwnershipValidator {

  private final ConnectionRequestRepository connectionRepo;
  private final WorkoutRepository workoutRepository;

  public OwnershipValidator(ConnectionRequestRepository connectionRepo,
      WorkoutRepository workoutRepository) {
    this.connectionRepo = connectionRepo;
    this.workoutRepository = workoutRepository;
  }

  public void assertCoachOwnsStudent(UUID coachId, UUID studentId) {
    if (!connectionRepo.existsAcceptedConnection(coachId, studentId, ProfessionalRole.COACH)) {
      throw new AccessDeniedException("Acesso negado a este estudante");
    }
  }

  public void assertNutriOwnsPatient(UUID nutriId, UUID studentId) {
    if (!connectionRepo.existsAcceptedConnection(nutriId, studentId, ProfessionalRole.NUTRI)) {
      throw new AccessDeniedException("Acesso negado a este estudante");
    }
  }

  /**
   * Validates professional access. Maps Spring Security role names to ProfessionalRole:
   * PERSONALTRAINER → COACH, NUTRITIONIST → NUTRI.
   */
  public void assertProfessionalOwnsStudent(UUID professionalId, String springRole, UUID studentId) {
    ProfessionalRole professionalRole = switch (springRole) {
      case "PERSONALTRAINER" -> ProfessionalRole.COACH;
      case "NUTRITIONIST" -> ProfessionalRole.NUTRI;
      default -> throw new AccessDeniedException("Acesso negado a este estudante");
    };
    if (!connectionRepo.existsAcceptedConnection(professionalId, studentId, professionalRole)) {
      throw new AccessDeniedException("Acesso negado a este estudante");
    }
  }

  public void assertCoachOwnsExercise(UUID coachId, UUID exerciseId) {
    UUID studentId = workoutRepository.findById(exerciseId)
        .map(e -> e.getStudentId())
        .orElseThrow(() -> new IllegalArgumentException("Exercise not found: " + exerciseId));
    assertCoachOwnsStudent(coachId, studentId);
  }

  /**
   * For endpoints accessible by students (own data only) and professionals (their students).
   */
  public void assertCanAccessStudentData(UserPrincipal currentUser, UUID studentId) {
    String role = currentUser.getRole();
    if ("STUDENT".equals(role)) {
      if (!currentUser.getId().equals(studentId)) {
        throw new AccessDeniedException("Acesso negado a este estudante");
      }
    } else {
      assertProfessionalOwnsStudent(currentUser.getId(), role, studentId);
    }
  }

}
