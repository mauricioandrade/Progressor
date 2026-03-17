package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.dto.WaterIntakeResponse;
import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.user.Student;
import java.util.UUID;

public class SetWaterGoalUseCase {

    private final UserRepository userRepository;

    public SetWaterGoalUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public WaterIntakeResponse execute(UUID studentId, int goal) {
        Student student = userRepository.findStudentById(studentId)
            .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        student.setDailyWaterGoal(goal);
        userRepository.update(student);
        return new WaterIntakeResponse(student.getDailyWaterGoal(), student.getCurrentWaterIntake());
    }
}
