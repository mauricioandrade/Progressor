package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.application.dto.ExtraFoodLogResponse;
import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public interface MealConsumptionLogRepository {

  boolean isConsumed(UUID studentId, UUID mealItemId, LocalDate date);

  void log(UUID studentId, UUID mealItemId, LocalDate date);

  void remove(UUID studentId, UUID mealItemId, LocalDate date);

  Set<UUID> findConsumedItemIds(UUID studentId, LocalDate date);

  UUID logExtra(UUID studentId, LocalDate date, String foodName, Double caloriesKcal,
      Double proteinG, Double carbsG, Double fatG, Double quantity, String baseUnit);

  List<ExtraFoodLogResponse> findExtraItems(UUID studentId, LocalDate date);

  void deleteExtra(UUID logId);

  Map<UUID, LocalDate> findLastLogDatesByStudentIds(Collection<UUID> studentIds);

  Map<UUID, Integer> findConsumedCountsByStudentIds(Collection<UUID> studentIds, LocalDate date);
}
