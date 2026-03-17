package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.usecases.ForgotPasswordUseCase;
import com.mauricioandrade.progressor.core.application.usecases.ResetPasswordUseCase;
import com.mauricioandrade.progressor.infrastructure.api.dto.ForgotPasswordRequest;
import com.mauricioandrade.progressor.infrastructure.api.dto.LoginRequest;
import com.mauricioandrade.progressor.infrastructure.api.dto.LoginResponse;
import com.mauricioandrade.progressor.infrastructure.api.dto.ResetPasswordRequest;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import com.mauricioandrade.progressor.infrastructure.security.jwt.TokenService;
import jakarta.validation.Valid;
import org.hibernate.Hibernate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final AuthenticationManager authenticationManager;
  private final TokenService tokenService;
  private final ForgotPasswordUseCase forgotPasswordUseCase;
  private final ResetPasswordUseCase resetPasswordUseCase;

  public AuthController(AuthenticationManager authenticationManager, TokenService tokenService,
      ForgotPasswordUseCase forgotPasswordUseCase, ResetPasswordUseCase resetPasswordUseCase) {
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
    this.forgotPasswordUseCase = forgotPasswordUseCase;
    this.resetPasswordUseCase = resetPasswordUseCase;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(),
        request.password());
    var auth = this.authenticationManager.authenticate(usernamePassword);

    var user = (UserEntity) auth.getPrincipal();

    String role = Hibernate.getClass(user).getSimpleName().replace("Entity", "").toUpperCase();

    var token = tokenService.generateToken(user.getEmail(), role, user.getId().toString());

    return ResponseEntity.ok(new LoginResponse(token));
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    forgotPasswordUseCase.execute(request.email());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    boolean success = resetPasswordUseCase.execute(request.token(), request.newPassword());
    return success ? ResponseEntity.ok().build() : ResponseEntity.badRequest().build();
  }
}