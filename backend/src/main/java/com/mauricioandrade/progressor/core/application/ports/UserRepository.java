package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.common.Email;
import com.mauricioandrade.progressor.core.domain.user.Student;
import com.mauricioandrade.progressor.core.domain.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {

  void save(User user);

  void update(User user);

  boolean existsByEmail(Email email);

  boolean existsById(UUID id);

  Optional<User> findById(UUID id);

  Optional<Student> findStudentById(UUID id);

  Optional<Student> findStudentByEmail(Email email);

  List<Student> findStudentsByTrainerId(UUID trainerId);

  Optional<Student> findUnassignedStudentByEmail(Email email);

  List<Student> findStudentsByNutritionistId(UUID nutritionistId);

  Optional<Student> findStudentByEmailForNutritionist(Email email);

  void updateAvatar(UUID userId, byte[] avatar);

  void updateWeightGoal(UUID studentId, Double goal);

  java.util.Optional<Double> findWeightGoalByStudentId(UUID studentId);

  Optional<UUID> findIdByEmail(String email);

  Optional<UUID> findIdByValidResetToken(String token);

  void saveResetToken(UUID userId, String token, LocalDateTime expiry);

  void updatePassword(UUID userId, String encodedPassword);

  void clearResetToken(UUID userId);
}
