package com.mauricioandrade.progressor.infrastructure.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class PushTokenColumnMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public PushTokenColumnMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM information_schema.columns " +
                "WHERE table_name = 'app_users' AND column_name = 'push_token'",
                Integer.class
            );
            if (count == null || count == 0) {
                jdbcTemplate.execute("ALTER TABLE app_users ADD COLUMN push_token VARCHAR(255)");
            }
        } catch (Exception ignored) {
        }
    }
}
