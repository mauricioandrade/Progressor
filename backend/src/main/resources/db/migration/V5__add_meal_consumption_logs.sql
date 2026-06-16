CREATE TABLE meal_consumption_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    student_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    meal_plan_item_id UUID NOT NULL REFERENCES meal_items(id) ON DELETE CASCADE,
    log_date DATE NOT NULL DEFAULT CURRENT_DATE,
    consumed_at TIMESTAMP NOT NULL DEFAULT NOW(),
    CONSTRAINT uq_meal_consumption UNIQUE (student_id, meal_plan_item_id, log_date)
);

CREATE INDEX idx_meal_consumption_student_date ON meal_consumption_logs(student_id, log_date);
