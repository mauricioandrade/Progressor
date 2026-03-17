package com.mauricioandrade.progressor.infrastructure.security.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

  @Value("${api.security.token.secret:my-secret-key-progressor-v1}")
  private String secret;

  public String generateToken(String email, String role, String userId) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.create().withIssuer("progressor-api").withSubject(email)
          .withClaim("role", role).withClaim("userId", userId)
          .withExpiresAt(generateExpirationDate()).sign(algorithm);
    } catch (JWTCreationException exception) {
      throw new RuntimeException("Error while generating token", exception);
    }
  }

  public String validateToken(String token) {
    try {
      Algorithm algorithm = Algorithm.HMAC256(secret);
      return JWT.require(algorithm).withIssuer("progressor-api").build().verify(token).getSubject();
    } catch (JWTVerificationException exception) {
      return "";
    }
  }

  private Instant generateExpirationDate() {
    return LocalDateTime.now().plusHours(8).toInstant(ZoneOffset.of("-03:00"));
  }
}