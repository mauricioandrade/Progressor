package com.mauricioandrade.progressor.infrastructure.reports;

import com.mauricioandrade.progressor.core.application.ports.ProgressReportGenerator;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.BodyMeasurementEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataMeasurementRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataStudentRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
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
public class JasperProgressReportAdapter implements ProgressReportGenerator {

  private final SpringDataMeasurementRepository measurementRepository;
  private final SpringDataStudentRepository studentRepository;
  private final SpringDataUserRepository userRepository;

  public JasperProgressReportAdapter(SpringDataMeasurementRepository measurementRepository,
      SpringDataStudentRepository studentRepository,
      SpringDataUserRepository userRepository) {
    this.measurementRepository = measurementRepository;
    this.studentRepository = studentRepository;
    this.userRepository = userRepository;
  }

  @Override
  public byte[] generate(UUID studentId, Locale locale) {
    try {
      List<BodyMeasurementEntity> measurements = measurementRepository.findByStudentId(studentId);

      if (measurements.isEmpty()) {
        throw new IllegalArgumentException("No measurements found for this student");
      }

      BodyMeasurementEntity first = measurements.get(0);
      BodyMeasurementEntity last = measurements.get(measurements.size() - 1);

      ResourceBundle bundle = ResourceBundle.getBundle("reports.report_labels", locale);
      List<MeasurementRow> rows = buildRows(first, last, bundle);

      String studentName = studentRepository.findById(studentId)
          .map(s -> s.getFirstName() + " " + s.getLastName())
          .orElse("—");

      String professionalName = studentRepository.findById(studentId)
          .filter(s -> s.getPersonalTrainerId() != null)
          .flatMap(s -> userRepository.findById(s.getPersonalTrainerId()))
          .map(u -> u.getFirstName() + " " + u.getLastName())
          .orElse("—");

      InputStream reportStream = getClass().getResourceAsStream("/reports/progress_report.jrxml");
      if (reportStream == null) {
        throw new RuntimeException("Progress report template not found");
      }

      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(rows);

      DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy");
      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put(JRParameter.REPORT_LOCALE, locale);
      parameters.put("studentName", studentName);
      parameters.put("professionalName", professionalName);
      parameters.put("issuedOn", LocalDate.now().format(fmt));
      parameters.put("initialDate", first.getRecordedAt() != null ? first.getRecordedAt().format(fmt) : "—");
      parameters.put("currentDate", last.getRecordedAt() != null ? last.getRecordedAt().format(fmt) : "—");

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

      return JasperExportManager.exportReportToPdf(jasperPrint);

    } catch (JRException e) {
      throw new RuntimeException("Error generating progress report", e);
    }
  }

  private List<MeasurementRow> buildRows(BodyMeasurementEntity first, BodyMeasurementEntity last,
      ResourceBundle bundle) {
    List<MeasurementRow> rows = new ArrayList<>();

    rows.add(row(bundle.getString("report.metric.weight"), first.getWeight(), last.getWeight(), false));
    rows.add(row(bundle.getString("report.metric.bodyfat"), first.getBodyFatPercentage(), last.getBodyFatPercentage(), false));
    rows.add(row(bundle.getString("report.metric.chest"), first.getChest(), last.getChest(), true));
    rows.add(row(bundle.getString("report.metric.waist"), first.getWaist(), last.getWaist(), false));
    rows.add(row(bundle.getString("report.metric.abdomen"), first.getAbdomen(), last.getAbdomen(), false));
    rows.add(row(bundle.getString("report.metric.hips"), first.getHips(), last.getHips(), false));
    rows.add(row(bundle.getString("report.metric.rightBicep"), first.getRightBicep(), last.getRightBicep(), true));
    rows.add(row(bundle.getString("report.metric.leftBicep"), first.getLeftBicep(), last.getLeftBicep(), true));
    rows.add(row(bundle.getString("report.metric.rightThigh"), first.getRightThigh(), last.getRightThigh(), true));
    rows.add(row(bundle.getString("report.metric.leftThigh"), first.getLeftThigh(), last.getLeftThigh(), true));
    rows.add(row(bundle.getString("report.metric.rightCalf"), first.getRightCalf(), last.getRightCalf(), true));
    rows.add(row(bundle.getString("report.metric.leftCalf"), first.getLeftCalf(), last.getLeftCalf(), true));

    return rows;
  }

  private MeasurementRow row(String metric, Double initial, Double current, boolean higherIsBetter) {
    String initialStr = initial != null ? String.format("%.1f", initial) : "—";
    String currentStr = current != null ? String.format("%.1f", current) : "—";

    if (initial == null || current == null) {
      return new MeasurementRow(metric, initialStr, currentStr, "—", null);
    }

    double diff = current - initial;
    String diffStr = (diff >= 0 ? "+" : "") + String.format("%.1f", diff);
    Boolean improved = higherIsBetter ? diff > 0 : diff < 0;

    return new MeasurementRow(metric, initialStr, currentStr, diffStr, improved);
  }
}
