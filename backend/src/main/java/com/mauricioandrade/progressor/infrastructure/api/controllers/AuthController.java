package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.core.application.usecases.ForgotPasswordUseCase;
import com.mauricioandrade.progressor.core.application.usecases.ResetPasswordUseCase;
import com.mauricioandrade.progressor.infrastructure.api.dto.ForgotPasswordRequest;
import com.mauricioandrade.progressor.infrastructure.api.dto.LoginRequest;
import com.mauricioandrade.progressor.infrastructure.api.dto.LoginResponse;
import com.mauricioandrade.progressor.infrastructure.api.dto.ResetPasswordRequest;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import com.mauricioandrade.progressor.infrastructure.security.jwt.TokenService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.time.Duration;
import java.time.Instant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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

  @Value("${app.cookie.secure:false}")
  private boolean cookieSecure;

  @Value("${app.cookie.same-site:Lax}")
  private String cookieSameSite;

  public AuthController(AuthenticationManager authenticationManager, TokenService tokenService,
      ForgotPasswordUseCase forgotPasswordUseCase, ResetPasswordUseCase resetPasswordUseCase) {
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
    this.forgotPasswordUseCase = forgotPasswordUseCase;
    this.resetPasswordUseCase = resetPasswordUseCase;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request,
      HttpServletResponse response) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(),
        request.password());
    var auth = this.authenticationManager.authenticate(usernamePassword);

    var user = (UserEntity) auth.getPrincipal();
    String role = user.getRole();
    String token = tokenService.generateToken(user.getEmail(), role, user.getId().toString());

    ResponseCookie cookie = ResponseCookie.from("jwt", token)
        .httpOnly(true)
        .secure(cookieSecure)
        .path("/")
        .maxAge(Duration.ofHours(8))
        .sameSite(cookieSameSite)
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());

    String expiresAt = Instant.now().plusSeconds(8 * 3600).toString();
    return ResponseEntity.ok(
        new LoginResponse(user.getId().toString(), user.getEmail(), role, expiresAt));
  }

  @PostMapping("/logout")
  public ResponseEntity<Void> logout(HttpServletResponse response) {
    ResponseCookie cookie = ResponseCookie.from("jwt", "")
        .httpOnly(true)
        .secure(cookieSecure)
        .path("/")
        .maxAge(0)
        .sameSite(cookieSameSite)
        .build();
    response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/forgot-password")
  public ResponseEntity<Void> forgotPassword(@Valid @RequestBody ForgotPasswordRequest request) {
    forgotPasswordUseCase.execute(request.email());
    return ResponseEntity.ok().build();
  }

  @PostMapping("/reset-password")
  public ResponseEntity<Void> resetPassword(@Valid @RequestBody ResetPasswordRequest request) {
    resetPasswordUseCase.execute(request.token(), request.newPassword());
    return ResponseEntity.ok().build();
  }
}