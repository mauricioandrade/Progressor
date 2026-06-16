package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.ProfessionalDashboardStudentSummary;
import com.mauricioandrade.progressor.core.application.ports.CheckInRepository;
import com.mauricioandrade.progressor.core.application.ports.MealConsumptionLogRepository;
import com.mauricioandrade.progressor.core.application.ports.MealPlanRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutFeedbackRepository;
import com.mauricioandrade.progressor.core.domain.user.Student;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutFeedback;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetProfessionalDashboardUseCase {

  private final UserRepository userRepository;
  private final CheckInRepository checkInRepository;
  private final WorkoutFeedbackRepository feedbackRepository;
  private final MealConsumptionLogRepository consumptionRepository;
  private final MealPlanRepository mealPlanRepository;

  public GetProfessionalDashboardUseCase(UserRepository userRepository,
      CheckInRepository checkInRepository, WorkoutFeedbackRepository feedbackRepository,
      MealConsumptionLogRepository consumptionRepository, MealPlanRepository mealPlanRepository) {
    this.userRepository = userRepository;
    this.checkInRepository = checkInRepository;
    this.feedbackRepository = feedbackRepository;
    this.consumptionRepository = consumptionRepository;
    this.mealPlanRepository = mealPlanRepository;
  }

  public List<ProfessionalDashboardStudentSummary> executeForTrainer(UUID trainerId) {
    List<Student> students = userRepository.findStudentsByTrainerId(trainerId);
    if (students.isEmpty()) return List.of();

    List<UUID> studentIds = students.stream().map(Student::getId).toList();
    LocalDate since7Days = LocalDate.now().minusDays(7);

    Map<UUID, LocalDate> lastCheckIns = checkInRepository.findLastDatesByStudentIds(studentIds);
    Map<UUID, Long> checkInCounts = checkInRepository.findCountsByStudentIdsSince(studentIds, since7Days);

    Map<UUID, WorkoutFeedback> latestFeedbackByStudent = feedbackRepository
        .findByTrainerId(trainerId).stream()
        .collect(Collectors.toMap(WorkoutFeedback::getStudentId, f -> f, (a, b) -> a));

    return students.stream().map(s -> {
      WorkoutFeedback fb = latestFeedbackByStudent.get(s.getId());
      return new ProfessionalDashboardStudentSummary(
          s.getId(),
          s.getFirstName() + " " + s.getLastName(),
          s.getEmail().value(),
          lastCheckIns.get(s.getId()),
          checkInCounts.getOrDefault(s.getId(), 0L).intValue(),
          fb != null ? fb.getRating() : null,
          fb != null ? fb.getComment() : null,
          fb != null ? fb.getFeedbackDate() : null,
          null,
          null
      );
    }).toList();
  }

  public List<ProfessionalDashboardStudentSummary> executeForNutritionist(UUID nutritionistId) {
    List<Student> students = userRepository.findStudentsByNutritionistId(nutritionistId);
    if (students.isEmpty()) return List.of();

    List<UUID> studentIds = students.stream().map(Student::getId).toList();
    Map<UUID, LocalDate> lastFoodLogs = consumptionRepository.findLastLogDatesByStudentIds(studentIds);
    LocalDate today = LocalDate.now();

    return students.stream().map(s -> {
      int totalItems = mealPlanRepository.findByStudentId(s.getId())
          .map(plan -> plan.getItems().size())
          .orElse(0);
      Integer adherencePct = null;
      if (totalItems > 0) {
        int consumed = consumptionRepository.findConsumedItemIds(s.getId(), today).size();
        adherencePct = (consumed * 100) / totalItems;
      }
      return new ProfessionalDashboardStudentSummary(
          s.getId(),
          s.getFirstName() + " " + s.getLastName(),
          s.getEmail().value(),
          null,
          null,
          null,
          null,
          null,
          lastFoodLogs.get(s.getId()),
          adherencePct
      );
    }).toList();
  }
}
