package com.mauricioandrade.progressor.infrastructure.config;

import com.mauricioandrade.progressor.core.application.ports.CheckInRepository;
import com.mauricioandrade.progressor.core.application.ports.FoodSearchPort;
import com.mauricioandrade.progressor.core.application.ports.ProgressPhotoRepository;
import com.mauricioandrade.progressor.core.application.usecases.AddProfessionalFeedbackUseCase;
import com.mauricioandrade.progressor.core.application.usecases.DeleteProfessionalFeedbackUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateStudentNotesUseCase;
import com.mauricioandrade.progressor.core.application.usecases.DeleteProgressPhotoUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetProgressPhotosUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UploadProgressPhotoUseCase;
import com.mauricioandrade.progressor.core.application.ports.MealPlanReportGenerator;
import com.mauricioandrade.progressor.core.application.ports.MealPlanRepository;
import com.mauricioandrade.progressor.core.application.ports.MeasurementRepository;
import com.mauricioandrade.progressor.core.application.ports.PasswordEncoder;
import com.mauricioandrade.progressor.core.application.ports.ProgressReportGenerator;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutLogRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutReportGenerator;
import com.mauricioandrade.progressor.core.application.ports.WorkoutRepository;
import com.mauricioandrade.progressor.core.application.usecases.AddWaterIntakeUseCase;
import com.mauricioandrade.progressor.core.application.usecases.AssignStudentToNutritionistUseCase;
import com.mauricioandrade.progressor.core.application.usecases.AssignStudentToTrainerUseCase;
import com.mauricioandrade.progressor.core.application.usecases.CheckInStudentUseCase;
import com.mauricioandrade.progressor.core.application.usecases.CreateMealPlanUseCase;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutUseCase;
import com.mauricioandrade.progressor.core.application.usecases.FindStudentByEmailForNutritionistUseCase;
import com.mauricioandrade.progressor.core.application.usecases.FindStudentByEmailUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GenerateMealPlanReportUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GenerateProgressReportUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GenerateWorkoutSheetUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetExerciseHistoryUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetNutritionistStudentsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentFrequencyUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentMealPlanUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentMeasurementsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentPersonalRecordsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.ForgotPasswordUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentWorkoutUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetTodayWorkoutUseCase;
import com.mauricioandrade.progressor.core.application.usecases.ResetPasswordUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetTrainerStudentsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetWaterIntakeUseCase;
import com.mauricioandrade.progressor.core.application.usecases.LogExerciseExecutionUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RecordMeasurementUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RegisterNutritionistUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RegisterPersonalTrainerUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RegisterStudentUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SearchFoodUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SetWaterGoalUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateAvatarUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Bean
  public ForgotPasswordUseCase forgotPasswordUseCase(UserRepository userRepository) {
    return new ForgotPasswordUseCase(userRepository);
  }

  @Bean
  public ResetPasswordUseCase resetPasswordUseCase(UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    return new ResetPasswordUseCase(userRepository, passwordEncoder);
  }

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
  public RegisterNutritionistUseCase registerNutritionistUseCase(UserRepository userRepository,
      PasswordEncoder passwordEncoder) {
    return new RegisterNutritionistUseCase(userRepository, passwordEncoder);
  }

  @Bean
  public GetTrainerStudentsUseCase getTrainerStudentsUseCase(UserRepository userRepository,
      CheckInRepository checkInRepository) {
    return new GetTrainerStudentsUseCase(userRepository, checkInRepository);
  }

  @Bean
  public AssignStudentToTrainerUseCase assignStudentToTrainerUseCase(
      UserRepository userRepository) {
    return new AssignStudentToTrainerUseCase(userRepository);
  }

  @Bean
  public FindStudentByEmailUseCase findStudentByEmailUseCase(UserRepository userRepository) {
    return new FindStudentByEmailUseCase(userRepository);
  }

  @Bean
  public CreateWorkoutUseCase createWorkoutUseCase(WorkoutRepository workoutRepository,
      UserRepository userRepository) {
    return new CreateWorkoutUseCase(workoutRepository, userRepository);
  }

  @Bean
  public GetStudentWorkoutUseCase getStudentWorkoutUseCase(WorkoutRepository workoutRepository) {
    return new GetStudentWorkoutUseCase(workoutRepository);
  }

  @Bean
  public GetTodayWorkoutUseCase getTodayWorkoutUseCase(WorkoutRepository workoutRepository) {
    return new GetTodayWorkoutUseCase(workoutRepository);
  }

  @Bean
  public GenerateWorkoutSheetUseCase generateWorkoutSheetUseCase(
      WorkoutReportGenerator reportGenerator) {
    return new GenerateWorkoutSheetUseCase(reportGenerator);
  }

  @Bean
  public GenerateProgressReportUseCase generateProgressReportUseCase(
      ProgressReportGenerator reportGenerator) {
    return new GenerateProgressReportUseCase(reportGenerator);
  }

  @Bean
  public GenerateMealPlanReportUseCase generateMealPlanReportUseCase(
      MealPlanReportGenerator reportGenerator) {
    return new GenerateMealPlanReportUseCase(reportGenerator);
  }

  @Bean
  public RecordMeasurementUseCase recordMeasurementUseCase(
      MeasurementRepository measurementRepository, UserRepository userRepository) {
    return new RecordMeasurementUseCase(measurementRepository, userRepository);
  }

  @Bean
  public GetStudentMeasurementsUseCase getStudentMeasurementsUseCase(
      MeasurementRepository measurementRepository) {
    return new GetStudentMeasurementsUseCase(measurementRepository);
  }

  @Bean
  public LogExerciseExecutionUseCase logExerciseExecutionUseCase(
      WorkoutLogRepository workoutLogRepository) {
    return new LogExerciseExecutionUseCase(workoutLogRepository);
  }

  @Bean
  public GetExerciseHistoryUseCase getExerciseHistoryUseCase(
      WorkoutLogRepository workoutLogRepository) {
    return new GetExerciseHistoryUseCase(workoutLogRepository);
  }

  @Bean
  public GetStudentPersonalRecordsUseCase getStudentPersonalRecordsUseCase(
      WorkoutLogRepository workoutLogRepository) {
    return new GetStudentPersonalRecordsUseCase(workoutLogRepository);
  }

  @Bean
  public CheckInStudentUseCase checkInStudentUseCase(CheckInRepository checkInRepository) {
    return new CheckInStudentUseCase(checkInRepository);
  }

  @Bean
  public GetStudentFrequencyUseCase getStudentFrequencyUseCase(CheckInRepository checkInRepository) {
    return new GetStudentFrequencyUseCase(checkInRepository);
  }

  @Bean
  public GetNutritionistStudentsUseCase getNutritionistStudentsUseCase(UserRepository userRepository) {
    return new GetNutritionistStudentsUseCase(userRepository);
  }

  @Bean
  public AssignStudentToNutritionistUseCase assignStudentToNutritionistUseCase(UserRepository userRepository) {
    return new AssignStudentToNutritionistUseCase(userRepository);
  }

  @Bean
  public FindStudentByEmailForNutritionistUseCase findStudentByEmailForNutritionistUseCase(UserRepository userRepository) {
    return new FindStudentByEmailForNutritionistUseCase(userRepository);
  }

  @Bean
  public CreateMealPlanUseCase createMealPlanUseCase(MealPlanRepository mealPlanRepository) {
    return new CreateMealPlanUseCase(mealPlanRepository);
  }

  @Bean
  public GetStudentMealPlanUseCase getStudentMealPlanUseCase(MealPlanRepository mealPlanRepository) {
    return new GetStudentMealPlanUseCase(mealPlanRepository);
  }

  @Bean
  public SearchFoodUseCase searchFoodUseCase(FoodSearchPort foodSearchPort) {
    return new SearchFoodUseCase(foodSearchPort);
  }

  @Bean
  public GetWaterIntakeUseCase getWaterIntakeUseCase(UserRepository userRepository) {
    return new GetWaterIntakeUseCase(userRepository);
  }

  @Bean
  public AddWaterIntakeUseCase addWaterIntakeUseCase(UserRepository userRepository) {
    return new AddWaterIntakeUseCase(userRepository);
  }

  @Bean
  public SetWaterGoalUseCase setWaterGoalUseCase(UserRepository userRepository) {
    return new SetWaterGoalUseCase(userRepository);
  }

  @Bean
  public UpdateAvatarUseCase updateAvatarUseCase(UserRepository userRepository) {
    return new UpdateAvatarUseCase(userRepository);
  }

  @Bean
  public UploadProgressPhotoUseCase uploadProgressPhotoUseCase(
      ProgressPhotoRepository progressPhotoRepository) {
    return new UploadProgressPhotoUseCase(progressPhotoRepository);
  }

  @Bean
  public GetProgressPhotosUseCase getProgressPhotosUseCase(
      ProgressPhotoRepository progressPhotoRepository) {
    return new GetProgressPhotosUseCase(progressPhotoRepository);
  }

  @Bean
  public DeleteProgressPhotoUseCase deleteProgressPhotoUseCase(
      ProgressPhotoRepository progressPhotoRepository) {
    return new DeleteProgressPhotoUseCase(progressPhotoRepository);
  }

  @Bean
  public AddProfessionalFeedbackUseCase addProfessionalFeedbackUseCase(
      ProgressPhotoRepository progressPhotoRepository) {
    return new AddProfessionalFeedbackUseCase(progressPhotoRepository);
  }

  @Bean
  public DeleteProfessionalFeedbackUseCase deleteProfessionalFeedbackUseCase(
      ProgressPhotoRepository progressPhotoRepository) {
    return new DeleteProfessionalFeedbackUseCase(progressPhotoRepository);
  }

  @Bean
  public UpdateStudentNotesUseCase updateStudentNotesUseCase(
      ProgressPhotoRepository progressPhotoRepository) {
    return new UpdateStudentNotesUseCase(progressPhotoRepository);
  }
}
