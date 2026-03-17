package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.photo.ProgressPhoto;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProgressPhotoRepository {

  ProgressPhoto save(ProgressPhoto photo);

  List<ProgressPhoto> findByStudentId(UUID studentId);

  Optional<ProgressPhoto> findById(UUID id);

  void deleteByIdAndStudentId(UUID id, UUID studentId);
}
