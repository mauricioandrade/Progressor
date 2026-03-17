package com.mauricioandrade.progressor.infrastructure.config;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class AvatarColumnMigration implements ApplicationRunner {

    private final JdbcTemplate jdbcTemplate;

    public AvatarColumnMigration(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            String currentType = jdbcTemplate.queryForObject(
                "SELECT data_type FROM information_schema.columns " +
                "WHERE table_name = 'app_users' AND column_name = 'avatar'",
                String.class
            );
            if (!"bytea".equals(currentType)) {
                jdbcTemplate.execute("ALTER TABLE app_users DROP COLUMN IF EXISTS avatar");
                jdbcTemplate.execute("ALTER TABLE app_users ADD COLUMN avatar bytea");
            }
        } catch (Exception ignored) {
        }
    }
}
