package com.mauricioandrade.progressor.infrastructure.persistence.adapters;

import com.mauricioandrade.progressor.core.application.ports.UserRepository;
import com.mauricioandrade.progressor.core.domain.common.Email;
import com.mauricioandrade.progressor.core.domain.user.User;
import com.mauricioandrade.progressor.infrastructure.persistence.mappers.UserMapper;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepositoryAdapter implements UserRepository {

  private final SpringDataUserRepository springDataRepository;

  public UserRepositoryAdapter(SpringDataUserRepository springDataRepository) {
    this.springDataRepository = springDataRepository;
  }

  @Override
  public void save(User user) {
    springDataRepository.save(UserMapper.toEntity(user));
  }

  @Override
  public boolean existsByEmail(Email email) {
    return springDataRepository.existsByEmail(email.value());
  }

  @Override
  public boolean existsById(UUID id) {
    return springDataRepository.existsById(id);
  }
}