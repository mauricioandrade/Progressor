package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.ProgressPhotoRepository;
import com.mauricioandrade.progressor.core.domain.photo.ProgressPhoto;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.ProgressPhotoEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataProgressPhotoRepository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
public class ProgressPhotoRepositoryAdapter implements ProgressPhotoRepository {

  private final SpringDataProgressPhotoRepository springRepo;

  public ProgressPhotoRepositoryAdapter(SpringDataProgressPhotoRepository springRepo) {
    this.springRepo = springRepo;
  }

  @Override
  public ProgressPhoto save(ProgressPhoto photo) {
    ProgressPhotoEntity entity = toEntity(photo);
    return toDomain(springRepo.save(entity));
  }

  @Override
  public List<ProgressPhoto> findByStudentId(UUID studentId) {
    return springRepo.findByStudentIdOrderByTakenAtDesc(studentId).stream()
        .map(this::toDomain)
        .toList();
  }

  @Override
  public Optional<ProgressPhoto> findById(UUID id) {
    return springRepo.findById(id).map(this::toDomain);
  }

  @Override
  @Transactional
  public void deleteByIdAndStudentId(UUID id, UUID studentId) {
    springRepo.deleteByIdAndStudentId(id, studentId);
  }

  private ProgressPhotoEntity toEntity(ProgressPhoto photo) {
    ProgressPhotoEntity e = new ProgressPhotoEntity();
    e.setId(photo.getId());
    e.setStudentId(photo.getStudentId());
    e.setPhotoData(photo.getPhotoData());
    e.setTakenAt(photo.getTakenAt());
    e.setDescription(photo.getDescription());
    e.setProfessionalFeedback(photo.getProfessionalFeedback());
    e.setProfessionalId(photo.getProfessionalId());
    e.setProfessionalName(photo.getProfessionalName());
    e.setProfessionalRole(photo.getProfessionalRole());
    e.setStudentNotes(photo.getStudentNotes());
    return e;
  }

  private ProgressPhoto toDomain(ProgressPhotoEntity e) {
    return new ProgressPhoto(e.getId(), e.getStudentId(), e.getPhotoData(), e.getTakenAt(),
        e.getDescription(), e.getProfessionalFeedback(), e.getProfessionalId(),
        e.getProfessionalName(), e.getProfessionalRole(), e.getStudentNotes());
  }
}
