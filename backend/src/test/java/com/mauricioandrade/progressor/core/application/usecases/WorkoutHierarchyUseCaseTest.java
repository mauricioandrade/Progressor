package com.mauricioandrade.progressor.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutBlockRequest;
import com.mauricioandrade.progressor.core.application.dto.CreateWorkoutPlanRequest;
import com.mauricioandrade.progressor.core.application.dto.WorkoutBlockResponse;
import com.mauricioandrade.progressor.core.application.dto.WorkoutPlanResponse;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutBlockRepository;
import com.mauricioandrade.progressor.core.application.ports.WorkoutPlanRepository;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutBlock;
import com.mauricioandrade.progressor.core.domain.workout.WorkoutPlan;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class WorkoutHierarchyUseCaseTest {

    @Mock
    private WorkoutPlanRepository workoutPlanRepository;

    @Mock
    private WorkoutBlockRepository workoutBlockRepository;

    @Mock
    private UserRepository userRepository;

    private CreateWorkoutPlanUseCase createPlanUseCase;
    private CreateWorkoutBlockUseCase createBlockUseCase;

    @BeforeEach
    void setUp() {
        createPlanUseCase = new CreateWorkoutPlanUseCase(workoutPlanRepository, userRepository);
        createBlockUseCase = new CreateWorkoutBlockUseCase(workoutBlockRepository, workoutPlanRepository);
    }

    @Test
    void shouldCreateWorkoutPlan_andReturnResponse() {
        UUID trainerId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        when(userRepository.existsById(studentId)).thenReturn(true);

        WorkoutPlanResponse response = createPlanUseCase.execute(
                new CreateWorkoutPlanRequest(studentId, "Plano A"),
                trainerId
        );

        ArgumentCaptor<WorkoutPlan> captor = ArgumentCaptor.forClass(WorkoutPlan.class);
        verify(workoutPlanRepository).save(captor.capture());

        WorkoutPlan saved = captor.getValue();
        assertThat(saved.getStudentId()).isEqualTo(studentId);
        assertThat(saved.getTrainerId()).isEqualTo(trainerId);
        assertThat(saved.getName()).isEqualTo("Plano A");

        assertThat(response.name()).isEqualTo("Plano A");
        assertThat(response.blocks()).isEmpty();
    }

    @Test
    void shouldThrow_whenStudentNotFoundOnPlanCreation() {
        UUID trainerId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();
        when(userRepository.existsById(studentId)).thenReturn(false);

        assertThatThrownBy(() -> createPlanUseCase.execute(
                new CreateWorkoutPlanRequest(studentId, "Plano A"), trainerId))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");
    }

    @Test
    void shouldCreateWorkoutBlock_linkedToPlan() {
        UUID planId = UUID.randomUUID();
        WorkoutPlan existingPlan = new WorkoutPlan(planId, UUID.randomUUID(), UUID.randomUUID(),
                "Plano A", LocalDateTime.now());
        when(workoutPlanRepository.findById(planId)).thenReturn(Optional.of(existingPlan));

        WorkoutBlockResponse response = createBlockUseCase.execute(
                new CreateWorkoutBlockRequest(planId, "Bloco A", 0)
        );

        ArgumentCaptor<WorkoutBlock> captor = ArgumentCaptor.forClass(WorkoutBlock.class);
        verify(workoutBlockRepository).save(captor.capture());

        WorkoutBlock saved = captor.getValue();
        assertThat(saved.getWorkoutPlanId()).isEqualTo(planId);
        assertThat(saved.getName()).isEqualTo("Bloco A");
        assertThat(saved.getPosition()).isEqualTo(0);

        assertThat(response.workoutPlanId()).isEqualTo(planId);
        assertThat(response.name()).isEqualTo("Bloco A");
        assertThat(response.exercises()).isEmpty();
    }

    @Test
    void shouldThrow_whenPlanNotFoundOnBlockCreation() {
        UUID planId = UUID.randomUUID();
        when(workoutPlanRepository.findById(planId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> createBlockUseCase.execute(
                new CreateWorkoutBlockRequest(planId, "Bloco A", 0)))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Workout plan not found");
    }

    @Test
    void shouldValidate_workoutBlockDomain_negativePositionThrows() {
        UUID planId = UUID.randomUUID();

        assertThatThrownBy(() -> new WorkoutBlock(null, planId, "Bloco A", -1))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Position must be non-negative");
    }

    @Test
    void shouldValidate_workoutPlanDomain_blankNameThrows() {
        assertThatThrownBy(() -> new WorkoutPlan(null, UUID.randomUUID(), UUID.randomUUID(), "  "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Plan name is required");
    }
}
