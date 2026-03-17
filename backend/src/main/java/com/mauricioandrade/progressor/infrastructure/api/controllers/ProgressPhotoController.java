package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.ProgressPhotoResponse;
import com.mauricioandrade.progressor.core.application.usecases.AddProfessionalFeedbackUseCase;
import com.mauricioandrade.progressor.core.application.usecases.DeleteProfessionalFeedbackUseCase;
import com.mauricioandrade.progressor.core.application.usecases.DeleteProgressPhotoUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetProgressPhotosUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateStudentNotesUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UploadProgressPhotoUseCase;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/progress-photos")
public class ProgressPhotoController {

  private final UploadProgressPhotoUseCase uploadUseCase;
  private final GetProgressPhotosUseCase getUseCase;
  private final DeleteProgressPhotoUseCase deleteUseCase;
  private final AddProfessionalFeedbackUseCase feedbackUseCase;
  private final DeleteProfessionalFeedbackUseCase deleteFeedbackUseCase;
  private final UpdateStudentNotesUseCase studentNotesUseCase;

  public ProgressPhotoController(UploadProgressPhotoUseCase uploadUseCase,
      GetProgressPhotosUseCase getUseCase, DeleteProgressPhotoUseCase deleteUseCase,
      AddProfessionalFeedbackUseCase feedbackUseCase,
      DeleteProfessionalFeedbackUseCase deleteFeedbackUseCase,
      UpdateStudentNotesUseCase studentNotesUseCase) {
    this.uploadUseCase = uploadUseCase;
    this.getUseCase = getUseCase;
    this.deleteUseCase = deleteUseCase;
    this.feedbackUseCase = feedbackUseCase;
    this.deleteFeedbackUseCase = deleteFeedbackUseCase;
    this.studentNotesUseCase = studentNotesUseCase;
  }

  @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  public ResponseEntity<ProgressPhotoResponse> upload(
      @RequestParam("file") MultipartFile file,
      @RequestParam(value = "description", defaultValue = "") String description,
      @RequestParam(value = "takenAt", required = false)
      @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate takenAt,
      @AuthenticationPrincipal UserEntity currentUser) throws Exception {
    ProgressPhotoResponse response = uploadUseCase.execute(currentUser.getId(), file.getBytes(),
        takenAt, description);
    return ResponseEntity.ok(response);
  }

  @GetMapping("/my")
  public ResponseEntity<List<ProgressPhotoResponse>> getMyPhotos(
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(getUseCase.execute(currentUser.getId()));
  }

  @GetMapping("/student/{studentId}")
  public ResponseEntity<List<ProgressPhotoResponse>> getStudentPhotos(
      @PathVariable UUID studentId,
      @AuthenticationPrincipal UserEntity currentUser) {
    return ResponseEntity.ok(getUseCase.execute(studentId));
  }

  @PatchMapping("/{id}/feedback")
  public ResponseEntity<ProgressPhotoResponse> addFeedback(
      @PathVariable UUID id,
      @RequestBody Map<String, String> body,
      @AuthenticationPrincipal UserEntity currentUser) {
    String professionalName = currentUser.getFirstName() + " " + currentUser.getLastName();
    String professionalRole = currentUser.getAuthorities().iterator().next()
        .getAuthority().replace("ROLE_", "");
    String feedback = body.getOrDefault("feedback", "").trim();
    if (feedback.isEmpty()) {
      throw new IllegalArgumentException("Feedback must not be empty");
    }
    ProgressPhotoResponse response = feedbackUseCase.execute(id, currentUser.getId(),
        professionalName, professionalRole, feedback);
    return ResponseEntity.ok(response);
  }

  @PatchMapping("/{id}/student-notes")
  public ResponseEntity<ProgressPhotoResponse> updateStudentNotes(
      @PathVariable UUID id,
      @RequestBody Map<String, String> body,
      @AuthenticationPrincipal UserEntity currentUser) {
    String notes = body.getOrDefault("notes", "").trim();
    ProgressPhotoResponse response = studentNotesUseCase.execute(id, currentUser.getId(), notes);
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}/feedback")
  public ResponseEntity<ProgressPhotoResponse> deleteFeedback(
      @PathVariable UUID id,
      @AuthenticationPrincipal UserEntity currentUser) {
    ProgressPhotoResponse response = deleteFeedbackUseCase.execute(id, currentUser.getId());
    return ResponseEntity.ok(response);
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> delete(@PathVariable UUID id,
      @AuthenticationPrincipal UserEntity currentUser) {
    deleteUseCase.execute(id, currentUser.getId());
    return ResponseEntity.noContent().build();
  }
}
