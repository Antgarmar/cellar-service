CREATE TABLE IF NOT EXISTS cellar_entries
(
    id             UUID           PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id        UUID           NOT NULL,
    wine_id        UUID           NOT NULL,
    quantity       INTEGER        NOT NULL CHECK (quantity >= 0),
    purchase_date  DATE,
    purchase_price DECIMAL(10, 2),
    location       VARCHAR(100),
    drink_from     DATE,
    drink_until    DATE,
    personal_notes TEXT,
    status         VARCHAR(20)    NOT NULL DEFAULT 'AVAILABLE',
    created_at     TIMESTAMP      NOT NULL DEFAULT NOW(),
    updated_at     TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_cellar_entries_user_id    ON cellar_entries (user_id);
CREATE INDEX IF NOT EXISTS idx_cellar_entries_wine_id    ON cellar_entries (wine_id);
CREATE INDEX IF NOT EXISTS idx_cellar_entries_status     ON cellar_entries (user_id, status);
CREATE INDEX IF NOT EXISTS idx_cellar_entries_drink_dates ON cellar_entries (user_id, drink_from, drink_until);

CREATE TABLE IF NOT EXISTS consumption_log
(
    id          UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    entry_id    UUID        NOT NULL REFERENCES cellar_entries (id) ON DELETE CASCADE,
    user_id     UUID        NOT NULL,
    wine_id     UUID        NOT NULL,
    quantity    INTEGER     NOT NULL CHECK (quantity > 0),
    consumed_at TIMESTAMP   NOT NULL DEFAULT NOW(),
    occasion    VARCHAR(100)
);

CREATE INDEX IF NOT EXISTS idx_consumption_log_entry_id ON consumption_log (entry_id);
CREATE INDEX IF NOT EXISTS idx_consumption_log_user_id  ON consumption_log (user_id);
