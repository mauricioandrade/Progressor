package com.mauricioandrade.progressor.infrastructure.persistence.mappers;

import com.mauricioandrade.progressor.core.domain.user.Nutritionist;
import com.mauricioandrade.progressor.core.domain.user.PersonalTrainer;
import com.mauricioandrade.progressor.core.domain.user.Student;
import com.mauricioandrade.progressor.core.domain.user.User;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.NutritionistEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.PersonalTrainerEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.StudentEntity;
import com.mauricioandrade.progressor.infrastructure.persistence.entities.UserEntity;

public class UserMapper {

  public static UserEntity toEntity(User domain) {
    if (domain instanceof PersonalTrainer pt) {
      PersonalTrainerEntity entity = new PersonalTrainerEntity();
      mapCommonFields(domain, entity);
      entity.setCref(pt.getCref().value());
      return entity;
    } else if (domain instanceof Nutritionist nut) {
      NutritionistEntity entity = new NutritionistEntity();
      mapCommonFields(domain, entity);
      entity.setCrn(nut.getCrn().value());
      return entity;
    } else if (domain instanceof Student stu) {
      StudentEntity entity = new StudentEntity();
      mapCommonFields(domain, entity);
      entity.setPersonalTrainerId(stu.getPersonalTrainerId());
      entity.setNutritionistId(stu.getNutritionistId());
      entity.setDailyWaterGoal(stu.getDailyWaterGoal());
      entity.setCurrentWaterIntake(stu.getCurrentWaterIntake());
      return entity;
    }
    throw new IllegalArgumentException("Unknown User type");
  }

  private static void mapCommonFields(User domain, UserEntity entity) {
    entity.setId(domain.getId());
    entity.setFirstName(domain.getFirstName());
    entity.setLastName(domain.getLastName());
    entity.setEmail(domain.getEmail().value());
    entity.setPassword(domain.getPassword());
    entity.setBirthDate(domain.getBirthDate());
    entity.setAvatar(domain.getAvatar());
  }
}