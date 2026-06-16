package com.mauricioandrade.progressor.infrastructure.scheduler;

import com.mauricioandrade.progressor.infrastructure.email.EmailService;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.StudentEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataCheckInRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataStudentRepository;
import com.mauricioandrade.progressor.infrastructure.push.ExpoPushService;
import java.time.LocalDate;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class InactivityAlertScheduler {

  private static final Logger log = LoggerFactory.getLogger(InactivityAlertScheduler.class);
  private static final int INACTIVITY_DAYS = 3;

  private final SpringDataStudentRepository studentRepository;
  private final SpringDataCheckInRepository checkInRepository;
  private final EmailService emailService;
  private final ExpoPushService pushService;

  public InactivityAlertScheduler(SpringDataStudentRepository studentRepository,
      SpringDataCheckInRepository checkInRepository,
      EmailService emailService,
      ExpoPushService pushService) {
    this.studentRepository = studentRepository;
    this.checkInRepository = checkInRepository;
    this.emailService = emailService;
    this.pushService = pushService;
  }

  // Runs every day at 08:00
  @Scheduled(cron = "0 0 8 * * *")
  public void alertInactiveStudents() {
    LocalDate cutoff = LocalDate.now().minusDays(INACTIVITY_DAYS);
    log.info("Verificando alunos inativos (sem check-in desde {})...", cutoff);

    var students = studentRepository.findStudentsWithPersonalTrainer();
    if (students.isEmpty()) return;

    var studentIds = students.stream().map(StudentEntity::getId).toList();
    Map<UUID, LocalDate> lastCheckInByStudent = checkInRepository
        .findLastDatesByStudentIds(studentIds).stream()
        .collect(Collectors.toMap(row -> (UUID) row[0], row -> (LocalDate) row[1]));

    for (StudentEntity student : students) {
      LocalDate lastDate = lastCheckInByStudent.get(student.getId());
      boolean inactive = lastDate == null || !lastDate.isAfter(cutoff);

      if (inactive) {
        log.info("Aluno inativo: {} <{}>", student.getFirstName(), student.getEmail());
        emailService.sendInactivityAlert(student.getEmail(), student.getFirstName());
        if (student.getPushToken() != null) {
          pushService.send(student.getPushToken(), "Progressor",
              "Você está há " + INACTIVITY_DAYS + " dias sem treinar. Bora voltar! 💪");
        }
      }
    }
  }
}
