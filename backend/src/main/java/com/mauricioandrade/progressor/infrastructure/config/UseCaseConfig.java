package com.mauricioandrade.progressor.infrastructure.config;

import com.mauricioandrade.progressor.core.application.ports.MeasurementRepository;
import com.mauricioandrade.progressor.core.application.ports.PasswordEncoder;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutReportGenerator;
import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GenerateWorkoutSheetUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RecordMeasurementUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RegisterPersonalTrainerUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RegisterStudentUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  public RegisterPersonalTrainerUseCase registerPersonalTrainerUseCase(
      UserRepository userRepository, PasswordEncoder passwordEncoder) {
    return new RegisterPersonalTrainerUseCase(userRepository, passwordEncoder);
  }

  @Bean
  public RegisterStudentUseCase registerStudentUseCase(UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    return new RegisterStudentUseCase(userRepository, passwordEncoder);
  }

  @Bean
  public CreateWorkoutUseCase createWorkoutUseCase(WorkoutRepository workoutRepository,
      UserRepository userRepository) {
    return new CreateWorkoutUseCase(workoutRepository, userRepository);
  }

  @Bean
  public GenerateWorkoutSheetUseCase generateWorkoutSheetUseCase(
      WorkoutReportGenerator reportGenerator) {
    return new GenerateWorkoutSheetUseCase(reportGenerator);
  }

  @Bean
  public RecordMeasurementUseCase recordMeasurementUseCase(
      MeasurementRepository measurementRepository, UserRepository userRepository) {
    return new RecordMeasurementUseCase(measurementRepository, userRepository);
  }
}