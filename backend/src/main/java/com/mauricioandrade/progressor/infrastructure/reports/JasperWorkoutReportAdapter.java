package com.mauricioandrade.progressor.infrastructure.reports;

import com.mauricioandrade.progressor.core.application.ports.WorkoutReportGenerator;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutExerciseEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataStudentRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutRepository;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRParameter;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.springframework.stereotype.Component;

@Component
public class JasperWorkoutReportAdapter implements WorkoutReportGenerator {

  private final SpringDataWorkoutRepository workoutRepository;
  private final SpringDataStudentRepository studentRepository;
  private final SpringDataUserRepository userRepository;

  public JasperWorkoutReportAdapter(SpringDataWorkoutRepository workoutRepository,
      SpringDataStudentRepository studentRepository,
      SpringDataUserRepository userRepository) {
    this.workoutRepository = workoutRepository;
    this.studentRepository = studentRepository;
    this.userRepository = userRepository;
  }

  @Override
  public byte[] generateWorkoutSheet(UUID studentId, Locale locale) {
    try {
      List<WorkoutExerciseEntity> exercises = workoutRepository.findByStudentId(studentId);

      if (exercises.isEmpty()) {
        throw new IllegalArgumentException("No exercises found for this student");
      }

      String studentName = studentRepository.findById(studentId)
          .map(s -> s.getFirstName() + " " + s.getLastName())
          .orElse("—");

      String professionalName = studentRepository.findById(studentId)
          .filter(s -> s.getPersonalTrainerId() != null)
          .flatMap(s -> userRepository.findById(s.getPersonalTrainerId()))
          .map(u -> u.getFirstName() + " " + u.getLastName())
          .orElse("—");

      double totalTonnage = exercises.stream()
          .filter(e -> e.getTonnage() != null)
          .mapToDouble(WorkoutExerciseEntity::getTonnage)
          .sum();

      InputStream reportStream = getClass().getResourceAsStream("/reports/workout_sheet.jrxml");
      if (reportStream == null) {
        throw new RuntimeException("Report template not found");
      }

      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(exercises);

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put(JRParameter.REPORT_LOCALE, locale);
      parameters.put("studentName", studentName);
      parameters.put("professionalName", professionalName);
      parameters.put("issuedOn", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
      parameters.put("totalTonnage", totalTonnage > 0 ? totalTonnage : null);

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

      return JasperExportManager.exportReportToPdf(jasperPrint);

    } catch (JRException e) {
      throw new RuntimeException("Error generating PDF report", e);
    }
  }
}
