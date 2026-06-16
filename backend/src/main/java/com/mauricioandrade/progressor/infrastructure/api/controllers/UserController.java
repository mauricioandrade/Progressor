package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.dto.RegisterNutritionistRequest;
import com.mauricioandrade.progressor.core.application.dto.RegisterPersonalTrainerRequest;
import com.mauricioandrade.progressor.core.application.dto.RegisterStudentRequest;
import com.mauricioandrade.progressor.core.application.dto.StudentSummaryResponse;
import com.mauricioandrade.progressor.core.application.dto.UserProfileResponse;
import com.mauricioandrade.progressor.core.application.usecases.AssignStudentToNutritionistUseCase;
import com.mauricioandrade.progressor.core.application.usecases.AssignStudentToTrainerUseCase;
import com.mauricioandrade.progressor.core.application.usecases.FindStudentByEmailForNutritionistUseCase;
import com.mauricioandrade.progressor.core.application.usecases.FindStudentByEmailUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetNutritionistStudentsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.GetTrainerStudentsUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RegisterNutritionistUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RegisterPersonalTrainerUseCase;
import com.mauricioandrade.progressor.core.application.usecases.RegisterStudentUseCase;
import com.mauricioandrade.progressor.core.application.usecases.UpdateAvatarUseCase;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import com.mauricioandrade.progressor.infrastructure.security.ImageValidator;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/users")
public class UserController {

  private final RegisterPersonalTrainerUseCase registerPersonalTrainerUseCase;
  private final RegisterStudentUseCase registerStudentUseCase;
  private final RegisterNutritionistUseCase registerNutritionistUseCase;
  private final GetTrainerStudentsUseCase getTrainerStudentsUseCase;
  private final AssignStudentToTrainerUseCase assignStudentToTrainerUseCase;
  private final FindStudentByEmailUseCase findStudentByEmailUseCase;
  private final GetNutritionistStudentsUseCase getNutritionistStudentsUseCase;
  private final AssignStudentToNutritionistUseCase assignStudentToNutritionistUseCase;
  private final FindStudentByEmailForNutritionistUseCase findStudentByEmailForNutritionistUseCase;
  private final UpdateAvatarUseCase updateAvatarUseCase;
  private final ImageValidator imageValidator;
  private final SpringDataUserRepository userRepository;

  public UserController(RegisterPersonalTrainerUseCase ptUseCase,
      RegisterStudentUseCase studentUseCase, RegisterNutritionistUseCase nutritionistUseCase,
      GetTrainerStudentsUseCase getTrainerStudentsUseCase,
      AssignStudentToTrainerUseCase assignStudentToTrainerUseCase,
      FindStudentByEmailUseCase findStudentByEmailUseCase,
      GetNutritionistStudentsUseCase getNutritionistStudentsUseCase,
      AssignStudentToNutritionistUseCase assignStudentToNutritionistUseCase,
      FindStudentByEmailForNutritionistUseCase findStudentByEmailForNutritionistUseCase,
      UpdateAvatarUseCase updateAvatarUseCase,
      ImageValidator imageValidator,
      SpringDataUserRepository userRepository) {
    this.registerPersonalTrainerUseCase = ptUseCase;
    this.registerStudentUseCase = studentUseCase;
    this.registerNutritionistUseCase = nutritionistUseCase;
    this.getTrainerStudentsUseCase = getTrainerStudentsUseCase;
    this.assignStudentToTrainerUseCase = assignStudentToTrainerUseCase;
    this.findStudentByEmailUseCase = findStudentByEmailUseCase;
    this.getNutritionistStudentsUseCase = getNutritionistStudentsUseCase;
    this.assignStudentToNutritionistUseCase = assignStudentToNutritionistUseCase;
    this.findStudentByEmailForNutritionistUseCase = findStudentByEmailForNutritionistUseCase;
    this.updateAvatarUseCase = updateAvatarUseCase;
    this.imageValidator = imageValidator;
    this.userRepository = userRepository;
  }

