package com.mauricioandrade.progressor.infrastructure.security.jwt;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import com.mauricioandrade.progressor.infrastructure.security.services.AuthorizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.hibernate.Hibernate;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class SecurityFilter extends OncePerRequestFilter {

  private final TokenService tokenService;
  private final AuthorizationService authorizationService;

  public SecurityFilter(TokenService tokenService, AuthorizationService authorizationService) {
    this.tokenService = tokenService;
    this.authorizationService = authorizationService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
      FilterChain filterChain) throws ServletException, IOException {
    var token = this.recoverToken(request);

    if (token != null) {
      var email = tokenService.validateToken(token);

      if (email != null && !email.isEmpty()) {
        try {
          var user = (UserEntity) authorizationService.loadUserByUsername(email);
          String roleName = Hibernate.getClass(user).getSimpleName().replace("Entity", "")
              .toUpperCase();
          var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
          var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UsernameNotFoundException ignored) {
        }
      }
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    var authHeader = request.getHeader("Authorization");
    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
      return null;
    }
    return authHeader.substring(7);
  }
}