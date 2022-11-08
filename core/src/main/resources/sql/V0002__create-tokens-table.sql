CREATE TABLE IF NOT EXISTS user_tokens(
    token TEXT NOT NULL,
    user_id UUID NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL
);