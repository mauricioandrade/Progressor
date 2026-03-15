CREATE TABLE app_users
(
    id         UUID PRIMARY KEY,
    first_name VARCHAR(255) NOT NULL,
    last_name  VARCHAR(255) NOT NULL,
    email      VARCHAR(255) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    birth_date DATE         NOT NULL,
    avatar     BYTEA
);

CREATE TABLE personal_trainers
(
    user_id UUID PRIMARY KEY REFERENCES app_users (id),
    cref    VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE nutritionists
(
    user_id UUID PRIMARY KEY REFERENCES app_users (id),
    crn     VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE students
(
    user_id             UUID PRIMARY KEY REFERENCES app_users (id),
    personal_trainer_id UUID REFERENCES personal_trainers (user_id),
    nutritionist_id     UUID REFERENCES nutritionists (user_id)
);

CREATE TABLE workout_exercises
(
    id               UUID PRIMARY KEY,
    student_id       UUID         NOT NULL REFERENCES students (user_id),
    name             VARCHAR(255) NOT NULL,
    sets             INT          NOT NULL,
    repetitions      INT          NOT NULL,
    measurement_type VARCHAR(50)  NOT NULL,
    weight_in_kg     DOUBLE PRECISION,
    speed            DOUBLE PRECISION,
    time_in_seconds  INT,
    cadence          VARCHAR(255),
    tonnage          DOUBLE PRECISION
);

CREATE TABLE body_measurements
(
    id          UUID PRIMARY KEY,
    student_id  UUID NOT NULL REFERENCES students (user_id),
    recorded_at DATE NOT NULL,
    right_bicep DOUBLE PRECISION,
    left_bicep  DOUBLE PRECISION,
    chest       DOUBLE PRECISION,
    waist       DOUBLE PRECISION,
    abdomen     DOUBLE PRECISION,
    hips        DOUBLE PRECISION,
    left_thigh  DOUBLE PRECISION,
    right_thigh DOUBLE PRECISION,
    right_calf  DOUBLE PRECISION,
    left_calf   DOUBLE PRECISION
);