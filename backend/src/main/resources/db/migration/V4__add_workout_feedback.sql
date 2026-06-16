CREATE TABLE workout_feedbacks (
    id UUID PRIMARY KEY,
    student_id UUID NOT NULL,
    trainer_id UUID,
    rating INT NOT NULL CHECK (rating BETWEEN 1 AND 5),
    comment TEXT,
    feedback_date DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_workout_feedbacks_student ON workout_feedbacks(student_id, feedback_date DESC);
CREATE INDEX idx_workout_feedbacks_trainer ON workout_feedbacks(trainer_id, feedback_date DESC);
