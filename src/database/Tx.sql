-- Parties table
CREATE TABLE swarnlink_schema.parties (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(255),
    mobile_number VARCHAR(255) NOT NULL,
    address VARCHAR(255),
    -- Audit columns from AuditEntity
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Transactions table
CREATE TABLE swarnlink_schema.transactions (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    party_id BIGINT NOT NULL,
    type VARCHAR(50),
    direction VARCHAR(50),
    total_amount DECIMAL(19,2) NOT NULL,
    unit VARCHAR(50),
    is_settled BOOLEAN NOT NULL DEFAULT FALSE,
    tentative_close_date DATE NOT NULL,
    description VARCHAR(255),
    -- Audit columns from AuditEntity
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
         last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- FK to parties
    CONSTRAINT fk_transactions_party
      FOREIGN KEY (party_id)
      REFERENCES swarnlink_schema.parties(id)
      ON DELETE RESTRICT
      ON UPDATE CASCADE
);

-- Transaction Logs table
CREATE TABLE swarnlink_schema.transaction_logs (
    id BIGSERIAL PRIMARY KEY,
    transaction_id BIGINT NOT NULL,
    amount DECIMAL(19,2) NOT NULL,
    log_date DATE NOT NULL,
    description VARCHAR(255),
    -- Audit columns from AuditEntity
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    last_modified_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    -- FK to transactions
    CONSTRAINT fk_logs_transaction
      FOREIGN KEY (transaction_id)
      REFERENCES swarnlink_schema.transactions(id)
      ON DELETE CASCADE
      ON UPDATE CASCADE
);