package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.usecases.GenerateWorkoutSheetUseCase;
import java.util.UUID;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {

  private final GenerateWorkoutSheetUseCase generateWorkoutSheetUseCase;

  public ReportController(GenerateWorkoutSheetUseCase generateWorkoutSheetUseCase) {
    this.generateWorkoutSheetUseCase = generateWorkoutSheetUseCase;
  }

  @GetMapping("/workouts/{studentId}")
  public ResponseEntity<byte[]> downloadWorkoutSheet(@PathVariable UUID studentId) {
    byte[] pdfBytes = generateWorkoutSheetUseCase.execute(studentId);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_PDF);
    headers.setContentDispositionFormData("attachment", "ficha_treino.pdf");

    return ResponseEntity.ok().headers(headers).body(pdfBytes);
  }
}