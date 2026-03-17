package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.ProgressPhotoRepository;
import java.util.UUID;

public class DeleteProgressPhotoUseCase {

  private final ProgressPhotoRepository repository;

  public DeleteProgressPhotoUseCase(ProgressPhotoRepository repository) {
    this.repository = repository;
  }

  public void execute(UUID photoId, UUID studentId) {
    repository.deleteByIdAndStudentId(photoId, studentId);
  }
}
