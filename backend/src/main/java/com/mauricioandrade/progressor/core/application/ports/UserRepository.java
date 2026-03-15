package com.mauricioandrade.progressor.core.application.ports;

import com.mauricioandrade.progressor.core.domain.user.User;
import com.mauricioandrade.progressor.core.domain.common.Email;
import java.util.UUID;

public interface UserRepository {

  void save(User user);

  boolean existsByEmail(Email email);

  boolean existsById(UUID id);
}