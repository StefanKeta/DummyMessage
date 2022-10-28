CREATE TYPE GENDER AS ENUM ('MALE','FEMALE');

CREATE TABLE IF NOT EXISTS user_account(
    user_id UUID,
    first_name TEXT,
    last_name TEXT,
    gender GENDER,
    dob DATE,
    email TEXT,
    password TEXT,
    activated BOOLEAN DEFAULT false,
    created_at TIMESTAMP
);