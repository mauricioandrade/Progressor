package com.mauricioandrade.progressor.infrastructure.security.jwt;

import com.mauricioandrade.progressor.infrastructure.security.UserPrincipal;
import com.mauricioandrade.progressor.infrastructure.security.services.AuthorizationService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
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
          var user = (UserPrincipal) authorizationService.loadUserByUsername(email);
          var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole()));
          var authentication = new UsernamePasswordAuthenticationToken(user, null, authorities);
          SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (UsernameNotFoundException e) {
          response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not found");
          return;
        }
      }
    }
    filterChain.doFilter(request, response);
  }

  private String recoverToken(HttpServletRequest request) {
    if (request.getCookies() != null) {
      for (var cookie : request.getCookies()) {
        if ("jwt".equals(cookie.getName())) {
          return cookie.getValue();
        }
      }
    }
    var authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ")) {
      return authHeader.substring(7);
    }
    return null;
  }
}