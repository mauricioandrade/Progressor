ALTER TABLE meal_consumption_logs
  ALTER COLUMN meal_plan_item_id DROP NOT NULL;

ALTER TABLE meal_consumption_logs
  ADD COLUMN food_name VARCHAR(255),
  ADD COLUMN calories_kcal DOUBLE PRECISION,
  ADD COLUMN protein_g DOUBLE PRECISION,
  ADD COLUMN carbs_g DOUBLE PRECISION,
  ADD COLUMN fat_g DOUBLE PRECISION,
  ADD COLUMN quantity DOUBLE PRECISION,
  ADD COLUMN base_unit VARCHAR(50);

ALTER TABLE meal_consumption_logs
  DROP CONSTRAINT uq_meal_consumption;

CREATE UNIQUE INDEX uq_plan_item_consumption
  ON meal_consumption_logs(student_id, meal_plan_item_id, log_date)
  WHERE meal_plan_item_id IS NOT NULL;

ALTER TABLE meal_consumption_logs
  ADD CONSTRAINT chk_consumption_type
  CHECK (meal_plan_item_id IS NOT NULL OR food_name IS NOT NULL);
