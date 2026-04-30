CREATE INDEX idx_users_status ON users (status);

CREATE UNIQUE INDEX roles_one_default
    ON roles (is_default)
    WHERE is_default = TRUE;

CREATE INDEX idx_user_roles_role_id ON user_roles (role_id);

CREATE INDEX idx_role_permissions_permission_id ON role_permissions (permission_id);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens (user_id);
CREATE INDEX idx_refresh_tokens_expires_at ON refresh_tokens (expires_at);
CREATE INDEX idx_refresh_tokens_active_by_user
    ON refresh_tokens (user_id, expires_at)
    WHERE revoked_at IS NULL;

CREATE INDEX idx_password_reset_tokens_user_id ON password_reset_tokens (user_id);
CREATE INDEX idx_password_reset_tokens_expires_at ON password_reset_tokens (expires_at);
CREATE INDEX idx_password_reset_tokens_active_by_user
    ON password_reset_tokens (user_id, expires_at)
    WHERE used_at IS NULL;
