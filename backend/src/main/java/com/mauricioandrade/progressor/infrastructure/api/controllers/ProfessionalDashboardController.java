package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.ProfessionalDashboardStudentSummary;
import com.mauricioandrade.progressor.core.application.usecases.GetProfessionalDashboardUseCase;
import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/professional")
public class ProfessionalDashboardController {

  private final GetProfessionalDashboardUseCase dashboardUseCase;

  public ProfessionalDashboardController(GetProfessionalDashboardUseCase dashboardUseCase) {
    this.dashboardUseCase = dashboardUseCase;
  }

  @GetMapping("/dashboard")
  public ResponseEntity<List<ProfessionalDashboardStudentSummary>> getDashboard(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    List<ProfessionalDashboardStudentSummary> result = switch (currentUser.getRole()) {
      case "PERSONALTRAINER" -> dashboardUseCase.executeForTrainer(currentUser.getId());
      case "NUTRITIONIST" -> dashboardUseCase.executeForNutritionist(currentUser.getId());
      default -> List.of();
    };
    return ResponseEntity.ok(result);
  }
}
