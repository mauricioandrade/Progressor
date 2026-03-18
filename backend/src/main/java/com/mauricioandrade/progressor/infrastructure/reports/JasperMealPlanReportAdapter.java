package com.mauricioandrade.progressor.infrastructure.reports;

import com.mauricioandrade.progressor.core.application.ports.MealPlanReportGenerator;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.MealItemEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.MealPlanEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataMealPlanRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataStudentRepository;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
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
public class JasperMealPlanReportAdapter implements MealPlanReportGenerator {

  private static final Map<String, Integer> MEAL_ORDER = Map.of(
      "BREAKFAST", 0, "LUNCH", 1, "DINNER", 2, "SNACK", 3
  );

  private final SpringDataMealPlanRepository mealPlanRepository;
  private final SpringDataStudentRepository studentRepository;
  private final SpringDataUserRepository userRepository;

  public JasperMealPlanReportAdapter(SpringDataMealPlanRepository mealPlanRepository,
      SpringDataStudentRepository studentRepository,
      SpringDataUserRepository userRepository) {
    this.mealPlanRepository = mealPlanRepository;
    this.studentRepository = studentRepository;
    this.userRepository = userRepository;
  }

  @Override
  public byte[] generate(UUID studentId, Locale locale) {
    try {
      MealPlanEntity plan = mealPlanRepository.findTopByStudentIdOrderByCreatedAtDesc(studentId)
          .orElseThrow(() -> new IllegalArgumentException("No meal plan found for this student"));

      ResourceBundle bundle = ResourceBundle.getBundle("reports.report_labels", locale);

      String studentName = studentRepository.findById(studentId)
          .map(s -> s.getFirstName() + " " + s.getLastName())
          .orElse("—");

      String professionalName = studentRepository.findById(studentId)
          .filter(s -> s.getNutritionistId() != null)
          .flatMap(s -> userRepository.findById(s.getNutritionistId()))
          .map(u -> u.getFirstName() + " " + u.getLastName())
          .orElse("—");

      List<MealPlanRow> rows = plan.getItems().stream()
          .sorted(Comparator.comparingInt(i -> MEAL_ORDER.getOrDefault(i.getMealTime(), 99)))
          .map(item -> toRow(item, bundle))
          .toList();

      double totalCalories = plan.getItems().stream()
          .filter(i -> i.getCaloriesKcal() != null).mapToDouble(MealItemEntity::getCaloriesKcal).sum();
      double totalProtein = plan.getItems().stream()
          .filter(i -> i.getProteinG() != null).mapToDouble(MealItemEntity::getProteinG).sum();
      double totalCarbs = plan.getItems().stream()
          .filter(i -> i.getCarbsG() != null).mapToDouble(MealItemEntity::getCarbsG).sum();
      double totalFat = plan.getItems().stream()
          .filter(i -> i.getFatG() != null).mapToDouble(MealItemEntity::getFatG).sum();

      InputStream reportStream = getClass().getResourceAsStream("/reports/meal_plan.jrxml");
      if (reportStream == null) {
        throw new RuntimeException("Meal plan report template not found");
      }

      JasperReport jasperReport = JasperCompileManager.compileReport(reportStream);
      JRBeanCollectionDataSource dataSource = new JRBeanCollectionDataSource(rows);

      HashMap<String, Object> parameters = new HashMap<>();
      parameters.put(JRParameter.REPORT_LOCALE, locale);
      parameters.put("studentName", studentName);
      parameters.put("professionalName", professionalName);
      parameters.put("issuedOn", LocalDate.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
      parameters.put("planName", plan.getName() != null ? plan.getName() : "—");
      parameters.put("planGoal", plan.getGoal() != null ? plan.getGoal() : "—");
      parameters.put("totalCalories", String.format("%.0f kcal", totalCalories));
      parameters.put("totalProtein", String.format("%.1f g", totalProtein));
      parameters.put("totalCarbs", String.format("%.1f g", totalCarbs));
      parameters.put("totalFat", String.format("%.1f g", totalFat));

      JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, parameters, dataSource);

      return JasperExportManager.exportReportToPdf(jasperPrint);

    } catch (JRException e) {
      throw new RuntimeException("Error generating meal plan report", e);
    }
  }

  private MealPlanRow toRow(MealItemEntity item, ResourceBundle bundle) {
    String mealTime = item.getMealTime() != null ? item.getMealTime() : "SNACK";
    String labelKey = "report.meal." + mealTime.toLowerCase();
    String mealTimeLabel = bundle.containsKey(labelKey) ? bundle.getString(labelKey) : mealTime;

    String quantity = item.getQuantity() != null
        ? String.format("%.0f %s", item.getQuantity(), item.getBaseUnit() != null ? item.getBaseUnit() : "")
        : "—";

    return new MealPlanRow(
        mealTime,
        mealTimeLabel,
        item.getName() != null ? item.getName() : "—",
        quantity,
        item.getCaloriesKcal() != null ? String.format("%.0f", item.getCaloriesKcal()) : "—",
        item.getProteinG() != null ? String.format("%.1f g", item.getProteinG()) : "—",
        item.getCarbsG() != null ? String.format("%.1f g", item.getCarbsG()) : "—",
        item.getFatG() != null ? String.format("%.1f g", item.getFatG()) : "—"
    );
  }
}
