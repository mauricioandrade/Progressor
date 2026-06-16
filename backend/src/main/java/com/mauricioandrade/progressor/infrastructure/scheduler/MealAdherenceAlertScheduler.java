package com.mauricioandrade.progressor.infrastructure.scheduler;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.StudentEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataMealConsumptionLogRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataMealPlanRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataStudentRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import com.mauricioandrade.progressor.infrastructure.push.ExpoPushService;
import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class MealAdherenceAlertScheduler {

  private static final Logger log = LoggerFactory.getLogger(MealAdherenceAlertScheduler.class);
  private static final int INACTIVITY_DAYS = 2;

  private final SpringDataStudentRepository studentRepository;
  private final SpringDataMealConsumptionLogRepository consumptionLogRepository;
  private final SpringDataMealPlanRepository mealPlanRepository;
  private final SpringDataUserRepository userRepository;
  private final ExpoPushService pushService;

  public MealAdherenceAlertScheduler(SpringDataStudentRepository studentRepository,
      SpringDataMealConsumptionLogRepository consumptionLogRepository,
      SpringDataMealPlanRepository mealPlanRepository,
      SpringDataUserRepository userRepository,
      ExpoPushService pushService) {
    this.studentRepository = studentRepository;
    this.consumptionLogRepository = consumptionLogRepository;
    this.mealPlanRepository = mealPlanRepository;
    this.userRepository = userRepository;
    this.pushService = pushService;
  }

  // Runs every day at 09:00
  @Scheduled(cron = "0 0 9 * * *")
  public void alertNutritionistsForLowAdherence() {
    LocalDate cutoff = LocalDate.now().minusDays(INACTIVITY_DAYS);
    log.info("Verificando aderência alimentar (sem registro desde {})...", cutoff);

    var students = studentRepository.findStudentsWithNutritionist();
    if (students.isEmpty()) return;

    Set<UUID> activeStudentIds = consumptionLogRepository.findStudentIdsWithLogsAfter(cutoff);

    for (StudentEntity student : students) {
      if (activeStudentIds.contains(student.getId())) continue;
      if (mealPlanRepository.findTopByStudentIdOrderByCreatedAtDesc(student.getId()).isEmpty()) continue;

      log.info("Aluno sem registro alimentar: {} <{}>", student.getFirstName(), student.getEmail());

      UUID nutritionistId = student.getNutritionistId();
      userRepository.findById(nutritionistId).map(UserEntity::getPushToken).ifPresent(token ->
          pushService.send(token, "Progressor",
              student.getFirstName() + " não registra alimentação há " + INACTIVITY_DAYS + "+ dias 📋")
      );
    }
  }
}
