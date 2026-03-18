package com.mauricioandrade.progressor.core.application.usecases;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.mauricioandrade.progressor.core.application.ports.ConnectionRequestRepository;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.common.Cref;
import com.mauricioandrade.progressor.core.domain.common.Email;
import com.mauricioandrade.progressor.core.domain.connection.ConnectionRequest;
import com.mauricioandrade.progressor.core.domain.connection.ProfessionalRole;
import com.mauricioandrade.progressor.core.domain.user.PersonalTrainer;
import com.mauricioandrade.progressor.core.domain.user.Student;
import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class SendConnectionRequestUseCaseTest {

    @Mock
    private ConnectionRequestRepository connectionRequestRepository;

    @Mock
    private UserRepository userRepository;

    private SendConnectionRequestUseCase useCase;

    private UUID professionalId;
    private UUID studentId;
    private PersonalTrainer professional;
    private Student student;

    @BeforeEach
    void setUp() {
        useCase = new SendConnectionRequestUseCase(connectionRequestRepository, userRepository);

        professionalId = UUID.randomUUID();
        studentId = UUID.randomUUID();

        professional = new PersonalTrainer(
                professionalId, "João", "Silva",
                new Email("joao@gym.com"), "pass123",
                LocalDate.of(1985, 3, 10), new Cref("CREF-12345")
        );

        student = new Student(
                studentId, "Maria", "Santos",
                new Email("maria@email.com"), "pass456",
                LocalDate.of(1995, 7, 20)
        );
    }

    @Test
    void shouldSavePendingConnectionRequest_whenValidInvite() {
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(userRepository.findStudentByEmail(any(Email.class))).thenReturn(Optional.of(student));
        when(connectionRequestRepository.existsPendingRequest(professionalId, studentId, ProfessionalRole.COACH))
                .thenReturn(false);

        useCase.execute(professionalId, "maria@email.com", ProfessionalRole.COACH);

        ArgumentCaptor<ConnectionRequest> captor = ArgumentCaptor.forClass(ConnectionRequest.class);
        verify(connectionRequestRepository).save(captor.capture());

        ConnectionRequest saved = captor.getValue();
        assertThat(saved.getProfessionalId()).isEqualTo(professionalId);
        assertThat(saved.getStudentId()).isEqualTo(studentId);
        assertThat(saved.getProfessionalRole()).isEqualTo(ProfessionalRole.COACH);
        assertThat(saved.getProfessionalName()).isEqualTo("João Silva");
        assertThat(saved.getStatus().name()).isEqualTo("PENDING");
    }

    @Test
    void shouldThrow_whenPendingInviteAlreadyExists() {
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(userRepository.findStudentByEmail(any(Email.class))).thenReturn(Optional.of(student));
        when(connectionRequestRepository.existsPendingRequest(professionalId, studentId, ProfessionalRole.COACH))
                .thenReturn(true);

        assertThatThrownBy(() -> useCase.execute(professionalId, "maria@email.com", ProfessionalRole.COACH))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("pending invitation already exists");

        verify(connectionRequestRepository, never()).save(any());
    }

    @Test
    void shouldThrow_whenProfessionalNotFound() {
        when(userRepository.findById(professionalId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(professionalId, "maria@email.com", ProfessionalRole.COACH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Professional not found");

        verify(connectionRequestRepository, never()).save(any());
    }

    @Test
    void shouldThrow_whenStudentNotFound() {
        when(userRepository.findById(professionalId)).thenReturn(Optional.of(professional));
        when(userRepository.findStudentByEmail(any(Email.class))).thenReturn(Optional.empty());

        assertThatThrownBy(() -> useCase.execute(professionalId, "unknown@email.com", ProfessionalRole.COACH))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Student not found");

        verify(connectionRequestRepository, never()).save(any());
    }
}
