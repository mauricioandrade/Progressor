package com.mauricioandrade.progressor.core.domain.user;

import com.mauricioandrade.progressor.core.domain.common.Crn;
import com.mauricioandrade.progressor.core.domain.common.Email;
import java.time.LocalDate;
import java.util.UUID;

public class Nutritionist extends User {

  private final Crn crn;

  public Nutritionist(UUID id, String firstName, String lastName, Email email, String password,
      LocalDate birthDate, Crn crn) {
    super(id, firstName, lastName, email, password, birthDate);
    if (crn == null) {
      throw new IllegalArgumentException("CRN is required");
    }
    this.crn = crn;
  }

  public Crn getCrn() {
    return crn;
  }
}