package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.MealConsumptionLogEntity;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataMealConsumptionLogRepository
    extends JpaRepository<MealConsumptionLogEntity, UUID> {

  boolean existsByStudentIdAndMealPlanItemIdAndLogDate(UUID studentId, UUID mealPlanItemId,
      LocalDate logDate);

  void deleteByStudentIdAndMealPlanItemIdAndLogDate(UUID studentId, UUID mealPlanItemId,
      LocalDate logDate);

  @Query("SELECT l FROM MealConsumptionLogEntity l WHERE l.studentId = :studentId AND l.logDate = :logDate AND l.mealPlanItemId IS NOT NULL")
  List<MealConsumptionLogEntity> findPlanItemsByStudentIdAndLogDate(@Param("studentId") UUID studentId, @Param("logDate") LocalDate logDate);

  @Query("SELECT l FROM MealConsumptionLogEntity l WHERE l.studentId = :studentId AND l.logDate = :logDate AND l.mealPlanItemId IS NULL")
  List<MealConsumptionLogEntity> findExtraItemsByStudentIdAndLogDate(@Param("studentId") UUID studentId, @Param("logDate") LocalDate logDate);

  @Query("SELECT DISTINCT l.studentId FROM MealConsumptionLogEntity l WHERE l.logDate >= :cutoff")
  Set<UUID> findStudentIdsWithLogsAfter(@Param("cutoff") LocalDate cutoff);

  @Query("SELECT l.studentId, MAX(l.logDate) FROM MealConsumptionLogEntity l WHERE l.studentId IN :studentIds GROUP BY l.studentId")
  List<Object[]> findLastLogDatesByStudentIds(@Param("studentIds") Collection<UUID> studentIds);
}
