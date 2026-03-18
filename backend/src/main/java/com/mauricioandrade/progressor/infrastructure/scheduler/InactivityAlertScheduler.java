package com.mauricioandrade.progressor.infrastructure.scheduler;

import com.mauricioandrade.progressor.infrastructure.email.EmailService;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.StudentEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutCheckInEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataCheckInRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataStudentRepository;
import java.time.LocalDate;
import java.util.Optional;
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

  public InactivityAlertScheduler(SpringDataStudentRepository studentRepository,
      SpringDataCheckInRepository checkInRepository,
      EmailService emailService) {
    this.studentRepository = studentRepository;
    this.checkInRepository = checkInRepository;
    this.emailService = emailService;
  }

  // Runs every day at 08:00
  @Scheduled(cron = "0 0 8 * * *")
  public void alertInactiveStudents() {
    LocalDate cutoff = LocalDate.now().minusDays(INACTIVITY_DAYS);
    log.info("Verificando alunos inativos (sem check-in desde {})...", cutoff);

    for (StudentEntity student : studentRepository.findAll()) {
      Optional<WorkoutCheckInEntity> lastCheckIn =
          checkInRepository.findTopByStudentIdOrderByDateDesc(student.getId());

      boolean inactive = lastCheckIn
          .map(c -> c.getDate().isBefore(cutoff))
          .orElse(true); // never checked in = inactive

      if (inactive) {
        log.info("Aluno inativo: {} <{}>", student.getFirstName(), student.getEmail());
        emailService.sendInactivityAlert(student.getEmail(), student.getFirstName());
      }
    }
  }
}
