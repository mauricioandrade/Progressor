package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.MealPlanEntity;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataMealPlanRepository extends JpaRepository<MealPlanEntity, UUID> {

  Optional<MealPlanEntity> findTopByStudentIdOrderByCreatedAtDesc(UUID studentId);

  List<MealPlanEntity> findAllByStudentIdOrderByCreatedAtDesc(UUID studentId);

  @Query(value = """
      SELECT mp.student_id, COUNT(mi.id) AS item_count
      FROM meal_plans mp
      JOIN meal_items mi ON mi.meal_plan_id = mp.id
      WHERE mp.student_id IN :studentIds
        AND mp.created_at = (
          SELECT MAX(mp2.created_at) FROM meal_plans mp2 WHERE mp2.student_id = mp.student_id
        )
      GROUP BY mp.student_id
      """, nativeQuery = true)
  List<Object[]> findLatestItemCountsByStudentIds(@Param("studentIds") Collection<UUID> studentIds);
}
