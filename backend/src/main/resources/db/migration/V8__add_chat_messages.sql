CREATE TABLE chat_messages (
    id          UUID PRIMARY KEY,
    sender_id   UUID        NOT NULL REFERENCES app_users(id),
    receiver_id UUID        NOT NULL REFERENCES app_users(id),
    content     TEXT,
    image_data  BYTEA,
    sent_at     TIMESTAMP   NOT NULL DEFAULT NOW(),
    read_at     TIMESTAMP,
    CONSTRAINT chk_content CHECK (content IS NOT NULL OR image_data IS NOT NULL)
);

CREATE INDEX idx_chat_conversation
    ON chat_messages(LEAST(sender_id, receiver_id), GREATEST(sender_id, receiver_id), sent_at DESC);

CREATE INDEX idx_chat_unread
    ON chat_messages(receiver_id, read_at) WHERE read_at IS NULL;
