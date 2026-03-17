package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WaterIntakeResponse;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.user.Student;
import java.util.UUID;

public class GetWaterIntakeUseCase {

    private final UserRepository userRepository;

    public GetWaterIntakeUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public WaterIntakeResponse execute(UUID studentId) {
        Student student = userRepository.findStudentById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        return new WaterIntakeResponse(student.getDailyWaterGoal(), student.getCurrentWaterIntake());
    }
}
