-- workout_exercises
CREATE INDEX idx_workout_exercises_student_id   ON workout_exercises (student_id);
CREATE INDEX idx_workout_exercises_block_id     ON workout_exercises (block_id);

-- workout_plans / workout_blocks
CREATE INDEX idx_workout_plans_student_id       ON workout_plans (student_id);
CREATE INDEX idx_workout_blocks_workout_plan_id ON workout_blocks (workout_plan_id);

-- body_measurements
CREATE INDEX idx_body_measurements_student_id   ON body_measurements (student_id);

-- meal_plans
CREATE INDEX idx_meal_plans_student_id          ON meal_plans (student_id);

-- connection_requests
CREATE INDEX idx_conn_req_professional          ON connection_requests (professional_id, professional_role, status);
CREATE INDEX idx_conn_req_student               ON connection_requests (student_id, status);

-- workout_log (if table exists)
DO $$
BEGIN
  IF EXISTS (SELECT FROM pg_tables WHERE tablename = 'workout_log') THEN
    EXECUTE 'CREATE INDEX IF NOT EXISTS idx_workout_log_student ON workout_log (student_id)';
    EXECUTE 'CREATE INDEX IF NOT EXISTS idx_workout_log_exercise ON workout_log (exercise_id)';
  END IF;
END $$;

-- progress_photos
DO $$
BEGIN
  IF EXISTS (SELECT FROM pg_tables WHERE tablename = 'progress_photos') THEN
    EXECUTE 'CREATE INDEX IF NOT EXISTS idx_progress_photos_student ON progress_photos (student_id)';
  END IF;
END $$;
