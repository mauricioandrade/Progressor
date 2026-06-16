package com.mauricioandrade.progressor.infrastructure.security;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class RateLimitFilter extends OncePerRequestFilter {

  private static final String LOGIN_PATH = "/api/auth/login";
  private static final String FORGOT_PATH = "/api/auth/forgot-password";
  private static final String RESET_PATH = "/api/auth/reset-password";

  private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
  private final Map<String, Bucket> forgotBuckets = new ConcurrentHashMap<>();
  private final Map<String, Bucket> resetBuckets = new ConcurrentHashMap<>();

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    String path = request.getServletPath();
    if (!"POST".equals(request.getMethod())) {
      filterChain.doFilter(request, response);
      return;
    }

    String ip = getClientIp(request);
    Bucket bucket = switch (path) {
      case LOGIN_PATH -> loginBuckets.computeIfAbsent(ip, k -> loginBucket());
      case FORGOT_PATH -> forgotBuckets.computeIfAbsent(ip, k -> slowBucket());
      case RESET_PATH -> resetBuckets.computeIfAbsent(ip, k -> slowBucket());
      default -> null;
    };

    if (bucket != null && !bucket.tryConsume(1)) {
      response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
      response.setContentType(MediaType.APPLICATION_JSON_VALUE);
      response.getWriter().write("{\"error\":\"Too many requests. Try again later.\"}");
      return;
    }

    filterChain.doFilter(request, response);
  }

  private Bucket loginBucket() {
    return Bucket.builder()
        .addLimit(Bandwidth.builder().capacity(5).refillIntervally(5, Duration.ofMinutes(1)).build())
        .build();
  }

  private Bucket slowBucket() {
    return Bucket.builder()
        .addLimit(Bandwidth.builder().capacity(3).refillIntervally(3, Duration.ofMinutes(15)).build())
        .build();
  }

  private String getClientIp(HttpServletRequest request) {
    return request.getRemoteAddr();
  }
}
