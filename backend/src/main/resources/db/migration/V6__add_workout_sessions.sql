CREATE TABLE workout_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_date DATE NOT NULL DEFAULT CURRENT_DATE,
    exercises INT NOT NULL DEFAULT 0,
    sets_completed INT NOT NULL DEFAULT 0,
    tonnage_kg DOUBLE PRECISION NOT NULL DEFAULT 0,
    pr_count INT NOT NULL DEFAULT 0,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_workout_sessions_student ON workout_sessions(student_id, session_date DESC);
