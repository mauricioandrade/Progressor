-- =====================================================================
-- V2: Opt-in consent flow (ConnectionRequest) + Workout Plan hierarchy
-- =====================================================================

-- Professional -> Student invitation/consent table
CREATE TABLE connection_requests
(
    id                UUID         PRIMARY KEY,
    professional_id   UUID         NOT NULL,
    student_id        UUID         NOT NULL REFERENCES students (user_id),
    professional_name VARCHAR(255) NOT NULL,
    professional_role VARCHAR(10)  NOT NULL CHECK (professional_role IN ('COACH', 'NUTRI')),
    status            VARCHAR(10)  NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'ACCEPTED', 'REJECTED')),
    created_at        TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_connection_request UNIQUE (professional_id, student_id, professional_role)
);

-- Top-level workout program for a student
CREATE TABLE workout_plans
(
    id         UUID         PRIMARY KEY,
    student_id UUID         NOT NULL REFERENCES students (user_id),
    trainer_id UUID,
    name       VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Workout splits/blocks within a plan (e.g. "Treino A - Superior", "Treino B - Inferior")
CREATE TABLE workout_blocks
(
    id              UUID         PRIMARY KEY,
    workout_plan_id UUID         NOT NULL REFERENCES workout_plans (id) ON DELETE CASCADE,
    name            VARCHAR(255) NOT NULL,
    position        INT          NOT NULL DEFAULT 0
);

-- Link existing exercises to a block (nullable for backward compatibility)
ALTER TABLE workout_exercises
    ADD COLUMN block_id UUID REFERENCES workout_blocks (id) ON DELETE SET NULL;
