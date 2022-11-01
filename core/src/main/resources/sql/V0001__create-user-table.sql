CREATE TYPE GENDER AS ENUM ('MALE','FEMALE');

CREATE TABLE IF NOT EXISTS user_account(
    user_id UUID NOT NULL,
    first_name TEXT NOT NULL,
    last_name TEXT NOT NULL,
    gender GENDER NOT NULL,
    dob DATE,
    email TEXT NOT NULL,
    password TEXT NOT NULL,
    activated BOOLEAN DEFAULT false,
    created_at TIMESTAMPTZ
);