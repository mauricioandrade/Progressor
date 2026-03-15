package com.mauricioandrade.progressor.infrastructure.security.jwt;

import com.mauricioandrade.progressor.infrastructure.persistence.repositories.SpringDataUserRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final SpringDataUserRepository userRepository;

  public SecurityFilter(TokenService tokenService, SpringDataUserRepository userRepository) {
    this.tokenService = tokenService;
    this.userRepository = userRepository;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    var token = this.recoverToken(request);
    if (token != null) {
      var email = tokenService.validateToken(token);

      if (!email.isEmpty()) {
        var user = userRepository.findByEmail(email);

        if (user.isPresent()) {
          var role = user.get().getClass().getSimpleName().toUpperCase();
          var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
          var authentication = new UsernamePasswordAuthenticationToken(user.get(), null,
              authorities);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        }
      }
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");
    if (authHeader == null) {
      return null;
    }
    return authHeader.replace("Bearer ", "");
  }
}