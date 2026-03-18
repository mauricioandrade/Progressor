package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.MealPlanEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SpringDataMealPlanRepository extends JpaRepository<MealPlanEntity, UUID> {

  Optional<MealPlanEntity> findTopByStudentIdOrderByCreatedAtDesc(UUID studentId);

  List<MealPlanEntity> findAllByStudentIdOrderByCreatedAtDesc(UUID studentId);
}
