package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.DailyAdherenceResponse;
import com.mauricioandrade.progressor.core.application.dto.MealConsumptionResponse;
import com.mauricioandrade.progressor.core.application.usecases.DeleteExtraFoodLogUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentAdherenceUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetTodayConsumptionUseCase;
import com.mauricioandrade.progressor.core.application.usecases.LogExtraFoodUseCase;
import com.mauricioandrade.progressor.core.application.usecases.ToggleMealConsumptionUseCase;
import com.mauricioandrade.progressor.infrastructure.security.OwnershipValidator;
import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nutrition/consumption")
public class MealConsumptionController {

  private final ToggleMealConsumptionUseCase toggleUseCase;
  private final GetTodayConsumptionUseCase getTodayUseCase;
  private final GetStudentAdherenceUseCase getAdherenceUseCase;
  private final LogExtraFoodUseCase logExtraFoodUseCase;
  private final DeleteExtraFoodLogUseCase deleteExtraFoodLogUseCase;
  private final OwnershipValidator ownershipValidator;

  public MealConsumptionController(ToggleMealConsumptionUseCase toggleUseCase,
      GetTodayConsumptionUseCase getTodayUseCase,
      GetStudentAdherenceUseCase getAdherenceUseCase,
      LogExtraFoodUseCase logExtraFoodUseCase,
      DeleteExtraFoodLogUseCase deleteExtraFoodLogUseCase,
      OwnershipValidator ownershipValidator) {
    this.toggleUseCase = toggleUseCase;
    this.getTodayUseCase = getTodayUseCase;
    this.getAdherenceUseCase = getAdherenceUseCase;
    this.logExtraFoodUseCase = logExtraFoodUseCase;
    this.deleteExtraFoodLogUseCase = deleteExtraFoodLogUseCase;
    this.ownershipValidator = ownershipValidator;
  }

  @PostMapping("/toggle")
  public ResponseEntity<MealConsumptionResponse> toggle(
      @RequestBody Map<String, String> body,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    UUID mealItemId = UUID.fromString(body.get("mealItemId"));
    return ResponseEntity.ok(toggleUseCase.execute(currentUser.getId(), mealItemId));
  }

  @GetMapping("/today")
  public ResponseEntity<MealConsumptionResponse> getToday(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(getTodayUseCase.execute(currentUser.getId()));
  }

  @PostMapping("/extra")
  public ResponseEntity<MealConsumptionResponse> logExtra(
      @RequestBody Map<String, Object> body,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    String foodName = (String) body.get("foodName");
    Double caloriesKcal = body.get("caloriesKcal") != null ? ((Number) body.get("caloriesKcal")).doubleValue() : null;
    Double proteinG = body.get("proteinG") != null ? ((Number) body.get("proteinG")).doubleValue() : null;
    Double carbsG = body.get("carbsG") != null ? ((Number) body.get("carbsG")).doubleValue() : null;
    Double fatG = body.get("fatG") != null ? ((Number) body.get("fatG")).doubleValue() : null;
    Double quantity = body.get("quantity") != null ? ((Number) body.get("quantity")).doubleValue() : null;
    String baseUnit = (String) body.get("baseUnit");
    return ResponseEntity.ok(
        logExtraFoodUseCase.execute(currentUser.getId(), foodName, caloriesKcal, proteinG,
            carbsG, fatG, quantity, baseUnit));
  }

  @DeleteMapping("/extra/{logId}")
  public ResponseEntity<MealConsumptionResponse> deleteExtra(
      @PathVariable UUID logId,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(deleteExtraFoodLogUseCase.execute(currentUser.getId(), logId));
  }

  @GetMapping("/student/{studentId}/adherence")
  public ResponseEntity<List<DailyAdherenceResponse>> getAdherence(
      @PathVariable UUID studentId,
      @RequestParam(defaultValue = "7") int days,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    ownershipValidator.assertProfessionalOwnsStudent(currentUser.getId(), currentUser.getRole(),
        studentId);
    return ResponseEntity.ok(getAdherenceUseCase.execute(studentId, Math.min(days, 30)));
  }
}
