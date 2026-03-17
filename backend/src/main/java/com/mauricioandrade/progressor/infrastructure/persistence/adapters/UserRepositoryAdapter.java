package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.common.Email;
import com.mauricioandrade.progressor.infrastructure.persistence.converters.SearchableEncryptedStringConverter;
import com.mauricioandrade.progressor.core.domain.user.Student;
import com.mauricioandrade.progressor.core.domain.user.User;
import com.mauricioandrade.progressor.infrastructure.persistence.mappers.UserMapper;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataStudentRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserRepository {

  private final SpringDataUserRepository springDataRepository;
  private final SpringDataStudentRepository springDataStudentRepository;

  public UserRepositoryAdapter(SpringDataUserRepository springDataRepository,
      SpringDataStudentRepository springDataStudentRepository) {
    this.springDataRepository = springDataRepository;
    this.springDataStudentRepository = springDataStudentRepository;
  }

  @Override
  public void save(User user) {
    springDataRepository.save(UserMapper.toEntity(user));
  }

  @Override
  public void update(User user) {
    springDataRepository.save(UserMapper.toEntity(user));
  }

  @Override
  public boolean existsByEmail(Email email) {
    String enc = SearchableEncryptedStringConverter.encrypt(email.value());
    return springDataRepository.existsByEmail(enc)
        || springDataRepository.existsByEmail(email.value());
  }

  @Override
  public boolean existsById(UUID id) {
    return springDataRepository.existsById(id);
  }

  @Override
  public Optional<Student> findStudentById(UUID id) {
    return springDataStudentRepository.findById(id).map(entity -> {
      Student student = new Student(entity.getId(), entity.getFirstName(), entity.getLastName(),
          new Email(entity.getEmail()), entity.getPassword(), entity.getBirthDate());
      if (entity.getPersonalTrainerId() != null) {
        student.assignPersonalTrainer(entity.getPersonalTrainerId());
      }
      if (entity.getNutritionistId() != null) {
        student.assignNutritionist(entity.getNutritionistId());
      }
      student.setDailyWaterGoal(entity.getDailyWaterGoal() != null ? entity.getDailyWaterGoal() : 0);
      student.setCurrentWaterIntake(entity.getCurrentWaterIntake() != null ? entity.getCurrentWaterIntake() : 0);
      return student;
    });
  }

  @Override
  public List<Student> findStudentsByTrainerId(UUID trainerId) {
    return springDataStudentRepository.findByPersonalTrainerId(trainerId).stream().map(entity -> {
      Student student = new Student(entity.getId(), entity.getFirstName(), entity.getLastName(),
          new Email(entity.getEmail()), entity.getPassword(), entity.getBirthDate());
      student.assignPersonalTrainer(entity.getPersonalTrainerId());
      if (entity.getNutritionistId() != null) {
        student.assignNutritionist(entity.getNutritionistId());
      }
      return student;
    }).toList();
  }

  @Override
  public Optional<Student> findUnassignedStudentByEmail(Email email) {
    String enc = SearchableEncryptedStringConverter.encrypt(email.value());
    return springDataStudentRepository.findUnassignedByEmail(enc)
        .or(() -> springDataStudentRepository.findUnassignedByEmail(email.value()))
        .map(entity -> {
      Student student = new Student(entity.getId(), entity.getFirstName(), entity.getLastName(),
          new Email(entity.getEmail()), entity.getPassword(), entity.getBirthDate());
      return student;
    });
  }

  @Override
  public List<Student> findStudentsByNutritionistId(UUID nutritionistId) {
    return springDataStudentRepository.findByNutritionistId(nutritionistId).stream().map(entity -> {
      Student student = new Student(entity.getId(), entity.getFirstName(), entity.getLastName(),
          new Email(entity.getEmail()), entity.getPassword(), entity.getBirthDate());
      if (entity.getPersonalTrainerId() != null) student.assignPersonalTrainer(entity.getPersonalTrainerId());
      if (entity.getNutritionistId() != null) student.assignNutritionist(entity.getNutritionistId());
      return student;
    }).toList();
  }

  @Override
  public Optional<Student> findStudentByEmailForNutritionist(Email email) {
    String enc = SearchableEncryptedStringConverter.encrypt(email.value());
    return springDataStudentRepository.findUnassignedByEmailForNutritionist(enc)
        .or(() -> springDataStudentRepository.findUnassignedByEmailForNutritionist(email.value()))
        .map(entity -> new Student(entity.getId(), entity.getFirstName(), entity.getLastName(),
            new Email(entity.getEmail()), entity.getPassword(), entity.getBirthDate()));
  }

  @Override
  public void updateAvatar(UUID userId, byte[] avatar) {
    springDataRepository.findById(userId).ifPresent(entity -> {
      entity.setAvatar(avatar);
      springDataRepository.save(entity);
    });
  }

  @Override
  public Optional<UUID> findIdByEmail(String email) {
    String enc = SearchableEncryptedStringConverter.encrypt(email);
    return springDataRepository.findByEmail(enc)
        .or(() -> springDataRepository.findByEmail(email))
        .map(e -> e.getId());
  }

  @Override
  public Optional<UUID> findIdByValidResetToken(String token) {
    return springDataRepository.findByResetToken(token)
        .filter(e -> e.getResetTokenExpiry() != null && e.getResetTokenExpiry().isAfter(LocalDateTime.now()))
        .map(e -> e.getId());
  }

  @Override
  public void saveResetToken(UUID userId, String token, LocalDateTime expiry) {
    springDataRepository.findById(userId).ifPresent(entity -> {
      entity.setResetToken(token);
      entity.setResetTokenExpiry(expiry);
      springDataRepository.save(entity);
    });
  }

  @Override
  public void updatePassword(UUID userId, String encodedPassword) {
    springDataRepository.findById(userId).ifPresent(entity -> {
      entity.setPassword(encodedPassword);
      springDataRepository.save(entity);
    });
  }

  @Override
  public void clearResetToken(UUID userId) {
    springDataRepository.findById(userId).ifPresent(entity -> {
      entity.setResetToken(null);
      entity.setResetTokenExpiry(null);
      springDataRepository.save(entity);
    });
  }
}
