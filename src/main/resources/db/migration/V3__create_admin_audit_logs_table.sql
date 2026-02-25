CREATE TABLE admin_audit_logs (
    id BIGSERIAL PRIMARY KEY,
    admin_id BIGINT NOT NULL,
    action VARCHAR(255) NOT NULL,
    target_id VARCHAR(255),
    details TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_admin_audit_logs_members FOREIGN KEY (admin_id) REFERENCES members (id)
);
