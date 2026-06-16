package com.mauricioandrade.progressor.infrastructure.security;

import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class UserPrincipal implements UserDetails {

  private final UUID id;
  private final String email;
  private final String firstName;
  private final String lastName;
  private final String password;
  private final String role;

  public UserPrincipal(UUID id, String email, String firstName, String lastName,
      String password, String role) {
    this.id = id;
    this.email = email;
    this.firstName = firstName;
    this.lastName = lastName;
    this.password = password;
    this.role = role;
  }

  public static UserPrincipal from(UserEntity entity) {
    return new UserPrincipal(entity.getId(), entity.getEmail(), entity.getFirstName(),
        entity.getLastName(), entity.getPassword(), entity.getRole());
  }

  public UUID getId() { return id; }
  public String getEmail() { return email; }
  public String getFirstName() { return firstName; }
  public String getLastName() { return lastName; }
  public String getRole() { return role; }

  @Override public String getUsername() { return email; }
  @Override public String getPassword() { return password; }

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    return List.of(new SimpleGrantedAuthority("ROLE_" + role));
  }

  @Override public boolean isAccountNonExpired() { return true; }
  @Override public boolean isAccountNonLocked() { return true; }
  @Override public boolean isCredentialsNonExpired() { return true; }
  @Override public boolean isEnabled() { return true; }
}
