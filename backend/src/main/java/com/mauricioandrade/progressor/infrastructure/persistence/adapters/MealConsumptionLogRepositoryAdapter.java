package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.dto.ExtraFoodLogResponse;
import com.mauricioandrade.progressor.core.application.ports.MealConsumptionLogRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.MealConsumptionLogEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataMealConsumptionLogRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class MealConsumptionLogRepositoryAdapter implements MealConsumptionLogRepository {

  private final SpringDataMealConsumptionLogRepository repository;

  public MealConsumptionLogRepositoryAdapter(SpringDataMealConsumptionLogRepository repository) {
    this.repository = repository;
  }

  @Override
  public boolean isConsumed(UUID studentId, UUID mealItemId, LocalDate date) {
    return repository.existsByStudentIdAndMealPlanItemIdAndLogDate(studentId, mealItemId, date);
  }

  @Override
  public void log(UUID studentId, UUID mealItemId, LocalDate date) {
    repository.save(new MealConsumptionLogEntity(studentId, mealItemId, date));
  }

  @Override
  @Transactional
  public void remove(UUID studentId, UUID mealItemId, LocalDate date) {
    repository.deleteByStudentIdAndMealPlanItemIdAndLogDate(studentId, mealItemId, date);
  }

  @Override
  public Set<UUID> findConsumedItemIds(UUID studentId, LocalDate date) {
    return repository.findPlanItemsByStudentIdAndLogDate(studentId, date).stream()
        .map(MealConsumptionLogEntity::getMealPlanItemId)
        .collect(Collectors.toSet());
  }

  @Override
  public UUID logExtra(UUID studentId, LocalDate date, String foodName, Double caloriesKcal,
      Double proteinG, Double carbsG, Double fatG, Double quantity, String baseUnit) {
    var entity = repository.save(new MealConsumptionLogEntity(studentId, date, foodName,
        caloriesKcal, proteinG, carbsG, fatG, quantity, baseUnit));
    return entity.getId();
  }

  @Override
  public List<ExtraFoodLogResponse> findExtraItems(UUID studentId, LocalDate date) {
    return repository.findExtraItemsByStudentIdAndLogDate(studentId, date).stream()
        .map(e -> new ExtraFoodLogResponse(e.getId(), e.getFoodName(), e.getCaloriesKcal(),
            e.getProteinG(), e.getCarbsG(), e.getFatG(), e.getQuantity(), e.getBaseUnit()))
        .toList();
  }

  @Override
  @Transactional
  public void deleteExtra(UUID logId) {
    repository.deleteById(logId);
  }

  @Override
  public Map<UUID, LocalDate> findLastLogDatesByStudentIds(Collection<UUID> studentIds) {
    if (studentIds.isEmpty()) return Map.of();
    return repository.findLastLogDatesByStudentIds(studentIds).stream()
        .collect(Collectors.toMap(
            row -> (UUID) row[0],
            row -> (LocalDate) row[1]
        ));
  }
}
