package com.mauricioandrade.progressor.infrastructure.scheduler;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutExerciseEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataStudentRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutRepository;
import com.mauricioandrade.progressor.infrastructure.push.ExpoPushService;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class WorkoutReminderScheduler {

  private static final Logger log = LoggerFactory.getLogger(WorkoutReminderScheduler.class);
  private static final int MAX_NAMES_IN_BODY = 4;

  private final SpringDataStudentRepository studentRepository;
  private final SpringDataWorkoutRepository workoutRepository;
  private final ExpoPushService pushService;

  public WorkoutReminderScheduler(SpringDataStudentRepository studentRepository,
      SpringDataWorkoutRepository workoutRepository,
      ExpoPushService pushService) {
    this.studentRepository = studentRepository;
    this.workoutRepository = workoutRepository;
    this.pushService = pushService;
  }

  // Runs every day at 09:00
  @Scheduled(cron = "0 0 9 * * *")
  public void sendWorkoutReminders() {
    String todayCode = LocalDate.now().getDayOfWeek().name().substring(0, 3);
    log.info("Enviando lembretes de treino para o dia {} ...", todayCode);

    var students = studentRepository.findStudentsWithPersonalTrainer();
    if (students.isEmpty()) return;

    for (var student : students) {
      if (student.getPushToken() == null) continue;

      List<WorkoutExerciseEntity> todayExercises = workoutRepository
          .findByStudentIdAndScheduledDayContaining(student.getId(), todayCode);
      if (todayExercises.isEmpty()) continue;

      String body = buildBody(todayExercises);
      log.info("Lembrete para {} — {} exercício(s)", student.getFirstName(), todayExercises.size());
      pushService.send(student.getPushToken(), "Hora do treino! 💪", body);
    }
  }

  private String buildBody(List<WorkoutExerciseEntity> exercises) {
    List<String> names = exercises.stream()
        .map(WorkoutExerciseEntity::getName)
        .limit(MAX_NAMES_IN_BODY)
        .toList();
    String joined = String.join(", ", names);
    if (exercises.size() > MAX_NAMES_IN_BODY) {
      joined += " e mais " + (exercises.size() - MAX_NAMES_IN_BODY) + "...";
    }
    return exercises.size() + " exercício(s) hoje: " + joined;
  }
}
