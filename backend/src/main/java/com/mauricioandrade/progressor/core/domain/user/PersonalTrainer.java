package com.mauricioandrade.progressor.core.domain.user;

import com.mauricioandrade.progressor.core.domain.common.Cref;
import com.mauricioandrade.progressor.core.domain.common.Email;
import java.time.LocalDate;
import java.util.UUID;

public class PersonalTrainer extends User {

  private final Cref cref;

  public PersonalTrainer(UUID id, String firstName, String lastName, Email email, String password,
      LocalDate birthDate, Cref cref) {
    super(id, firstName, lastName, email, password, birthDate);
    if (cref == null) {
      throw new IllegalArgumentException("CREF is required");
    }
    this.cref = cref;
  }

  public Cref getCref() {
    return cref;
  }
}