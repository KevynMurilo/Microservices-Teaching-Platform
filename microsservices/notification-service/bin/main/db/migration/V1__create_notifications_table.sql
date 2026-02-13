-- Flyway migration: versioned schema changes applied automatically on startup.
-- V1 = first version. Flyway tracks applied migrations in a flyway_schema_history table.
-- Docs: https://documentation.red-gate.com/fd/migrations-184127470.html

CREATE TABLE tb_notifications (
    id          UUID         PRIMARY KEY,
    order_id    UUID         NOT NULL,
    type        VARCHAR(50)  NOT NULL,
    message     TEXT         NOT NULL,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_notifications_order_id ON tb_notifications (order_id);
