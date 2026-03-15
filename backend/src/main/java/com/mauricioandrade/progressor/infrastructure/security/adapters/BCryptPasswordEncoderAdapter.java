package com.mauricioandrade.progressor.infrastructure.security.adapters;

import com.mauricioandrade.progressor.core.application.ports.PasswordEncoder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoderAdapter implements PasswordEncoder {

  private final BCryptPasswordEncoder bCryptPasswordEncoder;

  public BCryptPasswordEncoderAdapter() {
    this.bCryptPasswordEncoder = new BCryptPasswordEncoder();
  }

  @Override
  public String encode(String rawPassword) {
    return bCryptPasswordEncoder.encode(rawPassword);
  }
}