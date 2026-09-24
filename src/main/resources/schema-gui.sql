-- Schema for the JavaFX GUI app (com.medixcare.gui.MedixCare).
-- Extracted directly from MedixCare#createTables(); the app also runs
-- these CREATE TABLE IF NOT EXISTS statements itself on startup, so running
-- this file by hand is optional (useful mainly for review or CI setup).

CREATE DATABASE IF NOT EXISTS HMS;
USE HMS;

CREATE TABLE IF NOT EXISTS PATIENTS (
    patient_id       VARCHAR(10) PRIMARY KEY,
    name             VARCHAR(50)  NOT NULL,
    age              INT          NOT NULL,
    gender           VARCHAR(10)  NOT NULL,
    phone            VARCHAR(15)  NOT NULL,
    address          VARCHAR(100) NOT NULL,
    medical_history  TEXT
);

CREATE TABLE IF NOT EXISTS STAFF (
    staff_id    VARCHAR(10) PRIMARY KEY,
    name        VARCHAR(50) NOT NULL,
    role        VARCHAR(30) NOT NULL,
    phone       VARCHAR(15) NOT NULL,
    email       VARCHAR(50),
    department  VARCHAR(30) NOT NULL
);

CREATE TABLE IF NOT EXISTS DOCTORS (
    doctor_id         VARCHAR(10) PRIMARY KEY,
    staff_id          VARCHAR(10)   NOT NULL,
    specialty         VARCHAR(30)   NOT NULL,
    consultation_fee  DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (staff_id) REFERENCES STAFF(staff_id)
);

CREATE TABLE IF NOT EXISTS ROOMS (
    room_id         VARCHAR(10) PRIMARY KEY,
    room_number     VARCHAR(10) NOT NULL,
    type            VARCHAR(15) NOT NULL,
    status          VARCHAR(15) NOT NULL,
    assign_date     DATE,
    discharge_date  DATE,
    staff_id        VARCHAR(10),
    FOREIGN KEY (staff_id) REFERENCES STAFF(staff_id)
);

CREATE TABLE IF NOT EXISTS APPOINTMENTS (
    appointment_id     VARCHAR(15) PRIMARY KEY,
    patient_id         VARCHAR(10)  NOT NULL,
    doctor_id          VARCHAR(10)  NOT NULL,
    appointment_date   DATE         NOT NULL,
    appointment_time   TIME         NOT NULL,
    reason             VARCHAR(100) NOT NULL,
    status             VARCHAR(15)  NOT NULL,
    FOREIGN KEY (patient_id) REFERENCES PATIENTS(patient_id),
    FOREIGN KEY (doctor_id) REFERENCES DOCTORS(doctor_id)
);

CREATE TABLE IF NOT EXISTS BILLING (
    bill_id         VARCHAR(15) PRIMARY KEY,
    appointment_id  VARCHAR(15)   NOT NULL,
    amount          DECIMAL(10,2) NOT NULL,
    date            DATE          NOT NULL,
    description     VARCHAR(100)  NOT NULL,
    status          VARCHAR(15)   NOT NULL,
    FOREIGN KEY (appointment_id) REFERENCES APPOINTMENTS(appointment_id)
);
