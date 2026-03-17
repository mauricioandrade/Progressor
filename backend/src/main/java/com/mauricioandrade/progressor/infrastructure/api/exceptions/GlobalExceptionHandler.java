package com.mauricioandrade.progressor.infrastructure.api.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<StandardError> handleAuthenticationException(AuthenticationException e,
      HttpServletRequest request) {
    StandardError err = new StandardError(LocalDateTime.now(), HttpStatus.UNAUTHORIZED.value(),
        "Unauthorized", "Invalid email or password", request.getRequestURI());
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(err);
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<StandardError> handleIllegalArgumentException(IllegalArgumentException e,
      HttpServletRequest request) {
    StandardError err = new StandardError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
        "Bad Request", e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(IllegalStateException.class)
  public ResponseEntity<StandardError> handleIllegalStateException(IllegalStateException e,
      HttpServletRequest request) {
    StandardError err = new StandardError(LocalDateTime.now(), HttpStatus.CONFLICT.value(),
        "Conflict", e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(HttpStatus.CONFLICT).body(err);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<StandardError> handleValidationException(
      MethodArgumentNotValidException e, HttpServletRequest request) {
    String message = e.getBindingResult().getFieldErrors().stream()
        .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
        .collect(Collectors.joining(", "));
    StandardError err = new StandardError(LocalDateTime.now(), HttpStatus.BAD_REQUEST.value(),
        "Validation Error", message, request.getRequestURI());
    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<StandardError> handleRuntimeException(RuntimeException e,
      HttpServletRequest request) {
    StandardError err = new StandardError(LocalDateTime.now(),
        HttpStatus.INTERNAL_SERVER_ERROR.value(), "Internal Server Error",
        e.getMessage(), request.getRequestURI());
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(err);
  }

  public record StandardError(LocalDateTime timestamp, Integer status, String error, String message,
      String path) {

  }
}
