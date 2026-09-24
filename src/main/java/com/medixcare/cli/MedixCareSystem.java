package com.medixcare.cli;

import com.medixcare.config.DatabaseConfig;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Scanner;

public class MedixCareSystem {

    private static Connection connection;
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        try {
            connection = DatabaseConfig.getCliConnection();
            connection.setAutoCommit(false);
            System.out.println("╔════════════════════════════════════════╗");
            System.out.println("║       WELCOME TO MEDIXCARE             ║");
            System.out.println("╚════════════════════════════════════════╝");

            boolean running = true;
            while (running) {
                displayMainMenu();
                int choice = getIntInput("Enter your choice: ", 1, 9);

                switch (choice) {
                    case 1: manageStaff(); break;
                    case 2: manageDoctors(); break;
                    case 3: managePatients(); break;
                    case 4: manageAppointments(); break;
                    case 5: manageBilling(); break;
                    case 6: manageMedicalRecords(); break;
                    case 7: manageRooms(); break;
                    case 8: generateReports(); break;
                    case 9: running = false; break;
                }
            }
            System.out.println("\nThank you for using MedixCare System. Goodbye!");
        } catch (SQLException e) {
            System.err.println("Database connection error: " + e.getMessage());
            rollbackTransaction();
        } finally {
            closeResources();
        }
    }

    private static void displayMainMenu() {
        System.out.println("\n════════ MAIN MENU ════════════");
        System.out.println("1. Staff Management");
        System.out.println("2. Doctor Management");
        System.out.println("3. Patient Management");
        System.out.println("4. Appointment Management");
        System.out.println("5. Billing Management");
        System.out.println("6. Medical Records Management");
        System.out.println("7. Room Management");
        System.out.println("8. Report Generation");
        System.out.println("9. Exit System");
        System.out.println("══════════════════════════════════");
    }

    // Staff Management
    private static void manageStaff() {
        boolean back = false;
        while (!back) {
            System.out.println("\n════════ STAFF MANAGEMENT ════════");
            System.out.println("1. Add New Staff");
            System.out.println("2. View All Staff");
            System.out.println("3. Update Staff Information");
            System.out.println("4. Delete Staff");
            System.out.println("5. Search Staff");
            System.out.println("6. Back to Main Menu");
            System.out.println("══════════════════════════════════");

            int choice = getIntInput("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1: addStaff(); break;
                case 2: viewAllStaff(); break;
                case 3: updateStaff(); break;
                case 4: deleteStaff(); break;
                case 5: searchStaff(); break;
                case 6: back = true; break;
            }
        }
    }

    private static void addStaff() {
        System.out.println("\n════════ ADD NEW STAFF ════════");
        String staffId = getStringInput("Staff ID (format sXX): ", "^s\\d+$");
        String name = getStringInput("Full Name: ", "^[a-zA-Z ]+$");
        String role = getStringInput("Role: ", "^[a-zA-Z ]+$");
        String phone = getStringInput("Phone Number: ", "^\\d{10,15}$");
        String email = getStringInput("Email: ", "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
        String department = getStringInput("Department: ", "^[a-zA-Z ]+$");

        try {
            String sql = "INSERT INTO Staff (staff_id, name, role, phone, email, department) VALUES (?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, staffId);
            statement.setString(2, name);
            statement.setString(3, role);
            statement.setString(4, phone);
            statement.setString(5, email);
            statement.setString(6, department);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Staff member added successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to add staff member.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "add staff member");
        }
    }

    private static void viewAllStaff() {
        try {
            String sql = "SELECT * FROM Staff ORDER BY name";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ ALL STAFF MEMBERS ════════");
            System.out.printf("%-10s %-20s %-15s %-15s %-25s %-15s%n",
                    "Staff ID", "Name", "Role", "Phone", "Email", "Department");
            System.out.println("------------------------------------------------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10s %-20s %-15s %-15s %-25s %-15s%n",
                        resultSet.getString("staff_id"),
                        resultSet.getString("name"),
                        resultSet.getString("role"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("department"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve staff members");
        }
    }

    private static void updateStaff() {
        System.out.println("\n════════ UPDATE STAFF ════════");
        String staffId = getStringInput("Enter Staff ID to update: ", "^s\\d+$");

        try {
            if (staffExists(staffId)) {
                System.out.println("\n❌ Staff member not found.");
                return;
            }

            String name = getStringInput("Full Name: ", "^[a-zA-Z ]+$");
            String role = getStringInput("Role: ", "^[a-zA-Z ]+$");
            String phone = getStringInput("Phone Number: ", "^\\d{10,15}$");
            String email = getStringInput("Email: ", "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$");
            String department = getStringInput("Department: ", "^[a-zA-Z ]+$");

            String sql = "UPDATE Staff SET name=?, role=?, phone=?, email=?, department=? WHERE staff_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setString(2, role);
            statement.setString(3, phone);
            statement.setString(4, email);
            statement.setString(5, department);
            statement.setString(6, staffId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Staff member updated successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to update staff member.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "update staff member");
        }
    }

    private static void deleteStaff() {
        System.out.println("\n════════ DELETE STAFF ════════");
        String staffId = getStringInput("Enter Staff ID to delete: ", "^s\\d+$");

        try {
            if (staffExists(staffId)) {
                System.out.println("\n❌ Staff member not found.");
                return;
            }

            String sql = "DELETE FROM Staff WHERE staff_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, staffId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Staff member deleted successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to delete staff member.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "delete staff member");
        }
    }

    private static void searchStaff() {
        System.out.println("\n════════ SEARCH STAFF ════════");
        String searchTerm = getStringInput("Enter name or ID to search: ", "^[a-zA-Z0-9 ]+$");

        try {
            String sql = "SELECT * FROM Staff WHERE staff_id LIKE ? OR name LIKE ? ORDER BY name";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ SEARCH RESULTS ════════");
            System.out.printf("%-10s %-20s %-15s %-15s %-25s %-15s%n",
                    "Staff ID", "Name", "Role", "Phone", "Email", "Department");
            System.out.println("------------------------------------------------------------------------------");

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.printf("%-10s %-20s %-15s %-15s %-25s %-15s%n",
                        resultSet.getString("staff_id"),
                        resultSet.getString("name"),
                        resultSet.getString("role"),
                        resultSet.getString("phone"),
                        resultSet.getString("email"),
                        resultSet.getString("department"));
            }

            if (!found) {
                System.out.println("No staff members found matching your search.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "search staff members");
        }
    }

    // Doctor Management
    private static void manageDoctors() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n════════ DOCTOR MANAGEMENT ════════");
            System.out.println("1. Add New Doctor");
            System.out.println("2. View All Doctors");
            System.out.println("3. Update Doctor Information");
            System.out.println("4. Delete Doctor");
            System.out.println("5. Search Doctors by Speciality");
            System.out.println("6. Back to Main Menu");
            System.out.println("══════════════════════════════════");

            int choice = getIntInput("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1: addDoctor(); break;
                case 2: viewAllDoctors(); break;
                case 3: updateDoctor(); break;
                case 4: deleteDoctor(); break;
                case 5: searchDoctorsBySpeciality(); break;
                case 6: back = true; break;
            }
        }
    }

    private static void addDoctor() {
        System.out.println("\n════════ ADD NEW DOCTOR ════════");
        String doctorId = getStringInput("Doctor ID (format drX): ", "^dr\\d+$");
        String speciality = getStringInput("Speciality: ", "^[a-zA-Z ]+$");
        double consultationFee = getDoubleInput("Consultation Fee: ", 10000);

        try {
            String sql = "INSERT INTO Doctors (doctor_id, speciality, consultation_fee) VALUES (?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, doctorId);
            statement.setString(2, speciality);
            statement.setDouble(3, consultationFee);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Doctor added successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to add doctor.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "add doctor");
        }
    }

    private static void viewAllDoctors() {
        try {
            String sql = "SELECT d.doctor_id, d.speciality, d.consultation_fee, s.name AS staff_name " +
                    "FROM Doctors d LEFT JOIN Works_As w ON d.doctor_id = w.doctor_id " +
                    "LEFT JOIN Staff s ON w.staff_id = s.staff_id " +
                    "ORDER BY d.speciality";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ ALL DOCTORS ════════");
            System.out.printf("%-10s %-25s %-20s %-15s%n",
                    "Doctor ID", "Speciality", "Staff Name", "Consultation Fee");
            System.out.println("------------------------------------------------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10s %-25s %-20s $%-14.2f%n",
                        resultSet.getString("doctor_id"),
                        resultSet.getString("speciality"),
                        resultSet.getString("staff_name") != null ? resultSet.getString("staff_name") : "Not assigned",
                        resultSet.getDouble("consultation_fee"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve doctors");
        }
    }

    private static void updateDoctor() {
        System.out.println("\n════════ UPDATE DOCTOR ════════");
        String doctorId = getStringInput("Enter Doctor ID to update: ", "^dr\\d+$");

        try {
            if (doctorExists(doctorId)) {
                System.out.println("\n❌ Doctor not found.");
                return;
            }

            String speciality = getStringInput("Speciality: ", "^[a-zA-Z ]+$");
            double consultationFee = getDoubleInput("Consultation Fee: ", 10000);

            String sql = "UPDATE Doctors SET speciality=?, consultation_fee=? WHERE doctor_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, speciality);
            statement.setDouble(2, consultationFee);
            statement.setString(3, doctorId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Doctor updated successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to update doctor.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "update doctor");
        }
    }

    private static void deleteDoctor() {
        System.out.println("\n════════ DELETE DOCTOR ════════");
        String doctorId = getStringInput("Enter Doctor ID to delete: ", "^dr\\d+$");

        try {
            if (doctorExists(doctorId)) {
                System.out.println("\n❌ Doctor not found.");
                return;
            }

            connection.setAutoCommit(false);

            // First remove from Works_As if exists
            String worksAsSql = "DELETE FROM Works_As WHERE doctor_id=?";
            PreparedStatement worksAsStmt = connection.prepareStatement(worksAsSql);
            worksAsStmt.setString(1, doctorId);
            worksAsStmt.executeUpdate();

            // Then delete from Doctors
            String doctorSql = "DELETE FROM Doctors WHERE doctor_id=?";
            PreparedStatement doctorStmt = connection.prepareStatement(doctorSql);
            doctorStmt.setString(1, doctorId);

            int rowsAffected = doctorStmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Doctor deleted successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to delete doctor.");
            }

            worksAsStmt.close();
            doctorStmt.close();
        } catch (SQLException e) {
            rollbackTransaction();
            handleSQLException(e, "delete doctor");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    private static void searchDoctorsBySpeciality() {
        System.out.println("\n════════ SEARCH DOCTORS BY SPECIALITY ════════");
        String speciality = getStringInput("Enter speciality to search: ", "^[a-zA-Z ]+$");

        try {
            String sql = "SELECT d.doctor_id, d.speciality, d.consultation_fee, s.name AS staff_name " +
                    "FROM Doctors d LEFT JOIN Works_As w ON d.doctor_id = w.doctor_id " +
                    "LEFT JOIN Staff s ON w.staff_id = s.staff_id " +
                    "WHERE d.speciality LIKE ? " +
                    "ORDER BY d.speciality";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + speciality + "%");

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ SEARCH RESULTS ════════");
            System.out.printf("%-10s %-25s %-20s %-15s%n",
                    "Doctor ID", "Speciality", "Staff Name", "Consultation Fee");
            System.out.println("------------------------------------------------------------------------------");

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.printf("%-10s %-25s %-20s $%-14.2f%n",
                        resultSet.getString("doctor_id"),
                        resultSet.getString("speciality"),
                        resultSet.getString("staff_name") != null ? resultSet.getString("staff_name") : "Not assigned",
                        resultSet.getDouble("consultation_fee"));
            }

            if (!found) {
                System.out.println("No doctors found with that speciality.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "search doctors by speciality");
        }
    }

    // Patient Management
    private static void managePatients() {
        boolean back = false;
        while (!back) {
            System.out.println("\n════════ PATIENT MANAGEMENT ════════");
            System.out.println("1. Add New Patient");
            System.out.println("2. View All Patients");
            System.out.println("3. Update Patient Information");
            System.out.println("4. Delete Patient");
            System.out.println("5. Search Patients");
            System.out.println("6. Back to Main Menu");
            System.out.println("══════════════════════════════════");

            int choice = getIntInput("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1: addPatient(); break;
                case 2: viewAllPatients(); break;
                case 3: updatePatient(); break;
                case 4: deletePatient(); break;
                case 5: searchPatients(); break;
                case 6: back = true; break;
            }
        }
    }

    private static void addPatient() {
        System.out.println("\n════════ ADD NEW PATIENT ════════");
        String patientId = getStringInput("Patient ID (format pX): ", "^p\\d+$");
        String name = getStringInput("Full Name: ", "^[a-zA-Z ]+$");
        int age = getIntInput("Age: ", 0, 120);
        String gender = getGenderInput();
        String phone = getStringInput("Phone Number: ", "^\\d{10,15}$");
        String address = getStringInput("Address: ", "^[a-zA-Z0-9 ,.-]+$");
        String medicalHistory = getStringInput("Medical History: ", "^[a-zA-Z0-9 ,.-]+$");

        try {
            String sql = "INSERT INTO Patients (patient_id, name, age, gender, phone, address, medical_history) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, patientId);
            statement.setString(2, name);
            statement.setInt(3, age);
            statement.setString(4, gender);
            statement.setString(5, phone);
            statement.setString(6, address);
            statement.setString(7, medicalHistory);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Patient added successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to add patient.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "add patient");
        }
    }

    private static void viewAllPatients() {
        try {
            String sql = "SELECT * FROM Patients ORDER BY name";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ ALL PATIENTS ════════");
            System.out.printf("%-10s %-20s %-5s %-10s %-15s %-30s %-30s%n",
                    "Patient ID", "Name", "Age", "Gender", "Phone", "Address", "Medical History");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10s %-20s %-5d %-10s %-15s %-30s %-30s%n",
                        resultSet.getString("patient_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getString("gender"),
                        resultSet.getString("phone"),
                        resultSet.getString("address"),
                        resultSet.getString("medical_history"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve patients");
        }
    }

    private static void updatePatient() {
        System.out.println("\n════════ UPDATE PATIENT ════════");
        String patientId = getStringInput("Enter Patient ID to update: ", "^p\\d+$");

        try {
            if (patientExists(patientId)) {
                System.out.println("\n❌ Patient not found.");
                return;
            }

            String name = getStringInput("Full Name: ", "^[a-zA-Z ]+$");
            int age = getIntInput("Age: ", 0, 120);
            String gender = getGenderInput();
            String phone = getStringInput("Phone Number: ", "^\\d{10,15}$");
            String address = getStringInput("Address: ", "^[a-zA-Z0-9 ,.-]+$");
            String medicalHistory = getStringInput("Medical History: ", "^[a-zA-Z0-9 ,.-]+$");

            String sql = "UPDATE Patients SET name=?, age=?, gender=?, phone=?, address=?, medical_history=? WHERE patient_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, name);
            statement.setInt(2, age);
            statement.setString(3, gender);
            statement.setString(4, phone);
            statement.setString(5, address);
            statement.setString(6, medicalHistory);
            statement.setString(7, patientId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Patient updated successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to update patient.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "update patient");
        }
    }

    private static void deletePatient() {
        System.out.println("\n════════ DELETE PATIENT ════════");
        String patientId = getStringInput("Enter Patient ID to delete: ", "^p\\d+$");

        try {
            if (patientExists(patientId)) {
                System.out.println("\n❌ Patient not found.");
                return;
            }

            String sql = "DELETE FROM Patients WHERE patient_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, patientId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Patient deleted successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to delete patient.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "delete patient");
        }
    }

    private static void searchPatients() {
        System.out.println("\n════════ SEARCH PATIENTS ════════");
        String searchTerm = getStringInput("Enter name or ID to search: ", "^[a-zA-Z0-9 ]+$");

        try {
            String sql = "SELECT * FROM Patients WHERE patient_id LIKE ? OR name LIKE ? ORDER BY name";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ SEARCH RESULTS ════════");
            System.out.printf("%-10s %-20s %-5s %-10s %-15s %-30s %-30s%n",
                    "Patient ID", "Name", "Age", "Gender", "Phone", "Address", "Medical History");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.printf("%-10s %-20s %-5d %-10s %-15s %-30s %-30s%n",
                        resultSet.getString("patient_id"),
                        resultSet.getString("name"),
                        resultSet.getInt("age"),
                        resultSet.getString("gender"),
                        resultSet.getString("phone"),
                        resultSet.getString("address"),
                        resultSet.getString("medical_history"));
            }

            if (!found) {
                System.out.println("No patients found matching your search.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "search patients");
        }
    }

    // Appointment Management
    private static void manageAppointments() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n════════ APPOINTMENT MANAGEMENT ════════");
            System.out.println("1. Schedule New Appointment");
            System.out.println("2. View All Appointments");
            System.out.println("3. Update Appointment");
            System.out.println("4. Cancel Appointment");
            System.out.println("5. View Appointment by Date");
            System.out.println("6. Back to Main Menu");
            System.out.println("══════════════════════════════════");

            int choice = getIntInput("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1: scheduleAppointment(); break;
                case 2: viewAllAppointments(); break;
                case 3: updateAppointment(); break;
                case 4: cancelAppointment(); break;
                case 5: viewAppointmentsByDate(); break;
                case 6: back = true; break;
            }
        }
    }

    private static void scheduleAppointment() throws SQLException {
        System.out.println("\n════════ SCHEDULE APPOINTMENT ════════");
        String patientId = getStringInput("Patient ID (format pX): ", "^p\\d+$");
        if (patientExists(patientId)) {
            System.out.println("\n❌ Patient with ID " + patientId + " does not exist.");
            return;
        }

        String appointmentId = getStringInput("Appointment ID (format aX): ", "^a\\d+$");
        LocalDate appointmentDate = getDateInput("Appointment Date (YYYY-MM-DD): ");
        LocalTime appointmentTime = getTimeInput();
        String reason = getStringInput("Reason for Appointment: ", "^[a-zA-Z0-9 ,.-]+$");

        viewAllStaff();
        String staffId = getStringInput("Staff ID (format sX): ", "^s\\d+$");
        if (staffExists(staffId)) {
            System.out.println("\n❌ Staff member with ID " + staffId + " does not exist.");
            return;
        }

        try {
            connection.setAutoCommit(false);

            String appointmentSql = "INSERT INTO Appointments (appointment_id, appointment_date, appointment_time, reason) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement appointmentStmt = connection.prepareStatement(appointmentSql);
            appointmentStmt.setString(1, appointmentId);
            appointmentStmt.setDate(2, Date.valueOf(appointmentDate));
            appointmentStmt.setTime(3, Time.valueOf(appointmentTime));
            appointmentStmt.setString(4, reason);
            appointmentStmt.executeUpdate();

            String hasAppointmentSql = "INSERT INTO Has_Appointment (patient_id, appointment_id) VALUES (?, ?)";
            PreparedStatement hasAppointmentStmt = connection.prepareStatement(hasAppointmentSql);
            hasAppointmentStmt.setString(1, patientId);
            hasAppointmentStmt.setString(2, appointmentId);
            hasAppointmentStmt.executeUpdate();

            String handledBySql = "INSERT INTO Handled_By (appointment_id, staff_id) VALUES (?, ?)";
            PreparedStatement handledByStmt = connection.prepareStatement(handledBySql);
            handledByStmt.setString(1, appointmentId);
            handledByStmt.setString(2, staffId);
            handledByStmt.executeUpdate();

            connection.commit();
            System.out.println("\n✅ Appointment scheduled successfully!");

            appointmentStmt.close();
            hasAppointmentStmt.close();
            handledByStmt.close();
        } catch (SQLException e) {
            rollbackTransaction();
            handleSQLException(e, "schedule appointment");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    private static void viewAllAppointments() {
        try {
            String sql = "SELECT a.appointment_id, p.name AS patient_name, s.name AS staff_name, " +
                    "a.appointment_date, a.appointment_time, a.reason, a.status " +
                    "FROM Appointments a " +
                    "JOIN Has_Appointment ha ON a.appointment_id = ha.appointment_id " +
                    "JOIN Patients p ON ha.patient_id = p.patient_id " +
                    "JOIN Handled_By hb ON a.appointment_id = hb.appointment_id " +
                    "JOIN Staff s ON hb.staff_id = s.staff_id " +
                    "ORDER BY a.appointment_date, a.appointment_time";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ ALL APPOINTMENTS ════════");
            System.out.printf("%-12s %-20s %-20s %-15s %-15s %-30s %-15s%n",
                    "Appointment", "Patient", "Staff", "Date", "Time", "Reason", "Status");
            System.out.println("----------------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-12s %-20s %-20s %-15s %-15s %-30s %-15s%n",
                        resultSet.getString("appointment_id"),
                        resultSet.getString("patient_name"),
                        resultSet.getString("staff_name"),
                        resultSet.getDate("appointment_date"),
                        resultSet.getTime("appointment_time"),
                        resultSet.getString("reason"),
                        resultSet.getString("status"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve appointments");
        }
    }

    private static void updateAppointment() {
        System.out.println("\n════════ UPDATE APPOINTMENT ════════");
        String appointmentId = getStringInput("Enter Appointment ID to update: ", "^a\\d+$");

        try {
            if (appointmentExists(appointmentId)) {
                System.out.println("\n❌ Appointment not found.");
                return;
            }

            LocalDate appointmentDate = getDateInput("Appointment Date (YYYY-MM-DD): ");
            LocalTime appointmentTime = getTimeInput();
            String reason = getStringInput("Reason for Appointment: ", "^[a-zA-Z0-9 ,.-]+$");
            String status = getStringInput("Status (Scheduled/Completed/Cancelled): ", "^(Scheduled|Completed|Cancelled)$");

            String sql = "UPDATE Appointments SET appointment_date=?, appointment_time=?, reason=?, status=? WHERE appointment_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(appointmentDate));
            statement.setTime(2, Time.valueOf(appointmentTime));
            statement.setString(3, reason);
            statement.setString(4, status);
            statement.setString(5, appointmentId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Appointment updated successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to update appointment.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "update appointment");
        }
    }

    private static void cancelAppointment() {
        System.out.println("\n════════ CANCEL APPOINTMENT ════════");
        String appointmentId = getStringInput("Enter Appointment ID to cancel: ", "^a\\d+$");

        try {
            if (appointmentExists(appointmentId)) {
                System.out.println("\n❌ Appointment not found.");
                return;
            }

            String sql = "UPDATE Appointments SET status='Cancelled' WHERE appointment_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, appointmentId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Appointment cancelled successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to cancel appointment.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "cancel appointment");
        }
    }

    private static void viewAppointmentsByDate() {
        System.out.println("\n════════ VIEW APPOINTMENTS BY DATE ════════");
        LocalDate date = getDateInput("Enter date to view appointments (YYYY-MM-DD): ");

        try {
            String sql = "SELECT a.appointment_id, p.name AS patient_name, s.name AS staff_name, " +
                    "a.appointment_date, a.appointment_time, a.reason, a.status " +
                    "FROM Appointments a " +
                    "JOIN Has_Appointment ha ON a.appointment_id = ha.appointment_id " +
                    "JOIN Patients p ON ha.patient_id = p.patient_id " +
                    "JOIN Handled_By hb ON a.appointment_id = hb.appointment_id " +
                    "JOIN Staff s ON hb.staff_id = s.staff_id " +
                    "WHERE a.appointment_date = ? " +
                    "ORDER BY a.appointment_time";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(date));

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ APPOINTMENTS ON " + date + " ════════");
            System.out.printf("%-12s %-20s %-20s %-15s %-30s %-15s%n",
                    "Appointment", "Patient", "Staff", "Time", "Reason", "Status");
            System.out.println("------------------------------------------------------------------------------------------");

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.printf("%-12s %-20s %-20s %-15s %-30s %-15s%n",
                        resultSet.getString("appointment_id"),
                        resultSet.getString("patient_name"),
                        resultSet.getString("staff_name"),
                        resultSet.getTime("appointment_time"),
                        resultSet.getString("reason"),
                        resultSet.getString("status"));
            }

            if (!found) {
                System.out.println("No appointments found for this date.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve appointments by date");
        }
    }

    // Billing Management
    private static void manageBilling() {
        boolean back = false;
        while (!back) {
            System.out.println("\n════════ BILLING MANAGEMENT ════════");
            System.out.println("1. Generate Bill");
            System.out.println("2. View All Bills");
            System.out.println("3. Update Bill");
            System.out.println("4. Process Payment");
            System.out.println("5. View Unpaid Bills");
            System.out.println("6. Back to Main Menu");
            System.out.println("══════════════════════════════════");

            int choice = getIntInput("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1: generateBill(); break;
                case 2: viewAllBills(); break;
                case 3: updateBill(); break;
                case 4: processPayment(); break;
                case 5: viewUnpaidBills(); break;
                case 6: back = true; break;
            }
        }
    }

    private static void generateBill() {
        System.out.println("\n════════ GENERATE BILL ════════");
        String appointmentId = getStringInput("Appointment ID (format aX): ", "^a\\d+$");
        try {
            if (appointmentExists(appointmentId)) {
                System.out.println("\n❌ Appointment with ID " + appointmentId + " does not exist.");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        try {
            if (billExistsForAppointment(appointmentId)) {
                System.out.println("\n❌ A bill already exists for this appointment.");
                return;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        String billId = getStringInput("Bill ID (format bX): ", "^b\\d+$");
        double amount = getDoubleInput("Amount: ", 100000);
        LocalDate date = getDateInput("Bill Date (YYYY-MM-DD): ");
        String description = getStringInput("Description: ", "^[a-zA-Z0-9 ,.-]+$");
        String status = "Pending";

        try {
            connection.setAutoCommit(false);

            String billingSql = "INSERT INTO Billing (bill_id, amount, date, description, status) " +
                    "VALUES (?, ?, ?, ?, ?)";
            PreparedStatement billingStmt = connection.prepareStatement(billingSql);
            billingStmt.setString(1, billId);
            billingStmt.setDouble(2, amount);
            billingStmt.setDate(3, Date.valueOf(date));
            billingStmt.setString(4, description);
            billingStmt.setString(5, status);
            billingStmt.executeUpdate();

            String generatesBillSql = "INSERT INTO Generates_Bill (appointment_id, bill_id) VALUES (?, ?)";
            PreparedStatement generatesBillStmt = connection.prepareStatement(generatesBillSql);
            generatesBillStmt.setString(1, appointmentId);
            generatesBillStmt.setString(2, billId);
            generatesBillStmt.executeUpdate();

            connection.commit();
            System.out.println("\n✅ Bill generated successfully!");

            billingStmt.close();
            generatesBillStmt.close();
        } catch (SQLException e) {
            rollbackTransaction();
            handleSQLException(e, "generate bill");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    private static void viewAllBills() {
        try {
            String sql = "SELECT b.bill_id, p.name AS patient_name, b.amount, b.date, b.description, b.status " +
                    "FROM Billing b " +
                    "JOIN Generates_Bill gb ON b.bill_id = gb.bill_id " +
                    "JOIN Appointments a ON gb.appointment_id = a.appointment_id " +
                    "JOIN Has_Appointment ha ON a.appointment_id = ha.appointment_id " +
                    "JOIN Patients p ON ha.patient_id = p.patient_id " +
                    "ORDER BY b.date DESC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ ALL BILLS ════════");
            System.out.printf("%-10s %-20s %-10s %-15s %-30s %-10s%n",
                    "Bill ID", "Patient", "Amount", "Date", "Description", "Status");
            System.out.println("------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10s %-20s $%-9.2f %-15s %-30s %-10s%n",
                        resultSet.getString("bill_id"),
                        resultSet.getString("patient_name"),
                        resultSet.getDouble("amount"),
                        resultSet.getDate("date"),
                        resultSet.getString("description"),
                        resultSet.getString("status"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve bills");
        }
    }

    private static void updateBill() {
        System.out.println("\n════════ UPDATE BILL ════════");
        String billId = getStringInput("Enter Bill ID to update: ", "^b\\d+$");

        try {
            String sql = "SELECT * FROM Billing WHERE bill_id=?";
            PreparedStatement checkStmt = connection.prepareStatement(sql);
            checkStmt.setString(1, billId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("\n❌ Bill not found.");
                return;
            }

            double amount = getDoubleInput("Amount: ", 100000);
            LocalDate date = getDateInput("Bill Date (YYYY-MM-DD): ");
            String description = getStringInput("Description: ", "^[a-zA-Z0-9 ,.-]+$");
            String status = getStringInput("Status (Paid/Unpaid/Pending): ", "^(Paid|Unpaid|Pending)$");

            sql = "UPDATE Billing SET amount=?, date=?, description=?, status=? WHERE bill_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDouble(1, amount);
            statement.setDate(2, Date.valueOf(date));
            statement.setString(3, description);
            statement.setString(4, status);
            statement.setString(5, billId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Bill updated successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to update bill.");
            }

            rs.close();
            checkStmt.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "update bill");
        }
    }

    private static void processPayment() {
        System.out.println("\n════════ PROCESS PAYMENT ════════");
        String billId = getStringInput("Enter Bill ID to process payment: ", "^b\\d+$");

        try {
            String sql = "SELECT status FROM Billing WHERE bill_id=?";
            PreparedStatement checkStmt = connection.prepareStatement(sql);
            checkStmt.setString(1, billId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("\n❌ Bill not found.");
                return;
            }

            String currentStatus = rs.getString("status");
            if ("Paid".equals(currentStatus)) {
                System.out.println("\n❌ This bill has already been paid.");
                return;
            }

            sql = "UPDATE Billing SET status='Paid' WHERE bill_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, billId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Payment processed successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to process payment.");
            }

            rs.close();
            checkStmt.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "process payment");
        }
    }

    private static void viewUnpaidBills() {
        try {
            String sql = "SELECT b.bill_id, p.name AS patient_name, b.amount, b.date, b.description " +
                    "FROM Billing b " +
                    "JOIN Generates_Bill gb ON b.bill_id = gb.bill_id " +
                    "JOIN Appointments a ON gb.appointment_id = a.appointment_id " +
                    "JOIN Has_Appointment ha ON a.appointment_id = ha.appointment_id " +
                    "JOIN Patients p ON ha.patient_id = p.patient_id " +
                    "WHERE b.status IN ('Unpaid', 'Pending') " +
                    "ORDER BY b.date";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ UNPAID BILLS ════════");
            System.out.printf("%-10s %-20s %-10s %-15s %-30s%n",
                    "Bill ID", "Patient", "Amount", "Date", "Description");
            System.out.println("--------------------------------------------------------------------------");

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.printf("%-10s %-20s $%-9.2f %-15s %-30s%n",
                        resultSet.getString("bill_id"),
                        resultSet.getString("patient_name"),
                        resultSet.getDouble("amount"),
                        resultSet.getDate("date"),
                        resultSet.getString("description"));
            }

            if (!found) {
                System.out.println("No unpaid bills found.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve unpaid bills");
        }
    }

    // Medical Records Management
    private static void manageMedicalRecords() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n════════ MEDICAL RECORDS MANAGEMENT ════════");
            System.out.println("1. Create Medical Record");
            System.out.println("2. View All Medical Records");
            System.out.println("3. Update Medical Record");
            System.out.println("4. View Records by Patient");
            System.out.println("5. View Records by Doctor");
            System.out.println("6. Back to Main Menu");
            System.out.println("══════════════════════════════════");

            int choice = getIntInput("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1: createMedicalRecord(); break;
                case 2: viewAllMedicalRecords(); break;
                case 3: updateMedicalRecord(); break;
                case 4: viewRecordsByPatient(); break;
                case 5: viewRecordsByDoctor(); break;
                case 6: back = true; break;
            }
        }
    }

    private static void createMedicalRecord() throws SQLException {
        System.out.println("\n════════ CREATE MEDICAL RECORD ════════");
        viewAllDoctors();
        String doctorId = getStringInput("Doctor ID (format drX): ", "^dr\\d+$");
        if (doctorExists(doctorId)) {
            System.out.println("\n❌ Doctor with ID " + doctorId + " does not exist.");
            return;
        }

        String recordId = getStringInput("Record ID (format mrX): ", "^mr\\d+$");
        String diagnosis = getStringInput("Diagnosis: ", "^[a-zA-Z0-9 ,.-]+$");
        String prescription = getStringInput("Prescription: ", "^[a-zA-Z0-9 ,.-]+$");
        LocalDate recordDate = getDateInput("Record Date (YYYY-MM-DD): ");

        try {
            connection.setAutoCommit(false);

            String recordSql = "INSERT INTO Medical_Records (record_id, diagnosis, prescription, record_date) " +
                    "VALUES (?, ?, ?, ?)";
            PreparedStatement recordStmt = connection.prepareStatement(recordSql);
            recordStmt.setString(1, recordId);
            recordStmt.setString(2, diagnosis);
            recordStmt.setString(3, prescription);
            recordStmt.setDate(4, Date.valueOf(recordDate));
            recordStmt.executeUpdate();

            String writesSql = "INSERT INTO Writes_Med_Records (doctor_id, record_id) VALUES (?, ?)";
            PreparedStatement writesStmt = connection.prepareStatement(writesSql);
            writesStmt.setString(1, doctorId);
            writesStmt.setString(2, recordId);
            writesStmt.executeUpdate();

            connection.commit();
            System.out.println("\n✅ Medical record created successfully!");

            recordStmt.close();
            writesStmt.close();
        } catch (SQLException e) {
            rollbackTransaction();
            handleSQLException(e, "create medical record");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    private static void viewAllMedicalRecords() {
        try {
            String sql = "SELECT mr.record_id, d.doctor_id, d.speciality, mr.diagnosis, " +
                    "mr.prescription, mr.record_date " +
                    "FROM Medical_Records mr " +
                    "JOIN Writes_Med_Records wmr ON mr.record_id = wmr.record_id " +
                    "JOIN Doctors d ON wmr.doctor_id = d.doctor_id " +
                    "ORDER BY mr.record_date DESC";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ ALL MEDICAL RECORDS ════════");
            System.out.printf("%-10s %-10s %-20s %-30s %-30s %-15s%n",
                    "Record ID", "Doctor ID", "Speciality", "Diagnosis", "Prescription", "Date");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10s %-10s %-20s %-30s %-30s %-15s%n",
                        resultSet.getString("record_id"),
                        resultSet.getString("doctor_id"),
                        resultSet.getString("speciality"),
                        resultSet.getString("diagnosis"),
                        resultSet.getString("prescription"),
                        resultSet.getDate("record_date"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve medical records");
        }
    }

    private static void updateMedicalRecord() {
        System.out.println("\n════════ UPDATE MEDICAL RECORD ════════");
        String recordId = getStringInput("Enter Record ID to update: ", "^mr\\d+$");

        try {
            String sql = "SELECT * FROM Medical_Records WHERE record_id=?";
            PreparedStatement checkStmt = connection.prepareStatement(sql);
            checkStmt.setString(1, recordId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("\n❌ Medical record not found.");
                return;
            }

            String diagnosis = getStringInput("Diagnosis: ", "^[a-zA-Z0-9 ,.-]+$");
            String prescription = getStringInput("Prescription: ", "^[a-zA-Z0-9 ,.-]+$");
            LocalDate recordDate = getDateInput("Record Date (YYYY-MM-DD): ");

            sql = "UPDATE Medical_Records SET diagnosis=?, prescription=?, record_date=? WHERE record_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, diagnosis);
            statement.setString(2, prescription);
            statement.setDate(3, Date.valueOf(recordDate));
            statement.setString(4, recordId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Medical record updated successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to update medical record.");
            }

            rs.close();
            checkStmt.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "update medical record");
        }
    }

    private static void viewRecordsByPatient() {
        System.out.println("\n════════ VIEW RECORDS BY PATIENT ════════");
        String patientId = getStringInput("Enter Patient ID: ", "^p\\d+$");

        try {
            String sql = "SELECT mr.record_id, p.name AS patient_name, d.doctor_id, d.speciality, " +
                    "mr.diagnosis, mr.prescription, mr.record_date " +
                    "FROM Medical_Records mr " +
                    "JOIN Writes_Med_Records wmr ON mr.record_id = wmr.record_id " +
                    "JOIN Doctors d ON wmr.doctor_id = d.doctor_id " +
                    "JOIN Patients p ON p.patient_id = ? " +
                    "ORDER BY mr.record_date DESC";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, patientId);

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ MEDICAL RECORDS FOR PATIENT " + patientId + " ════════");
            System.out.printf("%-10s %-20s %-10s %-20s %-30s %-30s %-15s%n",
                    "Record ID", "Patient", "Doctor ID", "Speciality", "Diagnosis", "Prescription", "Date");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.printf("%-10s %-20s %-10s %-20s %-30s %-30s %-15s%n",
                        resultSet.getString("record_id"),
                        resultSet.getString("patient_name"),
                        resultSet.getString("doctor_id"),
                        resultSet.getString("speciality"),
                        resultSet.getString("diagnosis"),
                        resultSet.getString("prescription"),
                        resultSet.getDate("record_date"));
            }

            if (!found) {
                System.out.println("No medical records found for this patient.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve medical records by patient");
        }
    }

    private static void viewRecordsByDoctor() {
        System.out.println("\n════════ VIEW RECORDS BY DOCTOR ════════");
        String doctorId = getStringInput("Enter Doctor ID: ", "^dr\\d+$");

        try {
            String sql = "SELECT mr.record_id, p.name AS patient_name, d.doctor_id, d.speciality, " +
                    "mr.diagnosis, mr.prescription, mr.record_date " +
                    "FROM Medical_Records mr " +
                    "JOIN Writes_Med_Records wmr ON mr.record_id = wmr.record_id " +
                    "JOIN Doctors d ON wmr.doctor_id = d.doctor_id " +
                    "JOIN Patients p ON p.patient_id IS NOT NULL " +
                    "WHERE d.doctor_id = ? " +
                    "ORDER BY mr.record_date DESC";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, doctorId);

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ MEDICAL RECORDS BY DOCTOR " + doctorId + " ════════");
            System.out.printf("%-10s %-20s %-10s %-20s %-30s %-30s %-15s%n",
                    "Record ID", "Patient", "Doctor ID", "Speciality", "Diagnosis", "Prescription", "Date");
            System.out.println("-------------------------------------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.printf("%-10s %-20s %-10s %-20s %-30s %-30s %-15s%n",
                        resultSet.getString("record_id"),
                        resultSet.getString("patient_name"),
                        resultSet.getString("doctor_id"),
                        resultSet.getString("speciality"),
                        resultSet.getString("diagnosis"),
                        resultSet.getString("prescription"),
                        resultSet.getDate("record_date"));
            }

            if (!found) {
                System.out.println("No medical records found for this doctor.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve medical records by doctor");
        }
    }

    // Room Management
    private static void manageRooms() throws SQLException {
        boolean back = false;
        while (!back) {
            System.out.println("\n════════ ROOM MANAGEMENT ════════");
            System.out.println("1. Add New Room");
            System.out.println("2. View All Rooms");
            System.out.println("3. Update Room Information");
            System.out.println("4. Assign Patient to Room");
            System.out.println("5. Discharge Patient from Room");
            System.out.println("6. Search Rooms");
            System.out.println("7. Back to Main Menu");
            System.out.println("══════════════════════════════════");

            int choice = getIntInput("Enter your choice: ", 1, 7);

            switch (choice) {
                case 1: addRoom(); break;
                case 2: viewAllRooms(); break;
                case 3: updateRoom(); break;
                case 4: assignPatientToRoom(); break;
                case 5: dischargePatientFromRoom(); break;
                case 6: searchRooms(); break;
                case 7: back = true; break;
            }
        }
    }

    private static void addRoom() {
        System.out.println("\n════════ ADD NEW ROOM ════════");
        String roomId = getStringInput("Room ID (format rX): ", "^r\\d+$");
        String roomNumber = getStringInput("Room Number: ", "^\\d+[A-Z]?$");
        String type = getStringInput("Type (Private/Shared): ", "^(Private|Shared)$");
        String status = getStringInput("Status (Available/Occupied): ", "^(Available|Occupied)$");

        try {
            String sql = "INSERT INTO Rooms (room_id, room_number, type, status) VALUES (?, ?, ?, ?)";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, roomId);
            statement.setString(2, roomNumber);
            statement.setString(3, type);
            statement.setString(4, status);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Room added successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to add room.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "add room");
        }
    }

    private static void viewAllRooms() {
        try {
            String sql = "SELECT r.room_id, r.room_number, r.type, r.status, " +
                    "p.name AS patient_name, r.assign_date, r.discharge_date " +
                    "FROM Rooms r LEFT JOIN Occupies o ON r.room_id = o.room_id " +
                    "LEFT JOIN Patients p ON o.patient_id = p.patient_id " +
                    "ORDER BY r.room_number";
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ ALL ROOMS ════════");
            System.out.printf("%-10s %-15s %-10s %-10s %-20s %-15s %-15s%n",
                    "Room ID", "Room Number", "Type", "Status", "Patient", "Assign Date", "Discharge Date");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10s %-15s %-10s %-10s %-20s %-15s %-15s%n",
                        resultSet.getString("room_id"),
                        resultSet.getString("room_number"),
                        resultSet.getString("type"),
                        resultSet.getString("status"),
                        resultSet.getString("patient_name") != null ? resultSet.getString("patient_name") : "N/A",
                        resultSet.getDate("assign_date") != null ? resultSet.getDate("assign_date") : "N/A",
                        resultSet.getDate("discharge_date") != null ? resultSet.getDate("discharge_date") : "N/A");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "retrieve rooms");
        }
    }

    private static void updateRoom() {
        System.out.println("\n════════ UPDATE ROOM ════════");
        String roomId = getStringInput("Enter Room ID to update: ", "^r\\d+$");

        try {
            if (roomExists(roomId)) {
                System.out.println("\n❌ Room not found.");
                return;
            }

            String roomNumber = getStringInput("Room Number: ", "^\\d+[A-Z]?$");
            String type = getStringInput("Type (Private/Shared): ", "^(Private|Shared)$");
            String status = getStringInput("Status (Available/Occupied): ", "^(Available|Occupied)$");

            String sql = "UPDATE Rooms SET room_number=?, type=?, status=? WHERE room_id=?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, roomNumber);
            statement.setString(2, type);
            statement.setString(3, status);
            statement.setString(4, roomId);

            int rowsAffected = statement.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("\n✅ Room updated successfully!");
                connection.commit();
            } else {
                System.out.println("\n❌ Failed to update room.");
            }
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "update room");
        }
    }

    private static void assignPatientToRoom() throws SQLException {
        System.out.println("\n════════ ASSIGN PATIENT TO ROOM ════════");
        viewAllPatients();
        String patientId = getStringInput("Patient ID (format pX): ", "^p\\d+$");
        if (patientExists(patientId)) {
            System.out.println("\n❌ Patient not found.");
            return;
        }

        viewAllRooms();
        String roomId = getStringInput("Room ID (format rX): ", "^r\\d+$");
        if (roomExists(roomId)) {
            System.out.println("\n❌ Room not found.");
            return;
        }

        LocalDate assignDate = getDateInput("Assign Date (YYYY-MM-DD): ");
        LocalDate dischargeDate = getDateInput("Discharge Date (YYYY-MM-DD): ");

        try {
            connection.setAutoCommit(false);

            // Update room status to Occupied
            String updateRoomSql = "UPDATE Rooms SET status='Occupied', assign_date=?, discharge_date=? WHERE room_id=?";
            PreparedStatement updateRoomStmt = connection.prepareStatement(updateRoomSql);
            updateRoomStmt.setDate(1, Date.valueOf(assignDate));
            updateRoomStmt.setDate(2, Date.valueOf(dischargeDate));
            updateRoomStmt.setString(3, roomId);
            updateRoomStmt.executeUpdate();

            // Assign patient to room
            String occupiesSql = "INSERT INTO Occupies (patient_id, room_id, assign_date, discharge_date) VALUES (?, ?, ?, ?)";
            PreparedStatement occupiesStmt = connection.prepareStatement(occupiesSql);
            occupiesStmt.setString(1, patientId);
            occupiesStmt.setString(2, roomId);
            occupiesStmt.setDate(3, Date.valueOf(assignDate));
            occupiesStmt.setDate(4, Date.valueOf(dischargeDate));
            occupiesStmt.executeUpdate();

            connection.commit();
            System.out.println("\n✅ Patient assigned to room successfully!");

            updateRoomStmt.close();
            occupiesStmt.close();
        } catch (SQLException e) {
            rollbackTransaction();
            handleSQLException(e, "assign patient to room");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    private static void dischargePatientFromRoom() {
        System.out.println("\n════════ DISCHARGE PATIENT FROM ROOM ════════");
        String roomId = getStringInput("Enter Room ID to discharge patient: ", "^r\\d+$");

        try {
            if (roomExists(roomId)) {
                System.out.println("\n❌ Room not found.");
                return;
            }

            connection.setAutoCommit(false);

            // Update room status to Available
            String updateRoomSql = "UPDATE Rooms SET status='Available', discharge_date=CURRENT_DATE WHERE room_id=?";
            PreparedStatement updateRoomStmt = connection.prepareStatement(updateRoomSql);
            updateRoomStmt.setString(1, roomId);
            updateRoomStmt.executeUpdate();

            // Remove patient from room
            String deleteOccupiesSql = "DELETE FROM Occupies WHERE room_id=?";
            PreparedStatement deleteOccupiesStmt = connection.prepareStatement(deleteOccupiesSql);
            deleteOccupiesStmt.setString(1, roomId);
            deleteOccupiesStmt.executeUpdate();

            connection.commit();
            System.out.println("\n✅ Patient discharged from room successfully!");

            updateRoomStmt.close();
            deleteOccupiesStmt.close();
        } catch (SQLException e) {
            rollbackTransaction();
            handleSQLException(e, "discharge patient from room");
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                System.err.println("Error resetting auto-commit: " + e.getMessage());
            }
        }
    }

    private static void searchRooms() {
        System.out.println("\n════════ SEARCH ROOMS ════════");
        String searchTerm = getStringInput("Enter room number or status to search: ", "^[a-zA-Z0-9 ]+$");

        try {
            String sql = "SELECT r.room_id, r.room_number, r.type, r.status, " +
                    "p.name AS patient_name, r.assign_date, r.discharge_date " +
                    "FROM Rooms r LEFT JOIN Occupies o ON r.room_id = o.room_id " +
                    "LEFT JOIN Patients p ON o.patient_id = p.patient_id " +
                    "WHERE r.room_number LIKE ? OR r.status LIKE ? " +
                    "ORDER BY r.room_number";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setString(1, "%" + searchTerm + "%");
            statement.setString(2, "%" + searchTerm + "%");

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ SEARCH RESULTS ════════");
            System.out.printf("%-10s %-15s %-10s %-10s %-20s %-15s %-15s%n",
                    "Room ID", "Room Number", "Type", "Status", "Patient", "Assign Date", "Discharge Date");
            System.out.println("--------------------------------------------------------------------------------------------------------");

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.printf("%-10s %-15s %-10s %-10s %-20s %-15s %-15s%n",
                        resultSet.getString("room_id"),
                        resultSet.getString("room_number"),
                        resultSet.getString("type"),
                        resultSet.getString("status"),
                        resultSet.getString("patient_name") != null ? resultSet.getString("patient_name") : "N/A",
                        resultSet.getDate("assign_date") != null ? resultSet.getDate("assign_date") : "N/A",
                        resultSet.getDate("discharge_date") != null ? resultSet.getDate("discharge_date") : "N/A");
            }

            if (!found) {
                System.out.println("No rooms found matching your search.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "search rooms");
        }
    }

    // Report Generation
    private static void generateReports() {
        boolean back = false;
        while (!back) {
            System.out.println("\n════════ REPORT GENERATION ════════");
            System.out.println("1. Patient Statistics Report");
            System.out.println("2. Doctor Activity Report");
            System.out.println("3. Financial Report");
            System.out.println("4. Appointment Trends Report");
            System.out.println("5. Room Utilization Report");
            System.out.println("6. Back to Main Menu");
            System.out.println("══════════════════════════════════");

            int choice = getIntInput("Enter your choice: ", 1, 6);

            switch (choice) {
                case 1: generatePatientStatisticsReport(); break;
                case 2: generateDoctorActivityReport(); break;
                case 3: generateFinancialReport(); break;
                case 4: generateAppointmentTrendsReport(); break;
                case 5: generateRoomUtilizationReport(); break;
                case 6: back = true; break;
            }
        }
    }

    private static void generatePatientStatisticsReport() {
        try {
            // Patient count by age group
            String sql = "SELECT " +
                    "SUM(CASE WHEN age < 18 THEN 1 ELSE 0 END) AS children, " +
                    "SUM(CASE WHEN age BETWEEN 18 AND 40 THEN 1 ELSE 0 END) AS young_adults, " +
                    "SUM(CASE WHEN age BETWEEN 41 AND 60 THEN 1 ELSE 0 END) AS middle_aged, " +
                    "SUM(CASE WHEN age > 60 THEN 1 ELSE 0 END) AS seniors, " +
                    "COUNT(*) AS total " +
                    "FROM Patients";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ PATIENT STATISTICS REPORT ════════");
            System.out.println("\nAge Group Distribution:");
            System.out.println("-----------------------");

            if (resultSet.next()) {
                System.out.printf("Children (<18): %d patients (%.1f%%)%n",
                        resultSet.getInt("children"),
                        resultSet.getInt("children") * 100.0 / resultSet.getInt("total"));
                System.out.printf("Young Adults (18-40): %d patients (%.1f%%)%n",
                        resultSet.getInt("young_adults"),
                        resultSet.getInt("young_adults") * 100.0 / resultSet.getInt("total"));
                System.out.printf("Middle Aged (41-60): %d patients (%.1f%%)%n",
                        resultSet.getInt("middle_aged"),
                        resultSet.getInt("middle_aged") * 100.0 / resultSet.getInt("total"));
                System.out.printf("Seniors (>60): %d patients (%.1f%%)%n",
                        resultSet.getInt("seniors"),
                        resultSet.getInt("seniors") * 100.0 / resultSet.getInt("total"));
                System.out.printf("Total Patients: %d%n", resultSet.getInt("total"));
            }

            // Gender distribution
            sql = "SELECT gender, COUNT(*) AS count FROM Patients GROUP BY gender";
            resultSet = statement.executeQuery(sql);

            System.out.println("\nGender Distribution:");
            System.out.println("--------------------");

            while (resultSet.next()) {
                System.out.printf("%s: %d patients%n",
                        resultSet.getString("gender"),
                        resultSet.getInt("count"));
            }

            // Top medical conditions
            sql = "SELECT diagnosis, COUNT(*) AS count " +
                    "FROM Medical_Records " +
                    "GROUP BY diagnosis " +
                    "ORDER BY count DESC " +
                    "LIMIT 5";
            resultSet = statement.executeQuery(sql);

            System.out.println("\nTop 5 Medical Conditions:");
            System.out.println("-------------------------");

            while (resultSet.next()) {
                System.out.printf("%s: %d cases%n",
                        resultSet.getString("diagnosis"),
                        resultSet.getInt("count"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "generate patient statistics report");
        }
    }

    private static void generateDoctorActivityReport() {
        LocalDate startDate = getDateInput("Enter start date for report (YYYY-MM-DD): ");
        LocalDate endDate = getDateInput("Enter end date for report (YYYY-MM-DD): ");

        try {
            String sql = "SELECT d.doctor_id, d.speciality, " +
                    "COUNT(a.appointment_id) AS appointment_count, " +
                    "COUNT(mr.record_id) AS record_count, " +
                    "SUM(b.amount) AS total_billing " +
                    "FROM Doctors d " +
                    "LEFT JOIN Works_As wa ON d.doctor_id = wa.doctor_id " +
                    "LEFT JOIN Staff s ON wa.staff_id = s.staff_id " +
                    "LEFT JOIN Handled_By hb ON s.staff_id = hb.staff_id " +
                    "LEFT JOIN Appointments a ON hb.appointment_id = a.appointment_id " +
                    "AND a.appointment_date BETWEEN ? AND ? " +
                    "LEFT JOIN Generates_Bill gb ON a.appointment_id = gb.appointment_id " +
                    "LEFT JOIN Billing b ON gb.bill_id = b.bill_id " +
                    "LEFT JOIN Writes_Med_Records wmr ON d.doctor_id = wmr.doctor_id " +
                    "LEFT JOIN Medical_Records mr ON wmr.record_id = mr.record_id " +
                    "AND mr.record_date BETWEEN ? AND ? " +
                    "GROUP BY d.doctor_id, d.speciality " +
                    "ORDER BY appointment_count DESC";

            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            statement.setDate(3, Date.valueOf(startDate));
            statement.setDate(4, Date.valueOf(endDate));

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ DOCTOR ACTIVITY REPORT (" + startDate + " to " + endDate + ") ════════");
            System.out.printf("%-10s %-20s %-15s %-15s %-15s%n",
                    "Doctor ID", "Speciality", "Appointments", "Records", "Total Billing");
            System.out.println("--------------------------------------------------------------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10s %-20s %-15d %-15d $%-14.2f%n",
                        resultSet.getString("doctor_id"),
                        resultSet.getString("speciality"),
                        resultSet.getInt("appointment_count"),
                        resultSet.getInt("record_count"),
                        resultSet.getDouble("total_billing"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "generate doctor activity report");
        }
    }

    private static void generateFinancialReport() {
        LocalDate startDate = getDateInput("Enter start date for report (YYYY-MM-DD): ");
        LocalDate endDate = getDateInput("Enter end date for report (YYYY-MM-DD): ");

        try {
            // Total revenue
            String sql = "SELECT SUM(amount) AS total_revenue FROM Billing " +
                    "WHERE status = 'Paid' AND date BETWEEN ? AND ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ FINANCIAL REPORT (" + startDate + " to " + endDate + ") ════════");

            if (resultSet.next()) {
                System.out.printf("\nTotal Revenue: $%.2f%n", resultSet.getDouble("total_revenue"));
            }

            // Revenue by service type
            sql = "SELECT b.description, SUM(b.amount) AS total " +
                    "FROM Billing b " +
                    "WHERE b.status = 'Paid' AND b.date BETWEEN ? AND ? " +
                    "GROUP BY b.description " +
                    "ORDER BY total DESC";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            resultSet = statement.executeQuery();

            System.out.println("\nRevenue by Service Type:");
            System.out.println("------------------------");

            while (resultSet.next()) {
                System.out.printf("%-30s $%.2f%n",
                        resultSet.getString("description"),
                        resultSet.getDouble("total"));
            }

            // Outstanding payments
            sql = "SELECT SUM(amount) AS outstanding FROM Billing " +
                    "WHERE status IN ('Unpaid', 'Pending') AND date BETWEEN ? AND ?";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            resultSet = statement.executeQuery();

            if (resultSet.next()) {
                System.out.printf("\nOutstanding Payments: $%.2f%n", resultSet.getDouble("outstanding"));
            }

            // Payment trends by month
            sql = "SELECT YEAR(date) AS year, MONTH(date) AS month, SUM(amount) AS monthly_total " +
                    "FROM Billing " +
                    "WHERE status = 'Paid' AND date BETWEEN ? AND ? " +
                    "GROUP BY YEAR(date), MONTH(date) " +
                    "ORDER BY year, month";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            resultSet = statement.executeQuery();

            System.out.println("\nMonthly Payment Trends:");
            System.out.println("-----------------------");

            while (resultSet.next()) {
                System.out.printf("%d-%02d: $%.2f%n",
                        resultSet.getInt("year"),
                        resultSet.getInt("month"),
                        resultSet.getDouble("monthly_total"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "generate financial report");
        }
    }

    private static void generateAppointmentTrendsReport() {
        LocalDate startDate = getDateInput("Enter start date for report (YYYY-MM-DD): ");
        LocalDate endDate = getDateInput("Enter end date for report (YYYY-MM-DD): ");

        try {
            // Total appointments
            String sql = "SELECT COUNT(*) AS total_appointments FROM Appointments " +
                    "WHERE appointment_date BETWEEN ? AND ?";
            PreparedStatement statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));

            ResultSet resultSet = statement.executeQuery();

            System.out.println("\n════════ APPOINTMENT TRENDS REPORT (" + startDate + " to " + endDate + ") ════════");

            if (resultSet.next()) {
                System.out.printf("\nTotal Appointments: %d%n", resultSet.getInt("total_appointments"));
            }

            // Appointments by day of week
            sql = "SELECT DAYNAME(appointment_date) AS day, COUNT(*) AS count " +
                    "FROM Appointments " +
                    "WHERE appointment_date BETWEEN ? AND ? " +
                    "GROUP BY DAYNAME(appointment_date), DAYOFWEEK(appointment_date) " +
                    "ORDER BY DAYOFWEEK(appointment_date)";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            resultSet = statement.executeQuery();

            System.out.println("\nAppointments by Day of Week:");
            System.out.println("---------------------------");

            while (resultSet.next()) {
                System.out.printf("%-10s: %d appointments%n",
                        resultSet.getString("day"),
                        resultSet.getInt("count"));
            }

            // Appointments by doctor
            sql = "SELECT d.doctor_id, d.speciality, COUNT(a.appointment_id) AS appointment_count " +
                    "FROM Doctors d " +
                    "LEFT JOIN Works_As wa ON d.doctor_id = wa.doctor_id " +
                    "LEFT JOIN Staff s ON wa.staff_id = s.staff_id " +
                    "LEFT JOIN Handled_By hb ON s.staff_id = hb.staff_id " +
                    "LEFT JOIN Appointments a ON hb.appointment_id = a.appointment_id " +
                    "AND a.appointment_date BETWEEN ? AND ? " +
                    "GROUP BY d.doctor_id, d.speciality " +
                    "ORDER BY appointment_count DESC";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            resultSet = statement.executeQuery();

            System.out.println("\nAppointments by Doctor:");
            System.out.println("----------------------");

            while (resultSet.next()) {
                System.out.printf("%-10s %-20s: %d appointments%n",
                        resultSet.getString("doctor_id"),
                        resultSet.getString("speciality"),
                        resultSet.getInt("appointment_count"));
            }

            // Common appointment reasons
            sql = "SELECT reason, COUNT(*) AS count " +
                    "FROM Appointments " +
                    "WHERE appointment_date BETWEEN ? AND ? " +
                    "GROUP BY reason " +
                    "ORDER BY count DESC " +
                    "LIMIT 5";
            statement = connection.prepareStatement(sql);
            statement.setDate(1, Date.valueOf(startDate));
            statement.setDate(2, Date.valueOf(endDate));
            resultSet = statement.executeQuery();

            System.out.println("\nTop 5 Appointment Reasons:");
            System.out.println("--------------------------");

            while (resultSet.next()) {
                System.out.printf("%-30s: %d appointments%n",
                        resultSet.getString("reason"),
                        resultSet.getInt("count"));
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "generate appointment trends report");
        }
    }

    private static void generateRoomUtilizationReport() {
        try {
            // Room utilization statistics
            String sql = "SELECT " +
                    "COUNT(*) AS total_rooms, " +
                    "SUM(CASE WHEN status = 'Occupied' THEN 1 ELSE 0 END) AS occupied_rooms, " +
                    "SUM(CASE WHEN status = 'Available' THEN 1 ELSE 0 END) AS available_rooms " +
                    "FROM Rooms";

            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(sql);

            System.out.println("\n════════ ROOM UTILIZATION REPORT ════════");
            System.out.println("\nRoom Utilization Statistics:");
            System.out.println("----------------------------");

            if (resultSet.next()) {
                int totalRooms = resultSet.getInt("total_rooms");
                int occupiedRooms = resultSet.getInt("occupied_rooms");
                int availableRooms = resultSet.getInt("available_rooms");

                System.out.printf("Total Rooms: %d%n", totalRooms);
                System.out.printf("Occupied Rooms: %d (%.1f%%)%n",
                        occupiedRooms,
                        (occupiedRooms * 100.0 / totalRooms));
                System.out.printf("Available Rooms: %d (%.1f%%)%n",
                        availableRooms,
                        (availableRooms * 100.0 / totalRooms));
            }

            // Rooms by type
            sql = "SELECT type, COUNT(*) AS count FROM Rooms GROUP BY type";
            resultSet = statement.executeQuery(sql);

            System.out.println("\nRooms by Type:");
            System.out.println("--------------");

            while (resultSet.next()) {
                System.out.printf("%-10s: %d rooms%n",
                        resultSet.getString("type"),
                        resultSet.getInt("count"));
            }

            // Current room assignments
            sql = "SELECT r.room_number, r.type, p.name AS patient_name, " +
                    "r.assign_date, r.discharge_date " +
                    "FROM Rooms r JOIN Occupies o ON r.room_id = o.room_id " +
                    "JOIN Patients p ON o.patient_id = p.patient_id " +
                    "WHERE r.status = 'Occupied' " +
                    "ORDER BY r.room_number";
            resultSet = statement.executeQuery(sql);

            System.out.println("\nCurrent Room Assignments:");
            System.out.println("------------------------");

            boolean found = false;
            while (resultSet.next()) {
                found = true;
                System.out.printf("%-10s %-10s %-20s %-15s %-15s%n",
                        resultSet.getString("room_number"),
                        resultSet.getString("type"),
                        resultSet.getString("patient_name"),
                        resultSet.getDate("assign_date"),
                        resultSet.getDate("discharge_date"));
            }

            if (!found) {
                System.out.println("No current room assignments.");
            }

            resultSet.close();
            statement.close();
        } catch (SQLException e) {
            handleSQLException(e, "generate room utilization report");
        }
    }

    // Helper Methods
    private static int getIntInput(String prompt, int min, int max) {
        while (true) {
            try {
                System.out.print(prompt);
                int value = Integer.parseInt(scanner.nextLine());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between " + min + " and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static double getDoubleInput(String prompt, double max) {
        while (true) {
            try {
                System.out.print(prompt);
                double value = Double.parseDouble(scanner.nextLine());
                if (value >= 0 && value <= max) {
                    return value;
                }
                System.out.println("Please enter a number between 0 and " + max + ".");
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a valid number.");
            }
        }
    }

    private static String getStringInput(String prompt, String regex) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (input.matches(regex)) {
                return input;
            }
            System.out.println("Invalid input format. Please try again.");
        }
    }

    private static String getGenderInput() {
        while (true) {
            System.out.print("Gender (Male/Female/Other): ");
            String gender = scanner.nextLine().trim();
            if (gender.equalsIgnoreCase("Male") || gender.equalsIgnoreCase("Female") || gender.equalsIgnoreCase("Other")) {
                return gender.substring(0, 1).toUpperCase() + gender.substring(1).toLowerCase();
            }
            System.out.println("Invalid gender. Please enter Male, Female, or Other.");
        }
    }

    private static LocalDate getDateInput(String prompt) {
        while (true) {
            try {
                System.out.print(prompt);
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid date format. Please use YYYY-MM-DD.");
            }
        }
    }

    private static LocalTime getTimeInput() {
        while (true) {
            try {
                System.out.print("Time (HH:MM): ");
                return LocalTime.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.println("Invalid time format. Please use HH:MM.");
            }
        }
    }

    private static boolean staffExists(String staffId) throws SQLException {
        String sql = "SELECT 1 FROM Staff WHERE staff_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, staffId);
        ResultSet resultSet = statement.executeQuery();
        boolean exists = resultSet.next();
        resultSet.close();
        statement.close();
        return !exists;
    }

    private static boolean patientExists(String patientId) throws SQLException {
        String sql = "SELECT 1 FROM Patients WHERE patient_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, patientId);
        ResultSet resultSet = statement.executeQuery();
        boolean exists = resultSet.next();
        resultSet.close();
        statement.close();
        return !exists;
    }

    private static boolean doctorExists(String doctorId) throws SQLException {
        String sql = "SELECT 1 FROM Doctors WHERE doctor_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, doctorId);
        ResultSet resultSet = statement.executeQuery();
        boolean exists = resultSet.next();
        resultSet.close();
        statement.close();
        return !exists;
    }

    private static boolean appointmentExists(String appointmentId) throws SQLException {
        String sql = "SELECT 1 FROM Appointments WHERE appointment_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, appointmentId);
        ResultSet resultSet = statement.executeQuery();
        boolean exists = resultSet.next();
        resultSet.close();
        statement.close();
        return !exists;
    }

    private static boolean roomExists(String roomId) throws SQLException {
        String sql = "SELECT 1 FROM Rooms WHERE room_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, roomId);
        ResultSet resultSet = statement.executeQuery();
        boolean exists = resultSet.next();
        resultSet.close();
        statement.close();
        return !exists;
    }

    private static boolean billExistsForAppointment(String appointmentId) throws SQLException {
        String sql = "SELECT 1 FROM Generates_Bill WHERE appointment_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, appointmentId);
        ResultSet resultSet = statement.executeQuery();
        boolean exists = resultSet.next();
        resultSet.close();
        statement.close();
        return exists;
    }

    private static void handleSQLException(SQLException e, String operation) {
        System.err.println("\n❌ Error occurred while trying to " + operation + ": " + e.getMessage());
        if (e.getSQLState() != null) {
            System.err.println("SQL State: " + e.getSQLState());
        }
        if (e.getErrorCode() != 0) {
            System.err.println("Error Code: " + e.getErrorCode());
        }
        rollbackTransaction();
    }

    private static void rollbackTransaction() {
        try {
            if (connection != null) {
                connection.rollback();
            }
        } catch (SQLException ex) {
            System.err.println("Error during rollback: " + ex.getMessage());
        }
    }

    private static void closeResources() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
            scanner.close();
        } catch (SQLException e) {
            System.err.println("Error closing resources: " + e.getMessage());
        }
    }
}