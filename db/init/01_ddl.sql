-- admin_users table
CREATE TABLE admin_users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- quotes table
CREATE TABLE quotes (
    id BIGSERIAL PRIMARY KEY,
    quote_no VARCHAR(20) NOT NULL UNIQUE,
    driver_age INTEGER NOT NULL,
    license_color VARCHAR(20) NOT NULL,
    usage_type VARCHAR(20) NOT NULL,
    annual_mileage INTEGER NOT NULL,
    driver_range VARCHAR(20) NOT NULL,
    has_current_insurance BOOLEAN NOT NULL,
    grade INTEGER,
    accident_term INTEGER,
    maker VARCHAR(50) NOT NULL,
    car_name VARCHAR(50) NOT NULL,
    first_registration_ym CHAR(7) NOT NULL,
    vehicle_type VARCHAR(20) NOT NULL,
    vehicle_insurance BOOLEAN NOT NULL,
    annual_premium INTEGER NOT NULL,
    monthly_premium INTEGER NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- quote_breakdowns table
CREATE TABLE quote_breakdowns (
    id BIGSERIAL PRIMARY KEY,
    quote_id BIGINT NOT NULL,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    rate NUMERIC(6,3),
    amount INTEGER,
    display_order INTEGER NOT NULL,
    CONSTRAINT fk_quote FOREIGN KEY(quote_id) REFERENCES quotes(id) ON DELETE CASCADE
);

-- rate_masters table
CREATE TABLE rate_masters (
    id BIGSERIAL PRIMARY KEY,
    category VARCHAR(50) NOT NULL,
    item_code VARCHAR(50) NOT NULL,
    item_name VARCHAR(100) NOT NULL,
    rate NUMERIC(6,3),
    amount INTEGER,
    active BOOLEAN NOT NULL DEFAULT TRUE
);
