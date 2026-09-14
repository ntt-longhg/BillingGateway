-- =============================================
-- BillingGateway Schema Initialization Script
-- Generated: 2026-09-10
-- Order: Parent tables first, then child tables
-- =============================================

-- 1. Tenants (no FK dependencies)
CREATE TABLE IF NOT EXISTS tenants (
    id CHAR(36) NOT NULL,
    name VARCHAR(100) NOT NULL,
    client_id VARCHAR(100) NOT NULL,
    client_secret VARCHAR(255) NOT NULL,
    allowed_domains TEXT NOT NULL,
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_tenants_client_id (client_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_tenants_status ON tenants(status);
CREATE INDEX idx_tenants_deleted_at ON tenants(deleted_at);

-- 2. Service Catalogs (no FK dependencies)
CREATE TABLE IF NOT EXISTS service_catalogs (
    id CHAR(36) NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_service_catalogs_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_service_catalogs_deleted_at ON service_catalogs(deleted_at);

-- 3. Pricing Plans (no FK dependencies)
CREATE TABLE IF NOT EXISTS pricing_plans (
    id CHAR(36) NOT NULL,
    code VARCHAR(100) NOT NULL,
    name VARCHAR(255) NOT NULL,
    description TEXT NULL,
    price DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    type VARCHAR(20) NOT NULL DEFAULT 'BALANCE_TOPUP',
    bonus_type VARCHAR(20) NOT NULL DEFAULT 'PERCENTAGE',
    bonus_value DECIMAL(15,2) DEFAULT 0.00,
    credit_limit_action VARCHAR(10) NOT NULL DEFAULT 'NONE',
    credit_limit_value DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_pricing_plans_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_pricing_plans_type ON pricing_plans(type);
CREATE INDEX idx_pricing_plans_status ON pricing_plans(status);
CREATE INDEX idx_pricing_plans_deleted_at ON pricing_plans(deleted_at);

-- 4. Wallets (FK -> tenants)
CREATE TABLE IF NOT EXISTS wallets (
    id CHAR(36) NOT NULL,
    tenant_id CHAR(36) NOT NULL,
    type VARCHAR(10) NOT NULL DEFAULT 'PREPAID',
    balance DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    credit_limit DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(10) NOT NULL DEFAULT 'ACTIVE',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_wallets_tenant_id (tenant_id),
    CONSTRAINT fk_wallets_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_wallets_tenant_id ON wallets(tenant_id);
CREATE INDEX idx_wallets_status ON wallets(status);
CREATE INDEX idx_wallets_type ON wallets(type);

-- 5. Service Prices (FK -> service_catalogs)
CREATE TABLE IF NOT EXISTS service_prices (
    id CHAR(36) NOT NULL,
    service_id CHAR(36) NOT NULL,
    initial_size INT NOT NULL,
    initial_fee DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    subsequent_size INT NOT NULL,
    subsequent_fee DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    is_active TINYINT(1) DEFAULT 1,
    effective_date TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_service_prices_service_catalog FOREIGN KEY (service_id) REFERENCES service_catalogs(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_service_prices_service_id ON service_prices(service_id);
CREATE INDEX idx_service_prices_active ON service_prices(is_active);
CREATE INDEX idx_service_prices_effective_date ON service_prices(effective_date);
CREATE INDEX idx_service_prices_deleted_at ON service_prices(deleted_at);

-- 6. Price Tiers (FK -> service_prices)
CREATE TABLE IF NOT EXISTS price_tiers (
    id CHAR(36) NOT NULL,
    service_price_id CHAR(36) NOT NULL,
    tier VARCHAR(100) NOT NULL,
    basic_fee DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    extended_size INT NOT NULL,
    extended_fee DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    PRIMARY KEY (id),
    CONSTRAINT fk_price_tiers_service_price FOREIGN KEY (service_price_id) REFERENCES service_prices(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_price_tiers_service_price_id ON price_tiers(service_price_id);

-- 7. Credit Adjustments (FK -> wallets)
CREATE TABLE IF NOT EXISTS credit_adjustments (
    id CHAR(36) NOT NULL,
    wallet_id CHAR(36) NOT NULL,
    credit_limit_before DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    credit_limit_after DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    adjustment_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    type VARCHAR(10) NOT NULL,
    reason VARCHAR(255) NOT NULL,
    reference_from VARCHAR(100) NOT NULL,
    reference_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_credit_adjustments_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_credit_adjustments_wallet_id ON credit_adjustments(wallet_id);
CREATE INDEX idx_credit_adjustments_type ON credit_adjustments(type);
CREATE INDEX idx_credit_adjustments_reference ON credit_adjustments(reference_from, reference_id);
CREATE INDEX idx_credit_adjustments_created_at ON credit_adjustments(created_at);

-- 8. Transactions (FK -> wallets)
CREATE TABLE IF NOT EXISTS transactions (
    id CHAR(36) NOT NULL,
    wallet_id CHAR(36) NOT NULL,
    amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    type VARCHAR(10) NOT NULL DEFAULT 'CHARGE',
    balance_before DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    balance_after DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    available_balance_before DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    available_balance_after DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(10) NOT NULL DEFAULT 'SUCCESS',
    description TEXT NULL,
    reference_from VARCHAR(100) NOT NULL,
    reference_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_transactions_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_transactions_wallet_id ON transactions(wallet_id);
CREATE INDEX idx_transactions_type ON transactions(type);
CREATE INDEX idx_transactions_status ON transactions(status);
CREATE INDEX idx_transactions_created_at ON transactions(created_at);
CREATE INDEX idx_transactions_reference ON transactions(reference_from, reference_id);

-- 9. Wallet Plans (FK -> tenants, FK -> pricing_plans)
CREATE TABLE IF NOT EXISTS wallet_plans (
    id CHAR(36) NOT NULL,
    pricing_plan_id CHAR(36) NOT NULL,
    tenant_id CHAR(36) NOT NULL,
    price DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    bonus_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    credited_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    balance_before DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    balance_after DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    credit_limit_before DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    credit_limit_after DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    approved_at TIMESTAMP NULL DEFAULT NULL,
    approved_by VARCHAR(100) NULL DEFAULT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_by VARCHAR(255) NOT NULL,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(255) NULL DEFAULT NULL,
    deleted_at TIMESTAMP NULL DEFAULT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_wallet_plans_pricing_plan FOREIGN KEY (pricing_plan_id) REFERENCES pricing_plans(id),
    CONSTRAINT fk_wallet_plans_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_wallet_plans_tenant_id ON wallet_plans(tenant_id);
CREATE INDEX idx_wallet_plans_pricing_plan_id ON wallet_plans(pricing_plan_id);
CREATE INDEX idx_wallet_plans_status ON wallet_plans(status);
CREATE INDEX idx_wallet_plans_created_at ON wallet_plans(created_at);
CREATE INDEX idx_wallet_plans_deleted_at ON wallet_plans(deleted_at);

-- 10. Usage Logs (FK -> tenants, FK -> service_catalogs)
CREATE TABLE IF NOT EXISTS usage_logs (
    id CHAR(36) NOT NULL,
    tenant_id CHAR(36) NOT NULL,
    service_id CHAR(36) NULL DEFAULT NULL,
    wallet_type_snapshot VARCHAR(10) NOT NULL DEFAULT 'PREPAID',
    total_usage INT NOT NULL,
    total_charged DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    credit_limit_snapshot DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    available_balance_snapshot DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    fee_breakdown LONGTEXT NULL,
    reference_from VARCHAR(100) NOT NULL,
    reference_id VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    CONSTRAINT fk_usage_logs_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_usage_logs_service_catalog FOREIGN KEY (service_id) REFERENCES service_catalogs(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_usage_logs_tenant_id ON usage_logs(tenant_id);
CREATE INDEX idx_usage_logs_service_id ON usage_logs(service_id);
CREATE INDEX idx_usage_logs_reference ON usage_logs(reference_from, reference_id);
CREATE INDEX idx_usage_logs_created_at ON usage_logs(created_at);

-- 11. Invoices (FK -> tenants, FK -> wallets)
CREATE TABLE IF NOT EXISTS invoices (
    id CHAR(36) NOT NULL,
    tenant_id CHAR(36) NOT NULL,
    wallet_id CHAR(36) NOT NULL,
    billing_period VARCHAR(7) NOT NULL,
    total_amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    status VARCHAR(10) NOT NULL DEFAULT 'ISSUED',
    due_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    updated_by VARCHAR(100) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_invoices_tenant_period (tenant_id, billing_period),
    CONSTRAINT fk_invoices_tenant FOREIGN KEY (tenant_id) REFERENCES tenants(id),
    CONSTRAINT fk_invoices_wallet FOREIGN KEY (wallet_id) REFERENCES wallets(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE INDEX idx_invoices_tenant_id ON invoices(tenant_id);
CREATE INDEX idx_invoices_wallet_id ON invoices(wallet_id);
CREATE INDEX idx_invoices_status ON invoices(status);
CREATE INDEX idx_invoices_billing_period ON invoices(billing_period);
CREATE INDEX idx_invoices_due_date ON invoices(due_date);

-- =============================================
-- 12. System Configs (no FK dependencies)
-- Stores key-value configuration for the system (SMTP, OTP, AUTH settings)
-- =============================================
CREATE TABLE IF NOT EXISTS system_configs (
    id CHAR(36) NOT NULL,
    config_key VARCHAR(100) NOT NULL,
    config_value TEXT NOT NULL,
    config_group VARCHAR(50) NOT NULL,
    description VARCHAR(255) NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_system_configs_key (config_key),
    INDEX idx_system_configs_group (config_group)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- Default system configs
INSERT IGNORE INTO system_configs (id, config_key, config_value, config_group, description) VALUES
('00000000-0000-0000-0000-000000000001', 'smtp.host', 'smtp.gmail.com', 'SMTP', 'SMTP server host'),
('00000000-0000-0000-0000-000000000002', 'smtp.port', '587', 'SMTP', 'SMTP server port'),
('00000000-0000-0000-0000-000000000003', 'smtp.username', '', 'SMTP', 'SMTP username for authentication'),
('00000000-0000-0000-0000-000000000004', 'smtp.password', '', 'SMTP', 'SMTP password for authentication'),
('00000000-0000-0000-0000-000000000005', 'smtp.from-email', '', 'SMTP', 'Sender email address'),
('00000000-0000-0000-0000-000000000006', 'otp.expiry_minutes', '5', 'OTP', 'OTP expiry time in minutes'),
('00000000-0000-0000-0000-000000000007', 'otp.length', '6', 'OTP', 'OTP code length'),
('00000000-0000-0000-0000-000000000008', 'auth.allowed_domains', 'dntg.com.vn', 'AUTH', 'Comma-separated allowed email domains'),
('00000000-0000-0000-0000-000000000009', 'auth.token_expiry_hours', '24', 'AUTH', 'Admin session token expiry in hours');

-- =============================================
-- 13. Admin OTPs (no FK dependencies)
-- Stores temporary OTP codes for admin email verification
-- =============================================
CREATE TABLE IF NOT EXISTS admin_otps (
    id CHAR(36) NOT NULL,
    email VARCHAR(255) NOT NULL,
    otp_code VARCHAR(10) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    used TINYINT(1) NOT NULL DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    INDEX idx_admin_otps_email (email),
    INDEX idx_admin_otps_expires_at (expires_at),
    INDEX idx_admin_otps_email_used (email, used)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =============================================
-- 14. Admin Tokens (no FK dependencies)
-- Stores active admin session tokens (UUID) for API authentication
-- =============================================
CREATE TABLE IF NOT EXISTS admin_tokens (
    id CHAR(36) NOT NULL,
    token VARCHAR(36) NOT NULL,
    email VARCHAR(255) NOT NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id),
    UNIQUE KEY uk_admin_tokens_token (token),
    INDEX idx_admin_tokens_email (email),
    INDEX idx_admin_tokens_expires_at (expires_at)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
