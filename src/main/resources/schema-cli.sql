-- Schema for the console/CLI app (com.medixcare.cli.MedixCareSystem).
--
-- IMPORTANT: unlike the GUI app, the original MedixCareSystem.java never
-- creates its own tables — it assumes a database already exists. This file
-- is reverse-engineered from the INSERT/UPDATE/SELECT statements in the
-- code (a normalized design with junction tables for its many-to-many
-- relationships). Column types/lengths are reasonable best guesses, not
-- taken from an original DDL script. Please review and adjust before use.

CREATE DATABASE IF NOT EXISTS MedixCare;
USE MedixCare;

CREATE TABLE IF NOT EXISTS Staff (
    staff_id    VARCHAR(10) PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    role        VARCHAR(30) NOT NULL,
    phone       VARCHAR(15) NOT NULL,
    email       VARCHAR(50),
    department  VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS Doctors (
    doctor_id          VARCHAR(10) PRIMARY KEY,
    speciality         VARCHAR(30)   NOT NULL,
    consultation_fee   DECIMAL(10,2) NOT NULL
);

-- Which staff member a doctor works as (1:1 in practice, modeled as a link table).
CREATE TABLE IF NOT EXISTS Works_As (
    doctor_id  VARCHAR(10) NOT NULL,
    staff_id   VARCHAR(10) NOT NULL,
    PRIMARY KEY (doctor_id, staff_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id),
    FOREIGN KEY (staff_id)  REFERENCES Staff(staff_id)
);

CREATE TABLE IF NOT EXISTS Patients (
    patient_id       VARCHAR(10) PRIMARY KEY,
    name             VARCHAR(50)  NOT NULL,
    age              INT          NOT NULL,
    gender           VARCHAR(10)  NOT NULL,
    phone            VARCHAR(15)  NOT NULL,
    address          VARCHAR(100) NOT NULL,
    medical_history  TEXT
);

CREATE TABLE IF NOT EXISTS Appointments (
    appointment_id    VARCHAR(15) PRIMARY KEY,
    appointment_date  DATE         NOT NULL,
    appointment_time  TIME         NOT NULL,
    reason            VARCHAR(100) NOT NULL,
    status            VARCHAR(15)  NOT NULL DEFAULT 'Scheduled'
);

-- Which patient a given appointment belongs to.
CREATE TABLE IF NOT EXISTS Has_Appointment (
    patient_id      VARCHAR(10) NOT NULL,
    appointment_id  VARCHAR(15) NOT NULL,
    PRIMARY KEY (patient_id, appointment_id),
    FOREIGN KEY (patient_id)     REFERENCES Patients(patient_id),
    FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id)
);

-- Which staff member (doctor) handled a given appointment.
CREATE TABLE IF NOT EXISTS Handled_By (
    appointment_id  VARCHAR(15) NOT NULL,
    staff_id        VARCHAR(10) NOT NULL,
    PRIMARY KEY (appointment_id, staff_id),
    FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id),
    FOREIGN KEY (staff_id)       REFERENCES Staff(staff_id)
);

CREATE TABLE IF NOT EXISTS Billing (
    bill_id      VARCHAR(15) PRIMARY KEY,
    amount       DECIMAL(10,2) NOT NULL,
    date         DATE          NOT NULL,
    description  VARCHAR(100)  NOT NULL,
    status       VARCHAR(15)   NOT NULL DEFAULT 'Pending'
);

-- Which appointment a bill was generated for.
CREATE TABLE IF NOT EXISTS Generates_Bill (
    appointment_id  VARCHAR(15) NOT NULL,
    bill_id         VARCHAR(15) NOT NULL,
    PRIMARY KEY (appointment_id, bill_id),
    FOREIGN KEY (appointment_id) REFERENCES Appointments(appointment_id),
    FOREIGN KEY (bill_id)        REFERENCES Billing(bill_id)
);

CREATE TABLE IF NOT EXISTS Medical_Records (
    record_id     VARCHAR(15) PRIMARY KEY,
    diagnosis     VARCHAR(200) NOT NULL,
    prescription  VARCHAR(200),
    record_date   DATE         NOT NULL
);

-- Which doctor wrote a given medical record.
CREATE TABLE IF NOT EXISTS Writes_Med_Records (
    doctor_id  VARCHAR(10) NOT NULL,
    record_id  VARCHAR(15) NOT NULL,
    PRIMARY KEY (doctor_id, record_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id),
    FOREIGN KEY (record_id) REFERENCES Medical_Records(record_id)
);

CREATE TABLE IF NOT EXISTS Rooms (
    room_id      VARCHAR(10) PRIMARY KEY,
    room_number  VARCHAR(10) NOT NULL,
    type         VARCHAR(15) NOT NULL,
    status       VARCHAR(15) NOT NULL DEFAULT 'Available'
);

-- Which patient occupies which room, and for how long.
CREATE TABLE IF NOT EXISTS Occupies (
    patient_id      VARCHAR(10) NOT NULL,
    room_id         VARCHAR(10) NOT NULL,
    assign_date     DATE,
    discharge_date  DATE,
    PRIMARY KEY (patient_id, room_id, assign_date),
    FOREIGN KEY (patient_id) REFERENCES Patients(patient_id),
    FOREIGN KEY (room_id)    REFERENCES Rooms(room_id)
);
