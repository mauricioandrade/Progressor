package com.mauricioandrade.progressor.infrastructure.api.controllers;

import com.mauricioandrade.progressor.infrastructure.api.dto.LoginRequest;
import com.mauricioandrade.progressor.infrastructure.api.dto.LoginResponse;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import com.mauricioandrade.progressor.infrastructure.security.jwt.TokenService;
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

  public AuthController(AuthenticationManager authenticationManager, TokenService tokenService) {
    this.authenticationManager = authenticationManager;
    this.tokenService = tokenService;
  }

  @PostMapping("/login")
  public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
    var usernamePassword = new UsernamePasswordAuthenticationToken(request.email(),
        request.password());
    var auth = this.authenticationManager.authenticate(usernamePassword);

    var user = (UserEntity) auth.getPrincipal();
    var role = user.getClass().getSimpleName().toUpperCase();
    var token = tokenService.generateToken(user.getEmail(), role);

    return ResponseEntity.ok(new LoginResponse(token));
  }
}