  @PostMapping("/register/personal")
  public ResponseEntity<Map<String, UUID>> registerPersonalTrainer(
      @Valid @RequestBody RegisterPersonalTrainerRequest request) {
    UUID id = registerPersonalTrainerUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
  }

  @PostMapping("/register/student")
  public ResponseEntity<Map<String, UUID>> registerStudent(
      @Valid @RequestBody RegisterStudentRequest request) {
    UUID id = registerStudentUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
  }

  @PostMapping("/register/nutritionist")
  public ResponseEntity<Map<String, UUID>> registerNutritionist(
      @Valid @RequestBody RegisterNutritionistRequest request) {
    UUID id = registerNutritionistUseCase.execute(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("id", id));
  }

  @GetMapping("/students")
  public ResponseEntity<List<StudentSummaryResponse>> getMyStudents(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(getTrainerStudentsUseCase.execute(currentUser.getId()));
  }

  @GetMapping("/students/search")
  public ResponseEntity<StudentSummaryResponse> searchStudent(@RequestParam String email) {
    return findStudentByEmailUseCase.execute(email)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/{studentId}/assign-trainer")
  public ResponseEntity<Void> assignTrainer(@PathVariable UUID studentId,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    assignStudentToTrainerUseCase.execute(studentId, currentUser.getId());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/my-students/nutritionist")
  public ResponseEntity<List<StudentSummaryResponse>> getNutritionistStudents(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(getNutritionistStudentsUseCase.execute(currentUser.getId()));
  }

  @PostMapping("/{studentId}/assign-nutritionist")
  public ResponseEntity<Void> assignNutritionist(@PathVariable UUID studentId,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    assignStudentToNutritionistUseCase.execute(studentId, currentUser.getId());
    return ResponseEntity.ok().build();
  }

  @GetMapping("/students/search/nutritionist")
  public ResponseEntity<StudentSummaryResponse> searchStudentForNutritionist(@RequestParam String email) {
    return findStudentByEmailForNutritionistUseCase.execute(email)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/me")
  public ResponseEntity<UserProfileResponse> getProfile(
      @AuthenticationPrincipal UserPrincipal currentUser) {
    return ResponseEntity.ok(new UserProfileResponse(
        currentUser.getFirstName() + " " + currentUser.getLastName(),
        currentUser.getEmail(),
        userRepository.hasAvatar(currentUser.getId())));
  }

  @GetMapping(value = "/me/avatar", produces = MediaType.IMAGE_JPEG_VALUE)
  public ResponseEntity<byte[]> getAvatar(@AuthenticationPrincipal UserPrincipal currentUser) {
    return userRepository.findById(currentUser.getId())
        .map(entity -> {
          byte[] avatar = entity.getAvatar();
          if (avatar == null || avatar.length == 0) {
            return ResponseEntity.notFound().<byte[]>build();
          }
          return ResponseEntity.ok(avatar);
        })
        .orElse(ResponseEntity.notFound().build());
  }

  @PostMapping("/push-token")
  public ResponseEntity<Void> savePushToken(
      @RequestBody Map<String, String> body,
      @AuthenticationPrincipal UserPrincipal currentUser) {
    String token = body.get("pushToken");
    if (token == null || token.isBlank()) return ResponseEntity.badRequest().build();
    userRepository.findById(currentUser.getId()).ifPresent(entity -> {
      entity.setPushToken(token);
      userRepository.save(entity);
    });
    return ResponseEntity.ok().build();
  }

  @PatchMapping("/me/avatar")
  public ResponseEntity<Void> updateAvatar(
      @RequestParam("file") MultipartFile file,
      @AuthenticationPrincipal UserPrincipal currentUser) throws Exception {
    byte[] bytes = imageValidator.validateAndRead(file);
    updateAvatarUseCase.execute(currentUser.getId(), bytes);
    return ResponseEntity.ok().build();
  }
}
