package com.mauricioandrade.progressor.infrastructure.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class SchemaColumnMigration implements ApplicationRunner {

  private final JdbcTemplate jdbcTemplate;

  public SchemaColumnMigration(JdbcTemplate jdbcTemplate) {
    this.jdbcTemplate = jdbcTemplate;
  }

  @Override
  public void run(ApplicationArguments args) {
    addColumnIfMissing("meal_plans", "cheat_meal", "BOOLEAN DEFAULT FALSE NOT NULL");
    addColumnIfMissing("progress_photos", "student_notes", "TEXT");
  }

  private void addColumnIfMissing(String table, String column, String definition) {
    try {
      Integer count = jdbcTemplate.queryForObject(
          "SELECT COUNT(*) FROM information_schema.columns " +
          "WHERE table_name = ? AND column_name = ?",
          Integer.class, table, column);
      if (count == null || count == 0) {
        jdbcTemplate.execute("ALTER TABLE " + table + " ADD COLUMN " + column + " " + definition);
      }
    } catch (Exception ignored) {
    }
  }
}
