package com.mauricioandrade.progressor.infrastructure.security.config;

import com.mauricioandrade.progressor.infrastructure.security.jwt.SecurityFilter;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  private final SecurityFilter securityFilter;

  public SecurityConfig(SecurityFilter securityFilter) {
    this.securityFilter = securityFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
    return httpSecurity.cors(cors -> cors.configurationSource(corsConfigurationSource()))
        .csrf(AbstractHttpConfigurer::disable).sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/forgot-password").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/auth/reset-password").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users/register/personal").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users/register/student").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/users/register/nutritionist").permitAll()
            .requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
            .requestMatchers(HttpMethod.GET, "/api/users/students").hasRole("PERSONALTRAINER")
            .requestMatchers(HttpMethod.GET, "/api/users/students/search")
            .hasRole("PERSONALTRAINER")
            .requestMatchers(HttpMethod.POST, "/api/users/*/assign-trainer")
            .hasRole("PERSONALTRAINER")
            .requestMatchers(HttpMethod.POST, "/api/workouts").hasRole("PERSONALTRAINER")
            .requestMatchers(HttpMethod.GET, "/api/workouts/student/**").hasAnyRole("PERSONALTRAINER", "NUTRITIONIST")
            .requestMatchers(HttpMethod.GET, "/api/workouts/my").hasRole("STUDENT")
            .requestMatchers(HttpMethod.GET, "/api/workouts/today").hasRole("STUDENT")
            .requestMatchers(HttpMethod.POST, "/api/measurements").hasRole("PERSONALTRAINER")
            .requestMatchers(HttpMethod.POST, "/api/measurements/my").hasRole("STUDENT")
            .requestMatchers(HttpMethod.GET, "/api/measurements/student/**")
            .hasAnyRole("PERSONALTRAINER", "NUTRITIONIST")
            .requestMatchers(HttpMethod.GET, "/api/measurements/my").hasRole("STUDENT")
            .requestMatchers(HttpMethod.POST, "/api/workouts/log").hasRole("STUDENT")
            .requestMatchers(HttpMethod.GET, "/api/workouts/history/**").hasRole("STUDENT")
            .requestMatchers(HttpMethod.GET, "/api/workouts/prs").hasRole("STUDENT")
            .requestMatchers(HttpMethod.POST, "/api/checkins/my").hasRole("STUDENT")
            .requestMatchers(HttpMethod.GET, "/api/checkins/my").hasRole("STUDENT")
            .requestMatchers("/api/reports/**").hasAnyRole("PERSONALTRAINER", "STUDENT")
            .requestMatchers(HttpMethod.GET, "/api/users/my-students/nutritionist").hasRole("NUTRITIONIST")
            .requestMatchers(HttpMethod.POST, "/api/users/*/assign-nutritionist").hasRole("NUTRITIONIST")
            .requestMatchers(HttpMethod.GET, "/api/users/students/search/nutritionist").hasRole("NUTRITIONIST")
            .requestMatchers(HttpMethod.POST, "/api/nutrition/meal-plans").hasRole("NUTRITIONIST")
            .requestMatchers(HttpMethod.GET, "/api/nutrition/meal-plans/student/**").hasAnyRole("PERSONALTRAINER", "NUTRITIONIST")
            .requestMatchers(HttpMethod.GET, "/api/nutrition/water/student/**").hasAnyRole("PERSONALTRAINER", "NUTRITIONIST")
            .requestMatchers(HttpMethod.GET, "/api/nutrition/meal-plans/my").hasRole("STUDENT")
            .requestMatchers(HttpMethod.GET, "/api/nutrition/foods/search").hasRole("NUTRITIONIST")
            .requestMatchers(HttpMethod.GET, "/api/nutrition/water").hasRole("STUDENT")
            .requestMatchers(HttpMethod.PATCH, "/api/nutrition/water/intake").hasRole("STUDENT")
            .requestMatchers(HttpMethod.PATCH, "/api/nutrition/water/goal").hasRole("STUDENT")
            .requestMatchers(HttpMethod.PATCH, "/api/nutrition/water/goal/**").hasRole("NUTRITIONIST")
            .requestMatchers(HttpMethod.GET, "/api/users/me").authenticated()
            .requestMatchers(HttpMethod.GET, "/api/users/me/avatar").authenticated()
            .requestMatchers(HttpMethod.PATCH, "/api/users/me/avatar").authenticated()
            .requestMatchers(HttpMethod.POST, "/api/progress-photos").hasRole("STUDENT")
            .requestMatchers(HttpMethod.GET, "/api/progress-photos/my").hasRole("STUDENT")
            .requestMatchers(HttpMethod.DELETE, "/api/progress-photos/*/feedback")
            .hasAnyRole("PERSONALTRAINER", "NUTRITIONIST")
            .requestMatchers(HttpMethod.DELETE, "/api/progress-photos/**").hasRole("STUDENT")
            .requestMatchers(HttpMethod.GET, "/api/progress-photos/student/**")
            .hasAnyRole("PERSONALTRAINER", "NUTRITIONIST")
            .requestMatchers(HttpMethod.PATCH, "/api/progress-photos/*/feedback")
            .hasAnyRole("PERSONALTRAINER", "NUTRITIONIST")
            .requestMatchers(HttpMethod.PATCH, "/api/progress-photos/*/student-notes")
            .hasRole("STUDENT")
            .anyRequest().authenticated())
        .addFilterBefore(securityFilter, UsernamePasswordAuthenticationFilter.class).build();
  }

  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration) throws Exception {
    return authenticationConfiguration.getAuthenticationManager();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  @Bean
  public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration configuration = new CorsConfiguration();
    configuration.setAllowedOrigins(Arrays.asList("http://localhost:5173", "http://localhost:5174"));
    configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
    configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "Accept-Language"));
    configuration.setAllowCredentials(true);

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", configuration);
    return source;
  }
}
