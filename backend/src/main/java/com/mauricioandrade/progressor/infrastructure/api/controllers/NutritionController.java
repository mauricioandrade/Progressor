package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.CreateMealPlanRequest;
import com.mauricioandrade.progressor.core.application.dto.FoodItemResponse;
import com.mauricioandrade.progressor.core.application.dto.MealPlanResponse;
import com.mauricioandrade.progressor.core.application.dto.MealPlanSummaryResponse;
import com.mauricioandrade.progressor.core.application.dto.SetWaterGoalRequest;
import com.mauricioandrade.progressor.core.application.dto.UpdateMealPlanRequest;
import com.mauricioandrade.progressor.core.application.dto.UpdateWaterIntakeRequest;
import com.mauricioandrade.progressor.core.application.dto.WaterIntakeResponse;
import com.mauricioandrade.progressor.core.application.usecases.AddWaterIntakeUseCase;
import com.mauricioandrade.progressor.core.application.usecases.CreateMealPlanUseCase;
import com.mauricioandrade.progressor.core.application.usecases.DeleteMealPlanUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetMealPlanHistoryUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentMealPlanUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetWaterIntakeUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SearchFoodUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SetWaterGoalUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateMealPlanUseCase;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/nutrition")
public class NutritionController {

  private final CreateMealPlanUseCase createMealPlanUseCase;
  private final GetStudentMealPlanUseCase getStudentMealPlanUseCase;
  private final DeleteMealPlanUseCase deleteMealPlanUseCase;
  private final UpdateMealPlanUseCase updateMealPlanUseCase;
  private final GetMealPlanHistoryUseCase getMealPlanHistoryUseCase;
  private final SearchFoodUseCase searchFoodUseCase;
  private final GetWaterIntakeUseCase getWaterIntakeUseCase;
  private final AddWaterIntakeUseCase addWaterIntakeUseCase;
  private final SetWaterGoalUseCase setWaterGoalUseCase;

  public NutritionController(CreateMealPlanUseCase createMealPlanUseCase,
      GetStudentMealPlanUseCase getStudentMealPlanUseCase,
      DeleteMealPlanUseCase deleteMealPlanUseCase,
      UpdateMealPlanUseCase updateMealPlanUseCase,
      GetMealPlanHistoryUseCase getMealPlanHistoryUseCase,
      SearchFoodUseCase searchFoodUseCase,
      GetWaterIntakeUseCase getWaterIntakeUseCase,
      AddWaterIntakeUseCase addWaterIntakeUseCase,
      SetWaterGoalUseCase setWaterGoalUseCase) {
    this.createMealPlanUseCase = createMealPlanUseCase;
    this.getStudentMealPlanUseCase = getStudentMealPlanUseCase;
    this.deleteMealPlanUseCase = deleteMealPlanUseCase;
    this.updateMealPlanUseCase = updateMealPlanUseCase;
    this.getMealPlanHistoryUseCase = getMealPlanHistoryUseCase;
    this.searchFoodUseCase = searchFoodUseCase;
    this.getWaterIntakeUseCase = getWaterIntakeUseCase;
    this.addWaterIntakeUseCase = addWaterIntakeUseCase;
    this.setWaterGoalUseCase = setWaterGoalUseCase;
  }

  @PostMapping("/meal-plans")
  public ResponseEntity<Map<String, UUID>> createMealPlan(
      @Valid @RequestBody CreateMealPlanRequest request,
      @AuthenticationPrincipal UserEntity currentUser) {
    UUID id = createMealPlanUseCase.execute(request, currentUser.getId());
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
  }

  @DeleteMapping("/meal-plans/{id}")
  public ResponseEntity<Void> deleteMealPlan(@PathVariable UUID id) {
    deleteMealPlanUseCase.execute(id);
    return ResponseEntity.noContent().build();
  }

  @PutMapping("/meal-plans/{id}")
  public ResponseEntity<Void> updateMealPlan(@PathVariable UUID id,
      @Valid @RequestBody UpdateMealPlanRequest request) {
    updateMealPlanUseCase.execute(id, request);
    return ResponseEntity.noContent().build();
  }

  @GetMapping("/meal-plans/my")
  public ResponseEntity<MealPlanResponse> getMyMealPlan(
      @AuthenticationPrincipal UserEntity currentUser) {
    return getStudentMealPlanUseCase.execute(currentUser.getId())
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/meal-plans/student/{studentId}")
  public ResponseEntity<MealPlanResponse> getStudentMealPlan(@PathVariable UUID studentId) {
    return getStudentMealPlanUseCase.execute(studentId)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/meal-plans/history/{studentId}")
  public ResponseEntity<List<MealPlanSummaryResponse>> getMealPlanHistory(
      @PathVariable UUID studentId) {
    return ResponseEntity.ok(getMealPlanHistoryUseCase.execute(studentId));
  }

  @GetMapping("/foods/search")
  public ResponseEntity<List<FoodItemResponse>> searchFoods(@RequestParam String q) {
    return ResponseEntity.ok(searchFoodUseCase.execute(q));
  }

  @GetMapping("/water")
  public ResponseEntity<WaterIntakeResponse> getWaterIntake(
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(getWaterIntakeUseCase.execute(currentUser.getId()));
  }

  @PatchMapping("/water/intake")
  public ResponseEntity<WaterIntakeResponse> addWaterIntake(
      @Valid @RequestBody UpdateWaterIntakeRequest request,
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(addWaterIntakeUseCase.execute(currentUser.getId(), request.amount()));
  }

  @PatchMapping("/water/goal")
  public ResponseEntity<WaterIntakeResponse> setWaterGoal(
      @Valid @RequestBody SetWaterGoalRequest request,
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(setWaterGoalUseCase.execute(currentUser.getId(), request.goal()));
  }

  @GetMapping("/water/student/{studentId}")
  public ResponseEntity<WaterIntakeResponse> getStudentWaterIntake(@PathVariable UUID studentId) {
    return ResponseEntity.ok(getWaterIntakeUseCase.execute(studentId));
  }

  @PatchMapping("/water/goal/{studentId}")
  public ResponseEntity<WaterIntakeResponse> setWaterGoalForStudent(
      @PathVariable UUID studentId,
      @Valid @RequestBody SetWaterGoalRequest request) {
    return ResponseEntity.ok(setWaterGoalUseCase.execute(studentId, request.goal()));
  }
}
