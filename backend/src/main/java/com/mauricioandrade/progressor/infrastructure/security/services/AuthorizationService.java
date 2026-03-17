package com.mauricioandrade.progressor.infrastructure.security.services;

import com.mauricioandrade.progressor.infrastructure.persistence.converters.SearchableEncryptedStringConverter;
import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthorizationService implements UserDetailsService {

  private final SpringDataUserRepository userRepository;

  public AuthorizationService(SpringDataUserRepository userRepository) {
    this.userRepository = userRepository;
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    // Try encrypted form first; fall back to plaintext for legacy rows before migration
    String encryptedUsername = SearchableEncryptedStringConverter.encrypt(username);
    return userRepository.findByEmail(encryptedUsername)
        .or(() -> userRepository.findByEmail(username))
        .orElseThrow(() -> new UsernameNotFoundException("User not found"));
  }
}