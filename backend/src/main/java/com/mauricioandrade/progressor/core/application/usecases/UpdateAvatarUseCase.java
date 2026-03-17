package com.mauricioandrade.progressor.core.application.usecases;

import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import java.util.UUID;

public class UpdateAvatarUseCase {

    private final UserRepository userRepository;

    public UpdateAvatarUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void execute(UUID userId, byte[] avatarBytes) {
        userRepository.updateAvatar(userId, avatarBytes);
    }
}
