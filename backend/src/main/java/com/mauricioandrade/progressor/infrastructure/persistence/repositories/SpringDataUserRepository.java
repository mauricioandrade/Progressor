package com.mauricioandrade.progressor.infrastructure.persistence.repositories;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SpringDataUserRepository extends JpaRepository<UserEntity, UUID> {

  boolean existsByEmail(String email);

  Optional<UserEntity> findByEmail(String email);

  Optional<UserEntity> findByResetToken(String resetToken);

  List<UserEntity> findAllByPushTokenIsNotNull();

  @Query("SELECT COUNT(u) > 0 FROM UserEntity u WHERE u.id = :id AND u.avatar IS NOT NULL")
  boolean hasAvatar(@Param("id") UUID id);
}