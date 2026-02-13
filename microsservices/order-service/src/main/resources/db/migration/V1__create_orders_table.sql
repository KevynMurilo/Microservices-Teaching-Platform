-- Flyway migration: versioned schema changes applied automatically on startup.
-- V1 = first version. Flyway tracks applied migrations in a flyway_schema_history table.
-- Docs: https://documentation.red-gate.com/fd/migrations-184127470.html

CREATE TABLE tb_orders (
                           id            UUID         PRIMARY KEY,
                           customer_id   VARCHAR(255) NOT NULL,
                           total_amount  NUMERIC(19, 2) NOT NULL,
                           status        VARCHAR(50)  NOT NULL DEFAULT 'PENDING',
                           created_at    TIMESTAMP    NOT NULL DEFAULT NOW(),
                           updated_at    TIMESTAMP    NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_orders_customer_id ON tb_orders (customer_id);
CREATE INDEX idx_orders_status ON tb_orders (status);