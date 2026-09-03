CREATE TABLE pharmacies (
    id BIGINT NOT NULL AUTO_INCREMENT,
    name VARCHAR(150) NOT NULL,
    license_number VARCHAR(100) NOT NULL UNIQUE,
    email VARCHAR(150) NOT NULL UNIQUE,
    phone VARCHAR(30) NOT NULL,
    address_line1 VARCHAR(255) NOT NULL,
    address_line2 VARCHAR(255),
    city VARCHAR(100) NOT NULL,
    state VARCHAR(100) NOT NULL,
    postal_code VARCHAR(20) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by BIGINT NOT NULL DEFAULT 0,
    updated_by BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id)
);

CREATE TABLE pharmacy_users (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pharmacy_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    role VARCHAR(50) NOT NULL,
    active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at DATETIME(6) NOT NULL,
    updated_at DATETIME(6) NOT NULL,
    created_by BIGINT NOT NULL DEFAULT 0,
    updated_by BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_pharmacy_user_pharmacy FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id),
    CONSTRAINT uk_pharmacy_user UNIQUE (pharmacy_id, user_id)
);

CREATE TABLE pharmacy_prescriptions (
    id BIGINT NOT NULL AUTO_INCREMENT,
    pharmacy_id BIGINT NOT NULL,
    prescription_id BIGINT NOT NULL,
    patient_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    dispensed_at TIMESTAMP NULL,
    created_at DATETIME(6) NOT NULL,
    created_by BIGINT NOT NULL DEFAULT 0,
    updated_by BIGINT NOT NULL DEFAULT 0,
    PRIMARY KEY (id),
    CONSTRAINT fk_pharmacy_prescription_pharmacy FOREIGN KEY (pharmacy_id) REFERENCES pharmacies(id),
    INDEX idx_pharmacy_prescriptions_pharmacy_created (pharmacy_id, created_at)
);
