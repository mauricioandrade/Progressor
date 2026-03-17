package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.usecases.GenerateMealPlanReportUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GenerateProgressReportUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GenerateWorkoutSheetUseCase;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import java.util.Locale;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

  private final GenerateWorkoutSheetUseCase generateWorkoutSheetUseCase;
  private final GenerateProgressReportUseCase generateProgressReportUseCase;
  private final GenerateMealPlanReportUseCase generateMealPlanReportUseCase;

  public ReportController(GenerateWorkoutSheetUseCase generateWorkoutSheetUseCase,
      GenerateProgressReportUseCase generateProgressReportUseCase,
      GenerateMealPlanReportUseCase generateMealPlanReportUseCase) {
    this.generateWorkoutSheetUseCase = generateWorkoutSheetUseCase;
    this.generateProgressReportUseCase = generateProgressReportUseCase;
    this.generateMealPlanReportUseCase = generateMealPlanReportUseCase;
  }

  @GetMapping("/workouts/{studentId}")
  public ResponseEntity<byte[]> downloadWorkoutSheet(@PathVariable UUID studentId, Locale locale) {
    byte[] pdfBytes = generateWorkoutSheetUseCase.execute(studentId, locale);
    return pdfResponse(pdfBytes, "workout_sheet.pdf");
  }

  @GetMapping("/workouts/my")
  public ResponseEntity<byte[]> downloadMyWorkoutSheet(
      @AuthenticationPrincipal UserEntity currentUser, Locale locale) {
    byte[] pdfBytes = generateWorkoutSheetUseCase.execute(currentUser.getId(), locale);
    return pdfResponse(pdfBytes, "workout_sheet.pdf");
  }

  @GetMapping("/progress/{studentId}")
  public ResponseEntity<byte[]> downloadProgressReport(@PathVariable UUID studentId, Locale locale) {
    byte[] pdfBytes = generateProgressReportUseCase.execute(studentId, locale);
    return pdfResponse(pdfBytes, "progress_report.pdf");
  }

  @GetMapping("/progress/my")
  public ResponseEntity<byte[]> downloadMyProgressReport(
      @AuthenticationPrincipal UserEntity currentUser, Locale locale) {
    byte[] pdfBytes = generateProgressReportUseCase.execute(currentUser.getId(), locale);
    return pdfResponse(pdfBytes, "progress_report.pdf");
  }

  @GetMapping("/meal-plan/my")
  public ResponseEntity<byte[]> downloadMyMealPlan(
      @AuthenticationPrincipal UserEntity currentUser, Locale locale) {
    byte[] pdfBytes = generateMealPlanReportUseCase.execute(currentUser.getId(), locale);
    return pdfResponse(pdfBytes, "meal_plan.pdf");
  }

  private ResponseEntity<byte[]> pdfResponse(byte[] bytes, String filename) {
    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", filename);
    return ResponseEntity.ok().headers(headers).body(bytes);
  }
}
