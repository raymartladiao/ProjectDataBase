CREATE DATABASE IF NOT EXISTS barangay_records;
USE barangay_records;

-- =========================================================
-- TABLE 1: residents  (main table)
-- =========================================================
CREATE TABLE IF NOT EXISTS residents (
    resident_id INT AUTO_INCREMENT PRIMARY KEY,
    first_name VARCHAR(80) NOT NULL,
    middle_name VARCHAR(80) NULL,          -- optional
    last_name VARCHAR(80) NOT NULL,
    sex VARCHAR(20) NOT NULL,
    birth_date DATE NOT NULL,
    address VARCHAR(255) NOT NULL,
    contact_no VARCHAR(30),
    civil_status VARCHAR(30)
);

-- =========================================================
-- TABLE 2: certificate_requests
-- One resident can have many certificate requests
-- =========================================================
CREATE TABLE IF NOT EXISTS certificate_requests (
    request_id INT AUTO_INCREMENT PRIMARY KEY,
    resident_id INT NOT NULL,
    certificate_type VARCHAR(60) NOT NULL,
    purpose VARCHAR(255) NOT NULL,
    date_requested DATE NOT NULL,
    date_issued DATE NULL,
    barangay_voter ENUM('Yes','No') NOT NULL DEFAULT 'No',
    electorate ENUM('Yes','No') NOT NULL DEFAULT 'No',
    status VARCHAR(20) NOT NULL DEFAULT 'Pending',
    CONSTRAINT fk_cert_resident
        FOREIGN KEY (resident_id) REFERENCES residents(resident_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- =========================================================
-- TABLE 3: blotter_records
-- Incident / complaint reports filed by a resident
-- =========================================================
CREATE TABLE IF NOT EXISTS blotter_records (
    blotter_id INT AUTO_INCREMENT PRIMARY KEY,
    complainant_id INT NOT NULL,
    respondent_name VARCHAR(160) NOT NULL,
    incident_date DATE NOT NULL,
    incident_place VARCHAR(255) NOT NULL,
    incident_details TEXT NOT NULL,
    date_filed DATE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'Open',
    CONSTRAINT fk_blotter_resident
        FOREIGN KEY (complainant_id) REFERENCES residents(resident_id)
        ON UPDATE CASCADE
        ON DELETE CASCADE
);

-- =========================================================
-- Optional sample records
-- =========================================================
INSERT INTO residents
(first_name, middle_name, last_name, sex, birth_date, address, contact_no, civil_status)
VALUES
('Juan', 'Reyes', 'Dela Cruz', 'Male', '2000-01-15',
 'Barangay Poblacion', '09123456789', 'Single');

INSERT INTO certificate_requests
(resident_id, certificate_type, purpose, date_requested, date_issued,
 barangay_voter, electorate, status)
VALUES
(1, 'Barangay Clearance', 'Employment', '2026-11-03', '2026-11-03', 'Yes', 'Yes', 'Issued');

INSERT INTO blotter_records
(complainant_id, respondent_name, incident_date, incident_place, incident_details, date_filed, status)
VALUES
(1, 'Pedro Santos', '2026-11-01', 'Purok 2, Poblacion',
 'Noise disturbance late at night.', '2026-11-02', 'Open');
