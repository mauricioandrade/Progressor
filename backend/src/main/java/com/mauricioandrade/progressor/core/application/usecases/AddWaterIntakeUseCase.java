package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WaterIntakeResponse;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.user.Student;
import java.util.UUID;

public class AddWaterIntakeUseCase {

    private final UserRepository userRepository;

    public AddWaterIntakeUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public WaterIntakeResponse execute(UUID studentId, int amount) {
        Student student = userRepository.findStudentById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        student.addWaterIntake(amount);
        userRepository.update(student);
        return new WaterIntakeResponse(student.getDailyWaterGoal(), student.getCurrentWaterIntake());
    }
}
