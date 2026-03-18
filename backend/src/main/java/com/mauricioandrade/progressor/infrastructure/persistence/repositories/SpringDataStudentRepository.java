package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.StudentEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface SpringDataStudentRepository extends JpaRepository<StudentEntity, UUID> {

  List<StudentEntity> findByPersonalTrainerId(UUID personalTrainerId);

  @Query("SELECT s FROM StudentEntity s WHERE s.email = :email AND s.personalTrainerId IS NULL")
  Optional<StudentEntity> findUnassignedByEmail(String email);

  List<StudentEntity> findByNutritionistId(UUID nutritionistId);

  @Query("SELECT s FROM StudentEntity s WHERE s.email = :email AND s.nutritionistId IS NULL")
  Optional<StudentEntity> findUnassignedByEmailForNutritionist(String email);

  Optional<StudentEntity> findByEmail(String email);
}
