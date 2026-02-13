CREATE TABLE tb_payments (
    id             UUID         PRIMARY KEY,
    order_id       UUID         NOT NULL,
    amount         NUMERIC(19, 2) NOT NULL,
    status         VARCHAR(50)  NOT NULL,
    failure_reason VARCHAR(500),
    processed_at   TIMESTAMP,

    -- UNIQUE constraint on order_id: database-level idempotency protection.
    -- Even if the application-level check fails (race condition), the DB prevents duplicates.
    CONSTRAINT uq_payments_order_id UNIQUE (order_id)
);

CREATE INDEX idx_payments_order_id ON tb_payments (order_id);
CREATE INDEX idx_payments_status ON tb_payments (status);
