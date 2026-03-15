package com.mauricioandrade.progressor.infrastructure.api.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalExceptionHandler {

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

  public record StandardError(LocalDateTime timestamp, Integer status, String error, String message,
                              String path) {

  }
}