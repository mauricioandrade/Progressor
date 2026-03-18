package com.mauricioandrade.progressor.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.common.Email;
import com.mauricioandrade.progressor.core.domain.connection.ConnectionRequest;
import com.mauricioandrade.progressor.core.domain.connection.ConnectionStatus;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import com.mauricioandrade.progressor.core.domain.user.Student;
import java.time.LocalDate;
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
class RespondToConnectionRequestUseCaseTest {

    @Mock
    private ConnectionRequestRepository connectionRequestRepository;

    @Mock
    private UserRepository userRepository;

    private RespondToConnectionRequestUseCase useCase;

    private UUID requestId;
    private UUID professionalId;
    private UUID studentId;
    private Student student;
    private ConnectionRequest pendingRequest;

    @BeforeEach
    void setUp() {
        useCase = new RespondToConnectionRequestUseCase(connectionRequestRepository, userRepository);

        requestId = UUID.randomUUID();
        professionalId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        student = new Student(
                studentId, "Maria", "Santos",
                new Email("maria@email.com"), "pass456",
                LocalDate.of(1995, 7, 20)
        );

        pendingRequest = new ConnectionRequest(
                requestId, professionalId, studentId,
                "João Silva", ProfessionalRole.COACH,
                ConnectionStatus.PENDING, LocalDateTime.now()
        );
    }

    @Test
    void shouldAcceptRequest_andAssignCoachToStudent() {
        when(connectionRequestRepository.findById(requestId)).thenReturn(Optional.of(pendingRequest));
        when(userRepository.findStudentById(studentId)).thenReturn(Optional.of(student));

        useCase.execute(requestId, studentId, true);

        assertThat(pendingRequest.getStatus()).isEqualTo(ConnectionStatus.ACCEPTED);
        verify(connectionRequestRepository).updateStatus(requestId, ConnectionStatus.ACCEPTED);

        ArgumentCaptor<Student> studentCaptor = ArgumentCaptor.forClass(Student.class);
        verify(userRepository).update(studentCaptor.capture());
        assertThat(studentCaptor.getValue().getPersonalTrainerId()).isEqualTo(professionalId);
    }

    @Test
    void shouldAcceptRequest_andAssignNutritionistToStudent() {
        ConnectionRequest nutriRequest = new ConnectionRequest(
                requestId, professionalId, studentId,
                "Ana Lima", ProfessionalRole.NUTRI,
                ConnectionStatus.PENDING, LocalDateTime.now()
        );
        when(connectionRequestRepository.findById(requestId)).thenReturn(Optional.of(nutriRequest));
        when(userRepository.findStudentById(studentId)).thenReturn(Optional.of(student));

        useCase.execute(requestId, studentId, true);

        assertThat(nutriRequest.getStatus()).isEqualTo(ConnectionStatus.ACCEPTED);
        ArgumentCaptor<Student> captor = ArgumentCaptor.forClass(Student.class);
        verify(userRepository).update(captor.capture());
        assertThat(captor.getValue().getNutritionistId()).isEqualTo(professionalId);
    }

    @Test
    void shouldRejectRequest_withoutAssigningAnyone() {
        when(connectionRequestRepository.findById(requestId)).thenReturn(Optional.of(pendingRequest));

        useCase.execute(requestId, studentId, false);

        assertThat(pendingRequest.getStatus()).isEqualTo(ConnectionStatus.REJECTED);
        verify(connectionRequestRepository).updateStatus(requestId, ConnectionStatus.REJECTED);
        verify(userRepository, never()).update(any());
    }

    @Test
    void shouldThrow_whenRequestDoesNotBelongToStudent() {
        UUID otherStudentId = UUID.randomUUID();
        when(connectionRequestRepository.findById(requestId)).thenReturn(Optional.of(pendingRequest));

        assertThatThrownBy(() -> useCase.execute(requestId, otherStudentId, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("does not belong to this student");

        verify(connectionRequestRepository, never()).updateStatus(any(), any());
        verify(userRepository, never()).update(any());
    }

    @Test
    void shouldThrow_whenRequestNotFound() {
        when(connectionRequestRepository.findById(requestId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(requestId, studentId, true))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Connection request not found");
    }

    @Test
    void shouldThrow_whenTryingToAcceptAlreadyAcceptedRequest() {
        ConnectionRequest alreadyAccepted = new ConnectionRequest(
                requestId, professionalId, studentId,
                "João Silva", ProfessionalRole.COACH,
                ConnectionStatus.ACCEPTED, LocalDateTime.now()
        );
        when(connectionRequestRepository.findById(requestId)).thenReturn(Optional.of(alreadyAccepted));

        assertThatThrownBy(() -> useCase.execute(requestId, studentId, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Only PENDING requests can be accepted");
    }
}
