package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ProgressPhotoResponse;
import com.mauricioandrade.progressor.core.application.ports.ProgressPhotoRepository;
import com.mauricioandrade.progressor.core.domain.photo.ProgressPhoto;
import java.time.LocalDate;
import java.util.Base64;
import java.util.UUID;

public class UploadProgressPhotoUseCase {

  private final ProgressPhotoRepository repository;

  public UploadProgressPhotoUseCase(ProgressPhotoRepository repository) {
    this.repository = repository;
  }

  public ProgressPhotoResponse execute(UUID studentId, byte[] photoData, LocalDate takenAt,
      String description) {
    if (photoData == null || photoData.length == 0) {
      throw new IllegalArgumentException("Photo data must not be empty");
    }
    if (photoData.length > 5 * 1024 * 1024) {
      throw new IllegalArgumentException("Photo must be smaller than 5 MB");
    }
    ProgressPhoto photo = new ProgressPhoto(UUID.randomUUID(), studentId, photoData,
        takenAt != null ? takenAt : LocalDate.now(), description != null ? description : "");
    ProgressPhoto saved = repository.save(photo);
    return new ProgressPhotoResponse(saved.getId(), saved.getTakenAt(), saved.getDescription(),
        Base64.getEncoder().encodeToString(saved.getPhotoData()),
        null, null, null, null, null);
  }
}
