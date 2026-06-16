package com.mauricioandrade.progressor.infrastructure.config;

import com.mauricioandrade.progressor.core.application.ports.ChatRepository;
import com.mauricioandrade.progressor.core.application.ports.CheckInRepository;
import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.PushNotificationPort;
import com.mauricioandrade.progressor.core.application.ports.MealConsumptionLogRepository;
import com.mauricioandrade.progressor.core.application.usecases.SendMessageUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetConversationUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetConversationsUseCase;
import com.mauricioandrade.progressor.core.application.ports.WorkoutSessionRepository;
import com.mauricioandrade.progressor.core.application.usecases.SaveWorkoutSessionUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetMyWorkoutSessionsUseCase;
import com.mauricioandrade.progressor.core.application.ports.WorkoutFeedbackRepository;
import com.mauricioandrade.progressor.core.application.usecases.DeleteExtraFoodLogUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentAdherenceUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetTodayConsumptionUseCase;
import com.mauricioandrade.progressor.core.application.usecases.LogExtraFoodUseCase;
import com.mauricioandrade.progressor.core.application.usecases.ToggleMealConsumptionUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SubmitWorkoutFeedbackUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetProfessionalDashboardUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentFeedbacksUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetTodayFeedbackUseCase;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataWorkoutLogRepository;
import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.EmailPort;
import com.mauricioandrade.progressor.core.application.ports.FoodSearchPort;
import com.mauricioandrade.progressor.core.application.ports.WorkoutBlockRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutPlanRepository;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutBlockUseCase;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutPlanUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetPendingConnectionRequestsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetStudentWorkoutPlansUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RespondToConnectionRequestUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SendConnectionRequestUseCase;
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
import com.mauricioandrade.progressor.core.application.usecases.DeleteMealPlanUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateMealPlanUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetMealPlanHistoryUseCase;
import com.mauricioandrade.progressor.core.application.usecases.CreateWorkoutUseCase;
import com.mauricioandrade.progressor.core.application.usecases.DeleteWorkoutExerciseUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetWeightGoalUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateWeightGoalUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateWorkoutExerciseUseCase;
import com.mauricioandrade.progressor.core.application.usecases.FindStudentByEmailForNutritionistUseCase;
import com.mauricioandrade.progressor.core.application.usecases.FindStudentByEmailUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GenerateMealPlanReportUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GenerateProgressReportUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GenerateWorkoutSheetUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetExerciseHistoryUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetExerciseStatsUseCase;
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
import com.mauricioandrade.progressor.core.application.usecases.LookupFoodByBarcodeUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SearchFoodUseCase;
import com.mauricioandrade.progressor.core.application.usecases.SetWaterGoalUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateAvatarUseCase;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class UseCaseConfig {

  @Value("${app.frontend-url:http://localhost:5173}")
  private String frontendUrl;

  @Bean
  public ForgotPasswordUseCase forgotPasswordUseCase(UserRepository userRepository,
      EmailPort emailPort) {
    return new ForgotPasswordUseCase(userRepository, emailPort, frontendUrl);
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
      UserRepository userRepository, ConnectionRequestRepository connectionRequestRepository) {
    return new AssignStudentToTrainerUseCase(userRepository, connectionRequestRepository);
  }

  @Bean
  public FindStudentByEmailUseCase findStudentByEmailUseCase(UserRepository userRepository) {
    return new FindStudentByEmailUseCase(userRepository);
  }

  @Bean
  public CreateWorkoutUseCase createWorkoutUseCase(WorkoutRepository workoutRepository,
      UserRepository userRepository, PushNotificationPort pushNotificationPort) {
    return new CreateWorkoutUseCase(workoutRepository, userRepository, pushNotificationPort);
  }

  @Bean
  public DeleteWorkoutExerciseUseCase deleteWorkoutExerciseUseCase(
      WorkoutRepository workoutRepository) {
    return new DeleteWorkoutExerciseUseCase(workoutRepository);
  }

  @Bean
  public UpdateWorkoutExerciseUseCase updateWorkoutExerciseUseCase(
      WorkoutRepository workoutRepository) {
    return new UpdateWorkoutExerciseUseCase(workoutRepository);
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
  public UpdateWeightGoalUseCase updateWeightGoalUseCase(UserRepository userRepository) {
    return new UpdateWeightGoalUseCase(userRepository);
  }

  @Bean
  public GetWeightGoalUseCase getWeightGoalUseCase(UserRepository userRepository) {
    return new GetWeightGoalUseCase(userRepository);
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
  public GetExerciseStatsUseCase getExerciseStatsUseCase(
      SpringDataWorkoutLogRepository workoutLogRepository) {
    return new GetExerciseStatsUseCase(workoutLogRepository);
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
  public AssignStudentToNutritionistUseCase assignStudentToNutritionistUseCase(
      UserRepository userRepository, ConnectionRequestRepository connectionRequestRepository) {
    return new AssignStudentToNutritionistUseCase(userRepository, connectionRequestRepository);
  }

  @Bean
  public FindStudentByEmailForNutritionistUseCase findStudentByEmailForNutritionistUseCase(UserRepository userRepository) {
    return new FindStudentByEmailForNutritionistUseCase(userRepository);
  }

  @Bean
  public CreateMealPlanUseCase createMealPlanUseCase(MealPlanRepository mealPlanRepository,
      PushNotificationPort pushNotificationPort) {
    return new CreateMealPlanUseCase(mealPlanRepository, pushNotificationPort);
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
  public LookupFoodByBarcodeUseCase lookupFoodByBarcodeUseCase(FoodSearchPort foodSearchPort) {
    return new LookupFoodByBarcodeUseCase(foodSearchPort);
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
      ProgressPhotoRepository progressPhotoRepository, PushNotificationPort pushNotificationPort) {
    return new AddProfessionalFeedbackUseCase(progressPhotoRepository, pushNotificationPort);
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

  @Bean
  public SendConnectionRequestUseCase sendConnectionRequestUseCase(
      ConnectionRequestRepository connectionRequestRepository, UserRepository userRepository) {
    return new SendConnectionRequestUseCase(connectionRequestRepository, userRepository);
  }

  @Bean
  public RespondToConnectionRequestUseCase respondToConnectionRequestUseCase(
      ConnectionRequestRepository connectionRequestRepository, UserRepository userRepository) {
    return new RespondToConnectionRequestUseCase(connectionRequestRepository, userRepository);
  }

  @Bean
  public GetPendingConnectionRequestsUseCase getPendingConnectionRequestsUseCase(
      ConnectionRequestRepository connectionRequestRepository) {
    return new GetPendingConnectionRequestsUseCase(connectionRequestRepository);
  }

  @Bean
  public CreateWorkoutPlanUseCase createWorkoutPlanUseCase(WorkoutPlanRepository workoutPlanRepository,
      UserRepository userRepository) {
    return new CreateWorkoutPlanUseCase(workoutPlanRepository, userRepository);
  }

  @Bean
  public GetStudentWorkoutPlansUseCase getStudentWorkoutPlansUseCase(
      WorkoutPlanRepository workoutPlanRepository, WorkoutBlockRepository workoutBlockRepository,
      WorkoutRepository workoutRepository) {
    return new GetStudentWorkoutPlansUseCase(workoutPlanRepository, workoutBlockRepository,
        workoutRepository);
  }

  @Bean
  public CreateWorkoutBlockUseCase createWorkoutBlockUseCase(
      WorkoutBlockRepository workoutBlockRepository, WorkoutPlanRepository workoutPlanRepository) {
    return new CreateWorkoutBlockUseCase(workoutBlockRepository, workoutPlanRepository);
  }

  @Bean
  public DeleteMealPlanUseCase deleteMealPlanUseCase(MealPlanRepository mealPlanRepository) {
    return new DeleteMealPlanUseCase(mealPlanRepository);
  }

  @Bean
  public UpdateMealPlanUseCase updateMealPlanUseCase(MealPlanRepository mealPlanRepository,
      PushNotificationPort pushNotificationPort) {
    return new UpdateMealPlanUseCase(mealPlanRepository, pushNotificationPort);
  }

  @Bean
  public GetMealPlanHistoryUseCase getMealPlanHistoryUseCase(MealPlanRepository mealPlanRepository) {
    return new GetMealPlanHistoryUseCase(mealPlanRepository);
  }

  @Bean
  public SaveWorkoutSessionUseCase saveWorkoutSessionUseCase(
      WorkoutSessionRepository workoutSessionRepository) {
    return new SaveWorkoutSessionUseCase(workoutSessionRepository);
  }

  @Bean
  public GetMyWorkoutSessionsUseCase getMyWorkoutSessionsUseCase(
      WorkoutSessionRepository workoutSessionRepository) {
    return new GetMyWorkoutSessionsUseCase(workoutSessionRepository);
  }

  @Bean
  public ToggleMealConsumptionUseCase toggleMealConsumptionUseCase(
      MealConsumptionLogRepository mealConsumptionLogRepository) {
    return new ToggleMealConsumptionUseCase(mealConsumptionLogRepository);
  }

  @Bean
  public GetTodayConsumptionUseCase getTodayConsumptionUseCase(
      MealConsumptionLogRepository mealConsumptionLogRepository) {
    return new GetTodayConsumptionUseCase(mealConsumptionLogRepository);
  }

  @Bean
  public LogExtraFoodUseCase logExtraFoodUseCase(
      MealConsumptionLogRepository mealConsumptionLogRepository) {
    return new LogExtraFoodUseCase(mealConsumptionLogRepository);
  }

  @Bean
  public DeleteExtraFoodLogUseCase deleteExtraFoodLogUseCase(
      MealConsumptionLogRepository mealConsumptionLogRepository) {
    return new DeleteExtraFoodLogUseCase(mealConsumptionLogRepository);
  }

  @Bean
  public GetStudentAdherenceUseCase getStudentAdherenceUseCase(
      MealConsumptionLogRepository mealConsumptionLogRepository,
      MealPlanRepository mealPlanRepository) {
    return new GetStudentAdherenceUseCase(mealConsumptionLogRepository, mealPlanRepository);
  }

  @Bean
  public SubmitWorkoutFeedbackUseCase submitWorkoutFeedbackUseCase(
      WorkoutFeedbackRepository feedbackRepository, UserRepository userRepository) {
    return new SubmitWorkoutFeedbackUseCase(feedbackRepository, userRepository);
  }

  @Bean
  public GetTodayFeedbackUseCase getTodayFeedbackUseCase(
      WorkoutFeedbackRepository feedbackRepository) {
    return new GetTodayFeedbackUseCase(feedbackRepository);
  }

  @Bean
  public GetStudentFeedbacksUseCase getStudentFeedbacksUseCase(
      WorkoutFeedbackRepository feedbackRepository) {
    return new GetStudentFeedbacksUseCase(feedbackRepository);
  }

  @Bean
  public SendMessageUseCase sendMessageUseCase(ChatRepository chatRepository,
      UserRepository userRepository, ConnectionRequestRepository connectionRepo,
      PushNotificationPort pushNotificationPort) {
    return new SendMessageUseCase(chatRepository, userRepository, connectionRepo, pushNotificationPort);
  }

  @Bean
  public GetConversationUseCase getConversationUseCase(ChatRepository chatRepository) {
    return new GetConversationUseCase(chatRepository);
  }

  @Bean
  public GetConversationsUseCase getConversationsUseCase(ChatRepository chatRepository) {
    return new GetConversationsUseCase(chatRepository);
  }

  @Bean
  public GetProfessionalDashboardUseCase getProfessionalDashboardUseCase(
      UserRepository userRepository, CheckInRepository checkInRepository,
      WorkoutFeedbackRepository feedbackRepository,
      MealConsumptionLogRepository mealConsumptionLogRepository,
      MealPlanRepository mealPlanRepository) {
    return new GetProfessionalDashboardUseCase(userRepository, checkInRepository,
        feedbackRepository, mealConsumptionLogRepository, mealPlanRepository);
  }
}
