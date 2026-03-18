package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.MeasurementCreatedResponse;
import com.mauricioandrade.progressor.core.application.dto.RecordMeasurementRequest;
import com.mauricioandrade.progressor.core.application.ports.MeasurementRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.measurement.BodyMeasurement;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RecordMeasurementUseCase {

  private static final Logger log = LoggerFactory.getLogger(RecordMeasurementUseCase.class);

  private final MeasurementRepository measurementRepository;
  private final UserRepository userRepository;

  public RecordMeasurementUseCase(MeasurementRepository measurementRepository,
      UserRepository userRepository) {
    this.measurementRepository = measurementRepository;
    this.userRepository = userRepository;
  }

  public MeasurementCreatedResponse execute(RecordMeasurementRequest request) {
    if (!userRepository.existsById(request.studentId())) {
      throw new IllegalArgumentException("Student not found");
    }

    BodyMeasurement measurement = new BodyMeasurement(null, request.studentId(), LocalDate.now());

    measurement.updateMeasurements(request.weight(), request.bodyFatPercentage(),
        request.rightBicep(), request.leftBicep(), request.chest(),
        request.waist(), request.abdomen(), request.hips(), request.leftThigh(),
        request.rightThigh(), request.rightCalf(), request.leftCalf());

    measurementRepository.save(measurement);

    String goalStatus = evaluateWeightGoal(request.studentId(), request.weight());

    return new MeasurementCreatedResponse(measurement.getId(), goalStatus);
  }

  private String evaluateWeightGoal(UUID studentId, Double weight) {
    if (weight == null) {
      return null;
    }
    Optional<Double> goalOpt = userRepository.findWeightGoalByStudentId(studentId);
    if (goalOpt.isEmpty() || goalOpt.get() == null) {
      return null;
    }
    double goal = goalOpt.get();
    double diff = Math.abs(weight - goal);
    if (diff <= 0.5) {
      log.info("[NOTIFICAÇÃO META] Aluno {} atingiu a meta de peso! Atual: {}kg | Meta: {}kg — 'Parabéns! Você atingiu sua meta de peso!'",
          studentId, weight, goal);
      return "REACHED";
    } else if (diff <= 2.0) {
      log.info("[NOTIFICAÇÃO META] Aluno {} está próximo da meta! Atual: {}kg | Meta: {}kg — 'A meta está próxima! Continue firme!'",
          studentId, weight, goal);
      return "NEARLY_REACHED";
    }
    return null;
  }
}
