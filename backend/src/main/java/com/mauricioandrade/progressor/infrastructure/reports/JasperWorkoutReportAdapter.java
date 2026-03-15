package com.mauricioandrade.progressor.infrastructure.reports;

import com.mauricioandrade.progressor.core.application.ports.WorkoutReportGenerator;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.WorkoutExerciseEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutRepository;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.sf.jasperreports.engine.JRException;
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

  public JasperWorkoutReportAdapter(SpringDataWorkoutRepository workoutRepository) {
    this.workoutRepository = workoutRepository;
  }

  @Override
  public byte[] generateWorkoutSheet(UUID studentId) {
    try {
      List<WorkoutExerciseEntity> exercises = workoutRepository.findByStudentId(studentId);

      if (exercises.isEmpty()) {
        throw new IllegalArgumentException("No exercises found for this student");
      }

      InputStream reportStream = getClass().getResourceAsStream("/reports/workout_sheet.jrxml");
      if (reportStream == null) {
        throw new RuntimeException("Report template not found");
      }

      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(exercises);

      HashMap<String, Object> parameters = new HashMap<>();

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

      return JasperExportManager.exportReportToPdf(jasperPrint);

    } catch (JRException e) {
      throw new RuntimeException("Error generating PDF report", e);
    }
  }
}