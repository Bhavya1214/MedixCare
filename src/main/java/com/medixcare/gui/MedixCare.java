package com.medixcare.gui;

import com.medixcare.config.DatabaseConfig;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class MedixCare extends Application {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    private Connection connection;
    private Stage primaryStage;
    private BorderPane rootLayout;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("MedixCare - Hospital Management System");

        try {
            connection = DatabaseConfig.getGuiConnection();
            createTables(connection);
            initRootLayout();
            insertInitialData(connection);
            showMainMenu();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to connect to database: " + e.getMessage());
        }
    }

    private void initRootLayout() {
        rootLayout = new BorderPane();
        rootLayout.setPadding(new Insets(20));
        rootLayout.setStyle("-fx-background-color: #f0f8ff;");

        Scene scene = new Scene(rootLayout, 1000, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void showMainMenu() {
        VBox menuBox = new VBox(20);
        menuBox.setAlignment(Pos.CENTER);
        menuBox.setPadding(new Insets(20));

        Label titleLabel = new Label("MedixCare");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 36));
        titleLabel.setTextFill(Color.DARKBLUE);

        Button patientBtn = createMenuButton("Patient Management");
        Button doctorBtn = createMenuButton("Doctor Management");
        Button staffBtn = createMenuButton("Staff Management");
        Button roomBtn = createMenuButton("Room Management");
        Button appointmentBtn = createMenuButton("Appointment Management");
        Button billingBtn = createMenuButton("Billing Management");
        Button reportBtn = createMenuButton("Reports");
        Button exitBtn = createMenuButton("Exit");

        patientBtn.setOnAction(e -> showPatientManagement());
        doctorBtn.setOnAction(e -> showDoctorManagement());
        staffBtn.setOnAction(e -> showStaffManagement());
        roomBtn.setOnAction(e -> showRoomManagement());
        appointmentBtn.setOnAction(e -> showAppointmentManagement());
        billingBtn.setOnAction(e -> showBillingManagement());
        reportBtn.setOnAction(e -> showReportManagement());
        exitBtn.setOnAction(e -> primaryStage.close());

        menuBox.getChildren().addAll(titleLabel, patientBtn, doctorBtn, staffBtn, roomBtn,
                appointmentBtn, billingBtn, reportBtn, exitBtn);
        rootLayout.setCenter(menuBox);
    }

    private Button createMenuButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(300);
        button.setPrefHeight(40);
        button.setStyle("-fx-font-size: 16; -fx-background-color: #4682b4; -fx-text-fill: white;");
        return button;
    }

    private void showPatientManagement() {
        VBox patientBox = new VBox(15);
        patientBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Patient Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        Button addBtn = createActionButton("Add New Patient");
        Button viewBtn = createActionButton("View Patients");
        Button updateBtn = createActionButton("Update Patient");
        Button searchBtn = createActionButton("Search Patients");
        Button deleteBtn = createActionButton("Delete Patient");
        Button backBtn = createActionButton("Back to Main Menu");

        addBtn.setOnAction(e -> showAddPatientForm());
        viewBtn.setOnAction(e -> displayPatients());
        updateBtn.setOnAction(e -> showUpdatePatientForm());
        searchBtn.setOnAction(e -> showSearchPatientForm());
        deleteBtn.setOnAction(e -> showDeletePatientForm());
        backBtn.setOnAction(e -> showMainMenu());

        patientBox.getChildren().addAll(titleLabel, addBtn, viewBtn, updateBtn, searchBtn, deleteBtn, backBtn);
        rootLayout.setCenter(patientBox);
    }

    private Button createActionButton(String text) {
        Button button = new Button(text);
        button.setPrefWidth(250);
        button.setPrefHeight(35);
        button.setStyle("-fx-font-size: 14; -fx-background-color: #5f9ea0; -fx-text-fill: white;");
        return button;
    }

    private void showAddPatientForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Add New Patient");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        TextField nameField = new TextField();
        TextField ageField = new TextField();
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female", "Other");
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        TextArea medicalHistoryArea = new TextArea();

        form.add(new Label("Patient ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(new Label("Full Name:"), 0, 2);
        form.add(nameField, 1, 2);
        form.add(new Label("Age:"), 0, 3);
        form.add(ageField, 1, 3);
        form.add(new Label("Gender:"), 0, 4);
        form.add(genderCombo, 1, 4);
        form.add(new Label("Phone:"), 0, 5);
        form.add(phoneField, 1, 5);
        form.add(new Label("Address:"), 0, 6);
        form.add(addressField, 1, 6);
        form.add(new Label("Medical History:"), 0, 7);
        form.add(medicalHistoryArea, 1, 7);

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, submitBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 8);

        submitBtn.setOnAction(e -> {
            try {
                String sql = "INSERT INTO PATIENTS VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                stmt.setString(2, nameField.getText());
                stmt.setInt(3, Integer.parseInt(ageField.getText()));
                stmt.setString(4, genderCombo.getValue());
                stmt.setString(5, phoneField.getText());
                stmt.setString(6, addressField.getText());
                stmt.setString(7, medicalHistoryArea.getText());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Patient added successfully!");
                    showPatientManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add patient: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid age");
            }
        });

        backBtn.setOnAction(e -> showPatientManagement());

        ScrollPane scrollPane = new ScrollPane(form);
        scrollPane.setFitToWidth(true);
        rootLayout.setCenter(scrollPane);
    }

    private void displayPatients() {
        try {
            String sql = "SELECT * FROM PATIENTS ORDER BY name";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            VBox patientBox = new VBox(10);
            patientBox.setPadding(new Insets(20));

            Label titleLabel = new Label("Patient List");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            titleLabel.setTextFill(Color.DARKBLUE);

            TableView<Patient> table = new TableView<>();

            TableColumn<Patient, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Patient, Integer> ageCol = new TableColumn<>("Age");
            ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));

            TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
            genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));

            TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

            TableColumn<Patient, String> addressCol = new TableColumn<>("Address");
            addressCol.setCellValueFactory(new PropertyValueFactory<>("address"));

            table.getColumns().addAll(idCol, nameCol, ageCol, genderCol, phoneCol, addressCol);
            table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            ObservableList<Patient> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Patient(
                        rs.getString("patient_id"),
                        rs.getString("name"),
                        rs.getInt("age"),
                        rs.getString("gender"),
                        rs.getString("phone"),
                        rs.getString("address"),
                        rs.getString("medical_history")
                ));
            }
            table.setItems(data);

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showPatientManagement());

            patientBox.getChildren().addAll(titleLabel, table, backBtn);
            rootLayout.setCenter(patientBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve patients: " + e.getMessage());
        }
    }

    private void showUpdatePatientForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Update Patient");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField nameField = new TextField();
        TextField ageField = new TextField();
        ComboBox<String> genderCombo = new ComboBox<>();
        genderCombo.getItems().addAll("Male", "Female", "Other");
        TextField phoneField = new TextField();
        TextField addressField = new TextField();
        TextArea medicalHistoryArea = new TextArea();

        form.add(new Label("Patient ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Full Name:"), 0, 2);
        form.add(nameField, 1, 2);
        form.add(new Label("Age:"), 0, 3);
        form.add(ageField, 1, 3);
        form.add(new Label("Gender:"), 0, 4);
        form.add(genderCombo, 1, 4);
        form.add(new Label("Phone:"), 0, 5);
        form.add(phoneField, 1, 5);
        form.add(new Label("Address:"), 0, 6);
        form.add(addressField, 1, 6);
        form.add(new Label("Medical History:"), 0, 7);
        form.add(medicalHistoryArea, 1, 7);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, updateBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 8);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT * FROM PATIENTS WHERE patient_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    ageField.setText(String.valueOf(rs.getInt("age")));
                    genderCombo.setValue(rs.getString("gender"));
                    phoneField.setText(rs.getString("phone"));
                    addressField.setText(rs.getString("address"));
                    medicalHistoryArea.setText(rs.getString("medical_history"));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No patient found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search patient: " + ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                String sql = "UPDATE PATIENTS SET name = ?, age = ?, gender = ?, phone = ?, address = ?, medical_history = ? WHERE patient_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, nameField.getText());
                stmt.setInt(2, Integer.parseInt(ageField.getText()));
                stmt.setString(3, genderCombo.getValue());
                stmt.setString(4, phoneField.getText());
                stmt.setString(5, addressField.getText());
                stmt.setString(6, medicalHistoryArea.getText());
                stmt.setString(7, idField.getText());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Patient updated successfully!");
                    showPatientManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update patient: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid age");
            }
        });

        backBtn.setOnAction(e -> showPatientManagement());

        ScrollPane scrollPane = new ScrollPane(form);
        scrollPane.setFitToWidth(true);
        rootLayout.setCenter(scrollPane);
    }

    private void showSearchPatientForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Search Patients");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField searchField = new TextField();
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("By ID", "By Name", "By Phone");
        searchType.setValue("By ID");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        TableView<Patient> resultsTable = new TableView<>();
        TableColumn<Patient, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Patient, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Patient, Integer> ageCol = new TableColumn<>("Age");
        ageCol.setCellValueFactory(new PropertyValueFactory<>("age"));
        TableColumn<Patient, String> genderCol = new TableColumn<>("Gender");
        genderCol.setCellValueFactory(new PropertyValueFactory<>("gender"));
        TableColumn<Patient, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        resultsTable.getColumns().addAll(idCol, nameCol, ageCol, genderCol, phoneCol);
        resultsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        form.add(new Label("Search Criteria:"), 0, 1);
        form.add(searchType, 1, 1);
        form.add(new Label("Search Value:"), 0, 2);
        form.add(searchField, 1, 2);
        form.add(searchBtn, 0, 3);
        form.add(backBtn, 1, 3);
        GridPane.setColumnSpan(resultsTable, 2);
        form.add(resultsTable, 0, 4);

        searchBtn.setOnAction(e -> {
            try {
                String sql = switch (searchType.getValue()) {
                    case "By ID" -> "SELECT * FROM PATIENTS WHERE patient_id LIKE ?";
                    case "By Name" -> "SELECT * FROM PATIENTS WHERE name LIKE ?";
                    case "By Phone" -> "SELECT * FROM PATIENTS WHERE phone LIKE ?";
                    default -> "SELECT * FROM PATIENTS WHERE patient_id LIKE ?";
                };

                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, "%" + searchField.getText() + "%");
                ResultSet rs = stmt.executeQuery();

                ObservableList<Patient> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    data.add(new Patient(
                            rs.getString("patient_id"),
                            rs.getString("name"),
                            rs.getInt("age"),
                            rs.getString("gender"),
                            rs.getString("phone"),
                            rs.getString("address"),
                            rs.getString("medical_history")
                    ));
                }
                resultsTable.setItems(data);

                if (data.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Results", "No patients found matching the criteria.");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search patients: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showPatientManagement());

        form.add(titleLabel, 0, 0);
        rootLayout.setCenter(form);
    }

    private void showDeletePatientForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Delete Patient");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField nameField = new TextField();
        nameField.setEditable(false);
        TextField ageField = new TextField();
        ageField.setEditable(false);
        TextField genderField = new TextField();
        genderField.setEditable(false);

        form.add(new Label("Patient ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Full Name:"), 0, 2);
        form.add(nameField, 1, 2);
        form.add(new Label("Age:"), 0, 3);
        form.add(ageField, 1, 3);
        form.add(new Label("Gender:"), 0, 4);
        form.add(genderField, 1, 4);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #5f9ea0; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, deleteBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 5);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT * FROM PATIENTS WHERE patient_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    ageField.setText(String.valueOf(rs.getInt("age")));
                    genderField.setText(rs.getString("gender"));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No patient found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search patient: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this patient?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String sql = "DELETE FROM PATIENTS WHERE patient_id = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, idField.getText());

                    if (stmt.executeUpdate() > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Patient deleted successfully!");
                        idField.clear();
                        nameField.clear();
                        ageField.clear();
                        genderField.clear();
                    }
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete patient: " + ex.getMessage());
                }
            }
        });

        backBtn.setOnAction(e -> showPatientManagement());

        rootLayout.setCenter(form);
    }

    private void showDoctorManagement() {
        VBox doctorBox = new VBox(15);
        doctorBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Doctor Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        Button addBtn = createActionButton("Add New Doctor");
        Button viewBtn = createActionButton("View Doctors");
        Button updateBtn = createActionButton("Update Doctor");
        Button searchBtn = createActionButton("Search Doctors");
        Button deleteBtn = createActionButton("Delete Doctor");
        Button backBtn = createActionButton("Back to Main Menu");

        addBtn.setOnAction(e -> showAddDoctorForm());
        viewBtn.setOnAction(e -> displayDoctors());
        updateBtn.setOnAction(e -> showUpdateDoctorForm());
        searchBtn.setOnAction(e -> showSearchDoctorForm());
        deleteBtn.setOnAction(e -> showDeleteDoctorForm());
        backBtn.setOnAction(e -> showMainMenu());

        doctorBox.getChildren().addAll(titleLabel, addBtn, viewBtn, updateBtn, searchBtn, deleteBtn, backBtn);
        rootLayout.setCenter(doctorBox);
    }

    private void showAddDoctorForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Add New Doctor");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        TextField staffIdField = new TextField();
        TextField specialtyField = new TextField();
        TextField feeField = new TextField();

        form.add(new Label("Doctor ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(new Label("Staff ID:"), 0, 2);
        form.add(staffIdField, 1, 2);
        form.add(new Label("Specialty:"), 0, 3);
        form.add(specialtyField, 1, 3);
        form.add(new Label("Consultation Fee:"), 0, 4);
        form.add(feeField, 1, 4);

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, submitBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 5);

        submitBtn.setOnAction(e -> {
            try {
                String sql = "INSERT INTO DOCTORS VALUES (?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                stmt.setString(2, staffIdField.getText());
                stmt.setString(3, specialtyField.getText());
                stmt.setDouble(4, Double.parseDouble(feeField.getText()));

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor added successfully!");
                    showDoctorManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add doctor: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid consultation fee");
            }
        });

        backBtn.setOnAction(e -> showDoctorManagement());

        rootLayout.setCenter(form);
    }

    private void displayDoctors() {
        try {
            String sql = "SELECT d.doctor_id, s.name, d.specialty, d.consultation_fee " +
                    "FROM DOCTORS d JOIN STAFF s ON d.staff_id = s.staff_id " +
                    "ORDER BY s.name";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            VBox doctorBox = new VBox(10);
            doctorBox.setPadding(new Insets(20));

            Label titleLabel = new Label("Doctor List");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            titleLabel.setTextFill(Color.DARKBLUE);

            TableView<Doctor> table = new TableView<>();

            TableColumn<Doctor, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Doctor, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Doctor, String> specialtyCol = new TableColumn<>("Specialty");
            specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialty"));

            TableColumn<Doctor, Double> feeCol = new TableColumn<>("Consultation Fee");
            feeCol.setCellValueFactory(new PropertyValueFactory<>("fee"));

            table.getColumns().addAll(idCol, nameCol, specialtyCol, feeCol);
            table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            ObservableList<Doctor> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Doctor(
                        rs.getString("doctor_id"),
                        rs.getString("name"),
                        rs.getString("specialty"),
                        rs.getDouble("consultation_fee")
                ));
            }
            table.setItems(data);

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showDoctorManagement());

            doctorBox.getChildren().addAll(titleLabel, table, backBtn);
            rootLayout.setCenter(doctorBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve doctors: " + e.getMessage());
        }
    }

    private void showUpdateDoctorForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Update Doctor");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField nameField = new TextField();
        nameField.setEditable(false);
        TextField specialtyField = new TextField();
        TextField feeField = new TextField();

        form.add(new Label("Doctor ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Doctor Name:"), 0, 2);
        form.add(nameField, 1, 2);
        form.add(new Label("Specialty:"), 0, 3);
        form.add(specialtyField, 1, 3);
        form.add(new Label("Consultation Fee:"), 0, 4);
        form.add(feeField, 1, 4);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, updateBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 5);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT d.doctor_id, s.name, d.specialty, d.consultation_fee " +
                        "FROM DOCTORS d JOIN STAFF s ON d.staff_id = s.staff_id " +
                        "WHERE d.doctor_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    specialtyField.setText(rs.getString("specialty"));
                    feeField.setText(String.valueOf(rs.getDouble("consultation_fee")));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No doctor found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search doctor: " + ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                String sql = "UPDATE DOCTORS SET specialty = ?, consultation_fee = ? WHERE doctor_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, specialtyField.getText());
                stmt.setDouble(2, Double.parseDouble(feeField.getText()));
                stmt.setString(3, idField.getText());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor updated successfully!");
                    showDoctorManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update doctor: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid consultation fee");
            }
        });

        backBtn.setOnAction(e -> showDoctorManagement());

        rootLayout.setCenter(form);
    }

    private void showSearchDoctorForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Search Doctors");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField searchField = new TextField();
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("By ID", "By Name", "By Specialty");
        searchType.setValue("By ID");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        TableView<Doctor> resultsTable = new TableView<>();
        TableColumn<Doctor, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Doctor, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Doctor, String> specialtyCol = new TableColumn<>("Specialty");
        specialtyCol.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        TableColumn<Doctor, Double> feeCol = new TableColumn<>("Fee");
        feeCol.setCellValueFactory(new PropertyValueFactory<>("fee"));
        resultsTable.getColumns().addAll(idCol, nameCol, specialtyCol, feeCol);
        resultsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        form.add(new Label("Search Criteria:"), 0, 1);
        form.add(searchType, 1, 1);
        form.add(new Label("Search Value:"), 0, 2);
        form.add(searchField, 1, 2);
        form.add(searchBtn, 0, 3);
        form.add(backBtn, 1, 3);
        GridPane.setColumnSpan(resultsTable, 2);
        form.add(resultsTable, 0, 4);

        searchBtn.setOnAction(e -> {
            try {
                String sql = switch (searchType.getValue()) {
                    case "By Name" -> "SELECT d.doctor_id, s.name, d.specialty, d.consultation_fee " +
                            "FROM DOCTORS d JOIN STAFF s ON d.staff_id = s.staff_id " +
                            "WHERE s.name LIKE ?";
                    case "By Specialty" -> "SELECT d.doctor_id, s.name, d.specialty, d.consultation_fee " +
                            "FROM DOCTORS d JOIN STAFF s ON d.staff_id = s.staff_id " +
                            "WHERE d.specialty LIKE ?";
                    default -> "SELECT d.doctor_id, s.name, d.specialty, d.consultation_fee " +
                            "FROM DOCTORS d JOIN STAFF s ON d.staff_id = s.staff_id " +
                            "WHERE d.doctor_id LIKE ?";
                };

                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, "%" + searchField.getText() + "%");
                ResultSet rs = stmt.executeQuery();

                ObservableList<Doctor> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    data.add(new Doctor(
                            rs.getString("doctor_id"),
                            rs.getString("name"),
                            rs.getString("specialty"),
                            rs.getDouble("consultation_fee")
                    ));
                }
                resultsTable.setItems(data);

                if (data.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Results", "No doctors found matching the criteria.");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search doctors: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showDoctorManagement());

        form.add(titleLabel, 0, 0);
        rootLayout.setCenter(form);
    }

    private void showDeleteDoctorForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Delete Doctor");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField nameField = new TextField();
        nameField.setEditable(false);
        TextField specialtyField = new TextField();
        specialtyField.setEditable(false);

        form.add(new Label("Doctor ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Doctor Name:"), 0, 2);
        form.add(nameField, 1, 2);
        form.add(new Label("Specialty:"), 0, 3);
        form.add(specialtyField, 1, 3);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #5f9ea0; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, deleteBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 4);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT d.doctor_id, s.name, d.specialty " +
                        "FROM DOCTORS d JOIN STAFF s ON d.staff_id = s.staff_id " +
                        "WHERE d.doctor_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    specialtyField.setText(rs.getString("specialty"));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No doctor found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search doctor: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this doctor?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String sql = "DELETE FROM DOCTORS WHERE doctor_id = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, idField.getText());

                    if (stmt.executeUpdate() > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Doctor deleted successfully!");
                        idField.clear();
                        nameField.clear();
                        specialtyField.clear();
                    }
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete doctor: " + ex.getMessage());
                }
            }
        });

        backBtn.setOnAction(e -> showDoctorManagement());

        rootLayout.setCenter(form);
    }

    private void showStaffManagement() {
        VBox staffBox = new VBox(15);
        staffBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Staff Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        Button addBtn = createActionButton("Add New Staff");
        Button viewBtn = createActionButton("View Staff");
        Button updateBtn = createActionButton("Update Staff");
        Button searchBtn = createActionButton("Search Staff");
        Button deleteBtn = createActionButton("Delete Staff");
        Button backBtn = createActionButton("Back to Main Menu");

        addBtn.setOnAction(e -> showAddStaffForm());
        viewBtn.setOnAction(e -> displayStaff());
        updateBtn.setOnAction(e -> showUpdateStaffForm());
        searchBtn.setOnAction(e -> showSearchStaffForm());
        deleteBtn.setOnAction(e -> showDeleteStaffForm());
        backBtn.setOnAction(e -> showMainMenu());

        staffBox.getChildren().addAll(titleLabel, addBtn, viewBtn, updateBtn, searchBtn, deleteBtn, backBtn);
        rootLayout.setCenter(staffBox);
    }

    private void showAddStaffForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Add New Staff");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        TextField nameField = new TextField();
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Doctor", "Nurse", "Administrator", "Receptionist", "Technician");
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField departmentField = new TextField();

        form.add(new Label("Staff ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(new Label("Full Name:"), 0, 2);
        form.add(nameField, 1, 2);
        form.add(new Label("Role:"), 0, 3);
        form.add(roleCombo, 1, 3);
        form.add(new Label("Phone:"), 0, 4);
        form.add(phoneField, 1, 4);
        form.add(new Label("Email:"), 0, 5);
        form.add(emailField, 1, 5);
        form.add(new Label("Department:"), 0, 6);
        form.add(departmentField, 1, 6);

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, submitBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 7);

        submitBtn.setOnAction(e -> {
            try {
                String sql = "INSERT INTO STAFF VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                stmt.setString(2, nameField.getText());
                stmt.setString(3, roleCombo.getValue());
                stmt.setString(4, phoneField.getText());
                stmt.setString(5, emailField.getText());
                stmt.setString(6, departmentField.getText());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Staff added successfully!");
                    showStaffManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add staff: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showStaffManagement());

        rootLayout.setCenter(form);
    }

    private void displayStaff() {
        try {
            String sql = "SELECT * FROM STAFF ORDER BY name";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            VBox staffBox = new VBox(10);
            staffBox.setPadding(new Insets(20));

            Label titleLabel = new Label("Staff List");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            titleLabel.setTextFill(Color.DARKBLUE);

            TableView<Staff> table = new TableView<>();

            TableColumn<Staff, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Staff, String> nameCol = new TableColumn<>("Name");
            nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

            TableColumn<Staff, String> roleCol = new TableColumn<>("Role");
            roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));

            TableColumn<Staff, String> phoneCol = new TableColumn<>("Phone");
            phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));

            TableColumn<Staff, String> deptCol = new TableColumn<>("Department");
            deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));

            table.getColumns().addAll(idCol, nameCol, roleCol, phoneCol, deptCol);
            table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            ObservableList<Staff> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Staff(
                        rs.getString("staff_id"),
                        rs.getString("name"),
                        rs.getString("role"),
                        rs.getString("phone"),
                        rs.getString("email"),
                        rs.getString("department")
                ));
            }
            table.setItems(data);

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showStaffManagement());

            staffBox.getChildren().addAll(titleLabel, table, backBtn);
            rootLayout.setCenter(staffBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve staff: " + e.getMessage());
        }
    }

    private void showUpdateStaffForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Update Staff");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField nameField = new TextField();
        ComboBox<String> roleCombo = new ComboBox<>();
        roleCombo.getItems().addAll("Doctor", "Nurse", "Administrator", "Receptionist", "Technician");
        TextField phoneField = new TextField();
        TextField emailField = new TextField();
        TextField departmentField = new TextField();

        form.add(new Label("Staff ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Full Name:"), 0, 2);
        form.add(nameField, 1, 2);
        form.add(new Label("Role:"), 0, 3);
        form.add(roleCombo, 1, 3);
        form.add(new Label("Phone:"), 0, 4);
        form.add(phoneField, 1, 4);
        form.add(new Label("Email:"), 0, 5);
        form.add(emailField, 1, 5);
        form.add(new Label("Department:"), 0, 6);
        form.add(departmentField, 1, 6);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, updateBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 7);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT * FROM STAFF WHERE staff_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    roleCombo.setValue(rs.getString("role"));
                    phoneField.setText(rs.getString("phone"));
                    emailField.setText(rs.getString("email"));
                    departmentField.setText(rs.getString("department"));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No staff found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search staff: " + ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                String sql = "UPDATE STAFF SET name = ?, role = ?, phone = ?, email = ?, department = ? WHERE staff_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, nameField.getText());
                stmt.setString(2, roleCombo.getValue());
                stmt.setString(3, phoneField.getText());
                stmt.setString(4, emailField.getText());
                stmt.setString(5, departmentField.getText());
                stmt.setString(6, idField.getText());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Staff updated successfully!");
                    showStaffManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update staff: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showStaffManagement());

        rootLayout.setCenter(form);
    }

    private void showSearchStaffForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Search Staff");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField searchField = new TextField();
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("By ID", "By Name", "By Role", "By Department");
        searchType.setValue("By ID");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        TableView<Staff> resultsTable = new TableView<>();
        TableColumn<Staff, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Staff, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));
        TableColumn<Staff, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(new PropertyValueFactory<>("role"));
        TableColumn<Staff, String> phoneCol = new TableColumn<>("Phone");
        phoneCol.setCellValueFactory(new PropertyValueFactory<>("phone"));
        TableColumn<Staff, String> deptCol = new TableColumn<>("Department");
        deptCol.setCellValueFactory(new PropertyValueFactory<>("department"));
        resultsTable.getColumns().addAll(idCol, nameCol, roleCol, phoneCol, deptCol);
        resultsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        form.add(new Label("Search Criteria:"), 0, 1);
        form.add(searchType, 1, 1);
        form.add(new Label("Search Value:"), 0, 2);
        form.add(searchField, 1, 2);
        form.add(searchBtn, 0, 3);
        form.add(backBtn, 1, 3);
        GridPane.setColumnSpan(resultsTable, 2);
        form.add(resultsTable, 0, 4);

        searchBtn.setOnAction(e -> {
            try {
                String sql = switch (searchType.getValue()) {
                    case "By ID" -> "SELECT * FROM STAFF WHERE staff_id LIKE ?";
                    case "By Name" -> "SELECT * FROM STAFF WHERE name LIKE ?";
                    case "By Role" -> "SELECT * FROM STAFF WHERE role LIKE ?";
                    case "By Department" -> "SELECT * FROM STAFF WHERE department LIKE ?";
                    default -> "SELECT * FROM STAFF WHERE staff_id LIKE ?";
                };

                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, "%" + searchField.getText() + "%");
                ResultSet rs = stmt.executeQuery();

                ObservableList<Staff> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    data.add(new Staff(
                            rs.getString("staff_id"),
                            rs.getString("name"),
                            rs.getString("role"),
                            rs.getString("phone"),
                            rs.getString("email"),
                            rs.getString("department")
                    ));
                }
                resultsTable.setItems(data);

                if (data.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Results", "No staff found matching the criteria.");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search staff: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showStaffManagement());

        form.add(titleLabel, 0, 0);
        rootLayout.setCenter(form);
    }

    private void showDeleteStaffForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Delete Staff");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField nameField = new TextField();
        nameField.setEditable(false);
        TextField roleField = new TextField();
        roleField.setEditable(false);
        TextField departmentField = new TextField();
        departmentField.setEditable(false);

        form.add(new Label("Staff ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Staff Name:"), 0, 2);
        form.add(nameField, 1, 2);
        form.add(new Label("Role:"), 0, 3);
        form.add(roleField, 1, 3);
        form.add(new Label("Department:"), 0, 4);
        form.add(departmentField, 1, 4);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #5f9ea0; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, deleteBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 5);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT * FROM STAFF WHERE staff_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    nameField.setText(rs.getString("name"));
                    roleField.setText(rs.getString("role"));
                    departmentField.setText(rs.getString("department"));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No staff found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search staff: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this staff member?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String sql = "DELETE FROM STAFF WHERE staff_id = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, idField.getText());

                    if (stmt.executeUpdate() > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Staff deleted successfully!");
                        idField.clear();
                        nameField.clear();
                        roleField.clear();
                        departmentField.clear();
                    }
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete staff: " + ex.getMessage());
                }
            }
        });

        backBtn.setOnAction(e -> showStaffManagement());

        rootLayout.setCenter(form);
    }

    private void showRoomManagement() {
        VBox roomBox = new VBox(15);
        roomBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Room Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        Button addBtn = createActionButton("Add New Room");
        Button viewBtn = createActionButton("View Rooms");
        Button updateBtn = createActionButton("Update Room");
        Button searchBtn = createActionButton("Search Rooms");
        Button deleteBtn = createActionButton("Delete Room");
        Button backBtn = createActionButton("Back to Main Menu");

        addBtn.setOnAction(e -> showAddRoomForm());
        viewBtn.setOnAction(e -> displayRooms());
        updateBtn.setOnAction(e -> showUpdateRoomForm());
        searchBtn.setOnAction(e -> showSearchRoomForm());
        deleteBtn.setOnAction(e -> showDeleteRoomForm());
        backBtn.setOnAction(e -> showMainMenu());

        roomBox.getChildren().addAll(titleLabel, addBtn, viewBtn, updateBtn, searchBtn, deleteBtn, backBtn);
        rootLayout.setCenter(roomBox);
    }

    private void showAddRoomForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Add New Room");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        TextField numberField = new TextField();
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Private", "Shared", "ICU", "Operating", "Emergency");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Available", "Occupied", "Maintenance");
        DatePicker assignDatePicker = new DatePicker();
        DatePicker dischargeDatePicker = new DatePicker();
        TextField staffIdField = new TextField();

        form.add(new Label("Room ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(new Label("Room Number:"), 0, 2);
        form.add(numberField, 1, 2);
        form.add(new Label("Type:"), 0, 3);
        form.add(typeCombo, 1, 3);
        form.add(new Label("Status:"), 0, 4);
        form.add(statusCombo, 1, 4);
        form.add(new Label("Assign Date:"), 0, 5);
        form.add(assignDatePicker, 1, 5);
        form.add(new Label("Discharge Date:"), 0, 6);
        form.add(dischargeDatePicker, 1, 6);
        form.add(new Label("Staff ID:"), 0, 7);
        form.add(staffIdField, 1, 7);

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, submitBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 8);

        submitBtn.setOnAction(e -> {
            try {
                String sql = "INSERT INTO ROOMS VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                stmt.setString(2, numberField.getText());
                stmt.setString(3, typeCombo.getValue());
                stmt.setString(4, statusCombo.getValue());

                LocalDate assignDate = assignDatePicker.getValue();
                if (assignDate != null) {
                    stmt.setDate(5, Date.valueOf(assignDate));
                } else {
                    stmt.setNull(5, Types.DATE);
                }

                LocalDate dischargeDate = dischargeDatePicker.getValue();
                if (dischargeDate != null) {
                    stmt.setDate(6, Date.valueOf(dischargeDate));
                } else {
                    stmt.setNull(6, Types.DATE);
                }

                if (!staffIdField.getText().isEmpty()) {
                    stmt.setString(7, staffIdField.getText());
                } else {
                    stmt.setNull(7, Types.VARCHAR);
                }

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Room added successfully!");
                    showRoomManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to add room: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showRoomManagement());

        rootLayout.setCenter(form);
    }

    private void displayRooms() {
        try {
            String sql = "SELECT r.room_id, r.room_number, r.type, r.status, r.assign_date, r.discharge_date, s.name as staff_name " +
                    "FROM ROOMS r LEFT JOIN STAFF s ON r.staff_id = s.staff_id " +
                    "ORDER BY r.room_number";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            VBox roomBox = new VBox(10);
            roomBox.setPadding(new Insets(20));

            Label titleLabel = new Label("Room List");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            titleLabel.setTextFill(Color.DARKBLUE);

            TableView<Room> table = new TableView<>();

            TableColumn<Room, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Room, String> numberCol = new TableColumn<>("Number");
            numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));

            TableColumn<Room, String> typeCol = new TableColumn<>("Type");
            typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));

            TableColumn<Room, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

            TableColumn<Room, String> assignDateCol = new TableColumn<>("Assign Date");
            assignDateCol.setCellValueFactory(new PropertyValueFactory<>("assignDate"));

            TableColumn<Room, String> staffCol = new TableColumn<>("Staff");
            staffCol.setCellValueFactory(new PropertyValueFactory<>("staffName"));

            table.getColumns().addAll(idCol, numberCol, typeCol, statusCol, assignDateCol, staffCol);
            table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            ObservableList<Room> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Room(
                        rs.getString("room_id"),
                        rs.getString("room_number"),
                        rs.getString("type"),
                        rs.getString("status"),
                        rs.getDate("assign_date") != null ? rs.getDate("assign_date").toString() : "",
                        rs.getString("staff_name")
                ));
            }
            table.setItems(data);

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showRoomManagement());

            roomBox.getChildren().addAll(titleLabel, table, backBtn);
            rootLayout.setCenter(roomBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve rooms: " + e.getMessage());
        }
    }

    private void showUpdateRoomForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Update Room");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField numberField = new TextField();
        ComboBox<String> typeCombo = new ComboBox<>();
        typeCombo.getItems().addAll("Private", "Shared", "ICU", "Operating", "Emergency");
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Available", "Occupied", "Maintenance");
        DatePicker assignDatePicker = new DatePicker();
        DatePicker dischargeDatePicker = new DatePicker();
        TextField staffIdField = new TextField();

        form.add(new Label("Room ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Room Number:"), 0, 2);
        form.add(numberField, 1, 2);
        form.add(new Label("Type:"), 0, 3);
        form.add(typeCombo, 1, 3);
        form.add(new Label("Status:"), 0, 4);
        form.add(statusCombo, 1, 4);
        form.add(new Label("Assign Date:"), 0, 5);
        form.add(assignDatePicker, 1, 5);
        form.add(new Label("Discharge Date:"), 0, 6);
        form.add(dischargeDatePicker, 1, 6);
        form.add(new Label("Staff ID:"), 0, 7);
        form.add(staffIdField, 1, 7);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, updateBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 8);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT * FROM ROOMS WHERE room_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    numberField.setText(rs.getString("room_number"));
                    typeCombo.setValue(rs.getString("type"));
                    statusCombo.setValue(rs.getString("status"));

                    Date assignDate = rs.getDate("assign_date");
                    if (assignDate != null) {
                        assignDatePicker.setValue(assignDate.toLocalDate());
                    } else {
                        assignDatePicker.setValue(null);
                    }

                    Date dischargeDate = rs.getDate("discharge_date");
                    if (dischargeDate != null) {
                        dischargeDatePicker.setValue(dischargeDate.toLocalDate());
                    } else {
                        dischargeDatePicker.setValue(null);
                    }

                    String staffId = rs.getString("staff_id");
                    staffIdField.setText(staffId != null ? staffId : "");
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No room found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search room: " + ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                String sql = "UPDATE ROOMS SET room_number = ?, type = ?, status = ?, assign_date = ?, discharge_date = ?, staff_id = ? WHERE room_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, numberField.getText());
                stmt.setString(2, typeCombo.getValue());
                stmt.setString(3, statusCombo.getValue());

                LocalDate assignDate = assignDatePicker.getValue();
                if (assignDate != null) {
                    stmt.setDate(4, Date.valueOf(assignDate));
                } else {
                    stmt.setNull(4, Types.DATE);
                }

                LocalDate dischargeDate = dischargeDatePicker.getValue();
                if (dischargeDate != null) {
                    stmt.setDate(5, Date.valueOf(dischargeDate));
                } else {
                    stmt.setNull(5, Types.DATE);
                }

                if (!staffIdField.getText().isEmpty()) {
                    stmt.setString(6, staffIdField.getText());
                } else {
                    stmt.setNull(6, Types.VARCHAR);
                }

                stmt.setString(7, idField.getText());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Room updated successfully!");
                    showRoomManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update room: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showRoomManagement());

        rootLayout.setCenter(form);
    }

    private void showSearchRoomForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Search Rooms");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField searchField = new TextField();
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("By ID", "By Number", "By Type", "By Status");
        searchType.setValue("By ID");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        TableView<Room> resultsTable = new TableView<>();
        TableColumn<Room, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Room, String> numberCol = new TableColumn<>("Number");
        numberCol.setCellValueFactory(new PropertyValueFactory<>("number"));
        TableColumn<Room, String> typeCol = new TableColumn<>("Type");
        typeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        TableColumn<Room, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        resultsTable.getColumns().addAll(idCol, numberCol, typeCol, statusCol);
        resultsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        form.add(new Label("Search Criteria:"), 0, 1);
        form.add(searchType, 1, 1);
        form.add(new Label("Search Value:"), 0, 2);
        form.add(searchField, 1, 2);
        form.add(searchBtn, 0, 3);
        form.add(backBtn, 1, 3);
        GridPane.setColumnSpan(resultsTable, 2);
        form.add(resultsTable, 0, 4);

        searchBtn.setOnAction(e -> {
            try {
                String sql = switch (searchType.getValue()) {
                    case "By ID" -> "SELECT r.room_id, r.room_number, r.type, r.status, s.name as staff_name " +
                            "FROM ROOMS r LEFT JOIN STAFF s ON r.staff_id = s.staff_id " +
                            "WHERE r.room_id LIKE ?";
                    case "By Number" -> "SELECT r.room_id, r.room_number, r.type, r.status, s.name as staff_name " +
                            "FROM ROOMS r LEFT JOIN STAFF s ON r.staff_id = s.staff_id " +
                            "WHERE r.room_number LIKE ?";
                    case "By Type" -> "SELECT r.room_id, r.room_number, r.type, r.status, s.name as staff_name " +
                            "FROM ROOMS r LEFT JOIN STAFF s ON r.staff_id = s.staff_id " +
                            "WHERE r.type LIKE ?";
                    case "By Status" -> "SELECT r.room_id, r.room_number, r.type, r.status, s.name as staff_name " +
                            "FROM ROOMS r LEFT JOIN STAFF s ON r.staff_id = s.staff_id " +
                            "WHERE r.status LIKE ?";
                    default -> "SELECT r.room_id, r.room_number, r.type, r.status, s.name as staff_name " +
                            "FROM ROOMS r LEFT JOIN STAFF s ON r.staff_id = s.staff_id " +
                            "WHERE r.room_id LIKE ?";
                };

                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, "%" + searchField.getText() + "%");
                ResultSet rs = stmt.executeQuery();

                ObservableList<Room> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    data.add(new Room(
                            rs.getString("room_id"),
                            rs.getString("room_number"),
                            rs.getString("type"),
                            rs.getString("status"),
                            "",
                            rs.getString("staff_name")
                    ));
                }
                resultsTable.setItems(data);

                if (data.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Results", "No rooms found matching the criteria.");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search rooms: " + ex.getMessage());
            }
        });

        backBtn.setOnAction(e -> showRoomManagement());

        form.add(titleLabel, 0, 0);
        rootLayout.setCenter(form);
    }

    private void showDeleteRoomForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Delete Room");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField numberField = new TextField();
        numberField.setEditable(false);
        TextField typeField = new TextField();
        typeField.setEditable(false);
        TextField statusField = new TextField();
        statusField.setEditable(false);

        form.add(new Label("Room ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Room Number:"), 0, 2);
        form.add(numberField, 1, 2);
        form.add(new Label("Type:"), 0, 3);
        form.add(typeField, 1, 3);
        form.add(new Label("Status:"), 0, 4);
        form.add(statusField, 1, 4);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #5f9ea0; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, deleteBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 5);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT * FROM ROOMS WHERE room_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    numberField.setText(rs.getString("room_number"));
                    typeField.setText(rs.getString("type"));
                    statusField.setText(rs.getString("status"));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No room found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search room: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this room?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String sql = "DELETE FROM ROOMS WHERE room_id = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, idField.getText());

                    if (stmt.executeUpdate() > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Room deleted successfully!");
                        idField.clear();
                        numberField.clear();
                        typeField.clear();
                        statusField.clear();
                    }
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete room: " + ex.getMessage());
                }
            }
        });

        backBtn.setOnAction(e -> showRoomManagement());

        rootLayout.setCenter(form);
    }

    private void showAppointmentManagement() {
        VBox appointmentBox = new VBox(15);
        appointmentBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Appointment Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        Button addBtn = createActionButton("Schedule Appointment");
        Button viewBtn = createActionButton("View Appointments");
        Button updateBtn = createActionButton("Update Appointment");
        Button cancelBtn = createActionButton("Cancel Appointment");
        Button searchBtn = createActionButton("Search Appointments");
        Button backBtn = createActionButton("Back to Main Menu");

        addBtn.setOnAction(e -> showScheduleAppointmentForm());
        viewBtn.setOnAction(e -> displayAppointments());
        updateBtn.setOnAction(e -> showUpdateAppointmentForm());
        cancelBtn.setOnAction(e -> showCancelAppointmentForm());
        searchBtn.setOnAction(e -> showSearchAppointmentForm());
        backBtn.setOnAction(e -> showMainMenu());

        appointmentBox.getChildren().addAll(titleLabel, addBtn, viewBtn, updateBtn, cancelBtn, searchBtn, backBtn);
        rootLayout.setCenter(appointmentBox);
    }

    private void showScheduleAppointmentForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Schedule Appointment");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        TextField patientIdField = new TextField();
        TextField doctorIdField = new TextField();
        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        TextField reasonField = new TextField();
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Scheduled", "Completed", "Cancelled");
        statusCombo.setValue("Scheduled");

        form.add(new Label("Appointment ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(new Label("Patient ID:"), 0, 2);
        form.add(patientIdField, 1, 2);
        form.add(new Label("Doctor ID:"), 0, 3);
        form.add(doctorIdField, 1, 3);
        form.add(new Label("Date:"), 0, 4);
        form.add(datePicker, 1, 4);
        form.add(new Label("Time (HH:MM):"), 0, 5);
        form.add(timeField, 1, 5);
        form.add(new Label("Reason:"), 0, 6);
        form.add(reasonField, 1, 6);
        form.add(new Label("Status:"), 0, 7);
        form.add(statusCombo, 1, 7);

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, submitBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 8);

        submitBtn.setOnAction(e -> {
            try {
                String sql = "INSERT INTO APPOINTMENTS VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                stmt.setString(2, patientIdField.getText());
                stmt.setString(3, doctorIdField.getText());
                stmt.setDate(4, Date.valueOf(datePicker.getValue()));
                stmt.setTime(5, Time.valueOf(timeField.getText() + ":00"));
                stmt.setString(6, reasonField.getText());
                stmt.setString(7, statusCombo.getValue());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment scheduled successfully!");
                    showAppointmentManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to schedule appointment: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter time in HH:MM format");
            }
        });

        backBtn.setOnAction(e -> showAppointmentManagement());

        rootLayout.setCenter(form);
    }

    private void displayAppointments() {
        try {
            String sql = "SELECT a.appointment_id, p.name as patient_name, d.doctor_id, s.name as doctor_name, " +
                    "a.appointment_date, a.appointment_time, a.reason, a.status " +
                    "FROM APPOINTMENTS a " +
                    "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                    "JOIN DOCTORS d ON a.doctor_id = d.doctor_id " +
                    "JOIN STAFF s ON d.staff_id = s.staff_id " +
                    "ORDER BY a.appointment_date, a.appointment_time";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            VBox appointmentBox = new VBox(10);
            appointmentBox.setPadding(new Insets(20));

            Label titleLabel = new Label("Appointment List");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            titleLabel.setTextFill(Color.DARKBLUE);

            TableView<Appointment> table = new TableView<>();

            TableColumn<Appointment, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
            patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

            TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
            doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));

            TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

            TableColumn<Appointment, String> timeCol = new TableColumn<>("Time");
            timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

            TableColumn<Appointment, String> reasonCol = new TableColumn<>("Reason");
            reasonCol.setCellValueFactory(new PropertyValueFactory<>("reason"));

            TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

            table.getColumns().addAll(idCol, patientCol, doctorCol, dateCol, timeCol, reasonCol, statusCol);
            table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            ObservableList<Appointment> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Appointment(
                        rs.getString("appointment_id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("appointment_date").toString(),
                        rs.getTime("appointment_time").toString(),
                        rs.getString("reason"),
                        rs.getString("status")
                ));
            }
            table.setItems(data);

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showAppointmentManagement());

            appointmentBox.getChildren().addAll(titleLabel, table, backBtn);
            rootLayout.setCenter(appointmentBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve appointments: " + e.getMessage());
        }
    }

    private void showUpdateAppointmentForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Update Appointment");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField patientNameField = new TextField();
        patientNameField.setEditable(false);
        TextField doctorNameField = new TextField();
        doctorNameField.setEditable(false);
        DatePicker datePicker = new DatePicker();
        TextField timeField = new TextField();
        TextField reasonField = new TextField();
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Scheduled", "Completed", "Cancelled");

        form.add(new Label("Appointment ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Patient Name:"), 0, 2);
        form.add(patientNameField, 1, 2);
        form.add(new Label("Doctor Name:"), 0, 3);
        form.add(doctorNameField, 1, 3);
        form.add(new Label("Date:"), 0, 4);
        form.add(datePicker, 1, 4);
        form.add(new Label("Time (HH:MM):"), 0, 5);
        form.add(timeField, 1, 5);
        form.add(new Label("Reason:"), 0, 6);
        form.add(reasonField, 1, 6);
        form.add(new Label("Status:"), 0, 7);
        form.add(statusCombo, 1, 7);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, updateBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 8);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT a.appointment_id, p.name as patient_name, s.name as doctor_name, " +
                        "a.appointment_date, a.appointment_time, a.reason, a.status " +
                        "FROM APPOINTMENTS a " +
                        "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                        "JOIN DOCTORS d ON a.doctor_id = d.doctor_id " +
                        "JOIN STAFF s ON d.staff_id = s.staff_id " +
                        "WHERE a.appointment_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    patientNameField.setText(rs.getString("patient_name"));
                    doctorNameField.setText(rs.getString("doctor_name"));
                    datePicker.setValue(rs.getDate("appointment_date").toLocalDate());
                    timeField.setText(rs.getTime("appointment_time").toString().substring(0, 5));
                    reasonField.setText(rs.getString("reason"));
                    statusCombo.setValue(rs.getString("status"));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No appointment found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search appointment: " + ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                String sql = "UPDATE APPOINTMENTS SET appointment_date = ?, appointment_time = ?, reason = ?, status = ? WHERE appointment_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setDate(1, Date.valueOf(datePicker.getValue()));
                stmt.setTime(2, Time.valueOf(timeField.getText() + ":00"));
                stmt.setString(3, reasonField.getText());
                stmt.setString(4, statusCombo.getValue());
                stmt.setString(5, idField.getText());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment updated successfully!");
                    showAppointmentManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update appointment: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter time in HH:MM format");
            }
        });

        backBtn.setOnAction(e -> showAppointmentManagement());

        rootLayout.setCenter(form);
    }

    private void showCancelAppointmentForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Cancel Appointment");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField patientNameField = new TextField();
        patientNameField.setEditable(false);
        TextField doctorNameField = new TextField();
        doctorNameField.setEditable(false);
        TextField dateField = new TextField();
        dateField.setEditable(false);
        TextField timeField = new TextField();
        timeField.setEditable(false);

        form.add(new Label("Appointment ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Patient Name:"), 0, 2);
        form.add(patientNameField, 1, 2);
        form.add(new Label("Doctor Name:"), 0, 3);
        form.add(doctorNameField, 1, 3);
        form.add(new Label("Date:"), 0, 4);
        form.add(dateField, 1, 4);
        form.add(new Label("Time:"), 0, 5);
        form.add(timeField, 1, 5);

        Button cancelBtn = new Button("Cancel Appointment");
        cancelBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #5f9ea0; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, cancelBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 6);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT a.appointment_id, p.name as patient_name, s.name as doctor_name, " +
                        "a.appointment_date, a.appointment_time " +
                        "FROM APPOINTMENTS a " +
                        "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                        "JOIN DOCTORS d ON a.doctor_id = d.doctor_id " +
                        "JOIN STAFF s ON d.staff_id = s.staff_id " +
                        "WHERE a.appointment_id = ? AND a.status = 'Scheduled'";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    patientNameField.setText(rs.getString("patient_name"));
                    doctorNameField.setText(rs.getString("doctor_name"));
                    dateField.setText(rs.getDate("appointment_date").toString());
                    timeField.setText(rs.getTime("appointment_time").toString());
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No scheduled appointment found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search appointment: " + ex.getMessage());
            }
        });

        cancelBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Cancellation");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to cancel this appointment?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String sql = "UPDATE APPOINTMENTS SET status = 'Cancelled' WHERE appointment_id = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, idField.getText());

                    if (stmt.executeUpdate() > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment cancelled successfully!");
                        idField.clear();
                        patientNameField.clear();
                        doctorNameField.clear();
                        dateField.clear();
                        timeField.clear();
                    }
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to cancel appointment: " + ex.getMessage());
                }
            }
        });

        backBtn.setOnAction(e -> showAppointmentManagement());

        rootLayout.setCenter(form);
    }

    private void showSearchAppointmentForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Search Appointments");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField searchField = new TextField();
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("By ID", "By Patient Name", "By Doctor Name", "By Date", "By Status");
        searchType.setValue("By ID");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        TableView<Appointment> resultsTable = new TableView<>();
        TableColumn<Appointment, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Appointment, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        TableColumn<Appointment, String> doctorCol = new TableColumn<>("Doctor");
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        TableColumn<Appointment, String> dateCol = new TableColumn<>("Date");
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        TableColumn<Appointment, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        resultsTable.getColumns().addAll(idCol, patientCol, doctorCol, dateCol, statusCol);
        resultsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        form.add(new Label("Search Criteria:"), 0, 1);
        form.add(searchType, 1, 1);
        form.add(new Label("Search Value:"), 0, 2);
        form.add(searchField, 1, 2);
        form.add(searchBtn, 0, 3);
        form.add(backBtn, 1, 3);
        GridPane.setColumnSpan(resultsTable, 2);
        form.add(resultsTable, 0, 4);

        searchBtn.setOnAction(e -> {
            try {
                String sql = switch (searchType.getValue()) {
                    case "By ID" -> "SELECT a.appointment_id, p.name as patient_name, s.name as doctor_name, " +
                            "a.appointment_date, a.status " +
                            "FROM APPOINTMENTS a " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "JOIN DOCTORS d ON a.doctor_id = d.doctor_id " +
                            "JOIN STAFF s ON d.staff_id = s.staff_id " +
                            "WHERE a.appointment_id LIKE ?";
                    case "By Patient Name" ->
                            "SELECT a.appointment_id, p.name as patient_name, s.name as doctor_name, " +
                                    "a.appointment_date, a.status " +
                                    "FROM APPOINTMENTS a " +
                                    "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                                    "JOIN DOCTORS d ON a.doctor_id = d.doctor_id " +
                                    "JOIN STAFF s ON d.staff_id = s.staff_id " +
                                    "WHERE p.name LIKE ?";
                    case "By Doctor Name" ->
                            "SELECT a.appointment_id, p.name as patient_name, s.name as doctor_name, " +
                                    "a.appointment_date, a.status " +
                                    "FROM APPOINTMENTS a " +
                                    "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                                    "JOIN DOCTORS d ON a.doctor_id = d.doctor_id " +
                                    "JOIN STAFF s ON d.staff_id = s.staff_id " +
                                    "WHERE s.name LIKE ?";
                    case "By Date" -> "SELECT a.appointment_id, p.name as patient_name, s.name as doctor_name, " +
                            "a.appointment_date, a.status " +
                            "FROM APPOINTMENTS a " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "JOIN DOCTORS d ON a.doctor_id = d.doctor_id " +
                            "JOIN STAFF s ON d.staff_id = s.staff_id " +
                            "WHERE a.appointment_date = ?";
                    case "By Status" -> "SELECT a.appointment_id, p.name as patient_name, s.name as doctor_name, " +
                            "a.appointment_date, a.status " +
                            "FROM APPOINTMENTS a " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "JOIN DOCTORS d ON a.doctor_id = d.doctor_id " +
                            "JOIN STAFF s ON d.staff_id = s.staff_id " +
                            "WHERE a.status LIKE ?";
                    default -> "SELECT a.appointment_id, p.name as patient_name, s.name as doctor_name, " +
                            "a.appointment_date, a.status " +
                            "FROM APPOINTMENTS a " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "JOIN DOCTORS d ON a.doctor_id = d.doctor_id " +
                            "JOIN STAFF s ON d.staff_id = s.staff_id " +
                            "WHERE a.appointment_id LIKE ?";
                };

                PreparedStatement stmt = connection.prepareStatement(sql);
                if (searchType.getValue().equals("By Date")) {
                    stmt.setDate(1, Date.valueOf(searchField.getText()));
                } else {
                    stmt.setString(1, "%" + searchField.getText() + "%");
                }
                ResultSet rs = stmt.executeQuery();

                ObservableList<Appointment> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    data.add(new Appointment(
                            rs.getString("appointment_id"),
                            rs.getString("patient_name"),
                            rs.getString("doctor_name"),
                            rs.getDate("appointment_date").toString(),
                            "",
                            "",
                            rs.getString("status")
                    ));
                }
                resultsTable.setItems(data);

                if (data.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Results", "No appointments found matching the criteria.");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search appointments: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter date in YYYY-MM-DD format");
            }
        });

        backBtn.setOnAction(e -> showAppointmentManagement());

        form.add(titleLabel, 0, 0);
        rootLayout.setCenter(form);
    }

    private void showBillingManagement() {
        VBox billingBox = new VBox(15);
        billingBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Billing Management");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        Button addBtn = createActionButton("Create Bill");
        Button viewBtn = createActionButton("View Bills");
        Button updateBtn = createActionButton("Update Bill");
        Button searchBtn = createActionButton("Search Bills");
        Button deleteBtn = createActionButton("Delete Bill");
        Button backBtn = createActionButton("Back to Main Menu");

        addBtn.setOnAction(e -> showCreateBillForm());
        viewBtn.setOnAction(e -> displayBills());
        updateBtn.setOnAction(e -> showUpdateBillForm());
        searchBtn.setOnAction(e -> showSearchBillForm());
        deleteBtn.setOnAction(e -> showDeleteBillForm());
        backBtn.setOnAction(e -> showMainMenu());

        billingBox.getChildren().addAll(titleLabel, addBtn, viewBtn, updateBtn, searchBtn, deleteBtn, backBtn);
        rootLayout.setCenter(billingBox);
    }

    private void showCreateBillForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Create Bill");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        TextField appointmentIdField = new TextField();
        TextField amountField = new TextField();
        DatePicker datePicker = new DatePicker();
        datePicker.setValue(LocalDate.now());
        TextField descriptionField = new TextField();
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Paid", "Unpaid", "Pending");
        statusCombo.setValue("Unpaid");

        form.add(new Label("Bill ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(new Label("Appointment ID:"), 0, 2);
        form.add(appointmentIdField, 1, 2);
        form.add(new Label("Amount:"), 0, 3);
        form.add(amountField, 1, 3);
        form.add(new Label("Date:"), 0, 4);
        form.add(datePicker, 1, 4);
        form.add(new Label("Description:"), 0, 5);
        form.add(descriptionField, 1, 5);
        form.add(new Label("Status:"), 0, 6);
        form.add(statusCombo, 1, 6);

        Button submitBtn = new Button("Submit");
        submitBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, submitBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 7);

        submitBtn.setOnAction(e -> {
            try {
                String sql = "INSERT INTO BILLING VALUES (?, ?, ?, ?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                stmt.setString(2, appointmentIdField.getText());
                stmt.setDouble(3, Double.parseDouble(amountField.getText()));
                stmt.setDate(4, Date.valueOf(datePicker.getValue()));
                stmt.setString(5, descriptionField.getText());
                stmt.setString(6, statusCombo.getValue());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Bill created successfully!");
                    showBillingManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to create bill: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid amount");
            }
        });

        backBtn.setOnAction(e -> showBillingManagement());

        rootLayout.setCenter(form);
    }

    private void displayBills() {
        try {
            String sql = "SELECT b.bill_id, a.appointment_id, p.name as patient_name, " +
                    "b.amount, b.date, b.description, b.status " +
                    "FROM BILLING b " +
                    "JOIN APPOINTMENTS a ON b.appointment_id = a.appointment_id " +
                    "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                    "ORDER BY b.date DESC";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            VBox billBox = new VBox(10);
            billBox.setPadding(new Insets(20));

            Label titleLabel = new Label("Bill List");
            titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
            titleLabel.setTextFill(Color.DARKBLUE);

            TableView<Bill> table = new TableView<>();

            TableColumn<Bill, String> idCol = new TableColumn<>("ID");
            idCol.setCellValueFactory(new PropertyValueFactory<>("id"));

            TableColumn<Bill, String> appointmentCol = new TableColumn<>("Appointment");
            appointmentCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));

            TableColumn<Bill, String> patientCol = new TableColumn<>("Patient");
            patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));

            TableColumn<Bill, Double> amountCol = new TableColumn<>("Amount");
            amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));

            TableColumn<Bill, String> dateCol = new TableColumn<>("Date");
            dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));

            TableColumn<Bill, String> statusCol = new TableColumn<>("Status");
            statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));

            table.getColumns().addAll(idCol, appointmentCol, patientCol, amountCol, dateCol, statusCol);
            table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

            ObservableList<Bill> data = FXCollections.observableArrayList();
            while (rs.next()) {
                data.add(new Bill(
                        rs.getString("bill_id"),
                        rs.getString("appointment_id"),
                        rs.getString("patient_name"),
                        rs.getDouble("amount"),
                        rs.getDate("date").toString(),
                        rs.getString("description"),
                        rs.getString("status")
                ));
            }
            table.setItems(data);

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showBillingManagement());

            billBox.getChildren().addAll(titleLabel, table, backBtn);
            rootLayout.setCenter(billBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to retrieve bills: " + e.getMessage());
        }
    }

    private void showUpdateBillForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Update Bill");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField appointmentIdField = new TextField();
        appointmentIdField.setEditable(false);
        TextField patientNameField = new TextField();
        patientNameField.setEditable(false);
        TextField amountField = new TextField();
        DatePicker datePicker = new DatePicker();
        TextField descriptionField = new TextField();
        ComboBox<String> statusCombo = new ComboBox<>();
        statusCombo.getItems().addAll("Paid", "Unpaid", "Pending");

        form.add(new Label("Bill ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Appointment ID:"), 0, 2);
        form.add(appointmentIdField, 1, 2);
        form.add(new Label("Patient Name:"), 0, 3);
        form.add(patientNameField, 1, 3);
        form.add(new Label("Amount:"), 0, 4);
        form.add(amountField, 1, 4);
        form.add(new Label("Date:"), 0, 5);
        form.add(datePicker, 1, 5);
        form.add(new Label("Description:"), 0, 6);
        form.add(descriptionField, 1, 6);
        form.add(new Label("Status:"), 0, 7);
        form.add(statusCombo, 1, 7);

        Button updateBtn = new Button("Update");
        updateBtn.setStyle("-fx-background-color: #4caf50; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, updateBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 8);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT b.bill_id, b.appointment_id, p.name as patient_name, " +
                        "b.amount, b.date, b.description, b.status " +
                        "FROM BILLING b " +
                        "JOIN APPOINTMENTS a ON b.appointment_id = a.appointment_id " +
                        "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                        "WHERE b.bill_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    appointmentIdField.setText(rs.getString("appointment_id"));
                    patientNameField.setText(rs.getString("patient_name"));
                    amountField.setText(String.valueOf(rs.getDouble("amount")));
                    datePicker.setValue(rs.getDate("date").toLocalDate());
                    descriptionField.setText(rs.getString("description"));
                    statusCombo.setValue(rs.getString("status"));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No bill found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search bill: " + ex.getMessage());
            }
        });

        updateBtn.setOnAction(e -> {
            try {
                String sql = "UPDATE BILLING SET amount = ?, date = ?, description = ?, status = ? WHERE bill_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setDouble(1, Double.parseDouble(amountField.getText()));
                stmt.setDate(2, Date.valueOf(datePicker.getValue()));
                stmt.setString(3, descriptionField.getText());
                stmt.setString(4, statusCombo.getValue());
                stmt.setString(5, idField.getText());

                if (stmt.executeUpdate() > 0) {
                    showAlert(Alert.AlertType.INFORMATION, "Success", "Bill updated successfully!");
                    showBillingManagement();
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to update bill: " + ex.getMessage());
            } catch (NumberFormatException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter a valid amount");
            }
        });

        backBtn.setOnAction(e -> showBillingManagement());

        rootLayout.setCenter(form);
    }

    private void showSearchBillForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Search Bills");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField searchField = new TextField();
        ComboBox<String> searchType = new ComboBox<>();
        searchType.getItems().addAll("By ID", "By Appointment ID", "By Patient Name", "By Date", "By Status");
        searchType.setValue("By ID");

        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

        TableView<Bill> resultsTable = new TableView<>();
        TableColumn<Bill, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        TableColumn<Bill, String> appointmentCol = new TableColumn<>("Appointment");
        appointmentCol.setCellValueFactory(new PropertyValueFactory<>("appointmentId"));
        TableColumn<Bill, String> patientCol = new TableColumn<>("Patient");
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        TableColumn<Bill, Double> amountCol = new TableColumn<>("Amount");
        amountCol.setCellValueFactory(new PropertyValueFactory<>("amount"));
        TableColumn<Bill, String> statusCol = new TableColumn<>("Status");
        statusCol.setCellValueFactory(new PropertyValueFactory<>("status"));
        resultsTable.getColumns().addAll(idCol, appointmentCol, patientCol, amountCol, statusCol);
        resultsTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);

        form.add(new Label("Search Criteria:"), 0, 1);
        form.add(searchType, 1, 1);
        form.add(new Label("Search Value:"), 0, 2);
        form.add(searchField, 1, 2);
        form.add(searchBtn, 0, 3);
        form.add(backBtn, 1, 3);
        GridPane.setColumnSpan(resultsTable, 2);
        form.add(resultsTable, 0, 4);

        searchBtn.setOnAction(e -> {
            try {
                String sql = switch (searchType.getValue()) {
                    case "By ID" -> "SELECT b.bill_id, b.appointment_id, p.name as patient_name, " +
                            "b.amount, b.status " +
                            "FROM BILLING b " +
                            "JOIN APPOINTMENTS a ON b.appointment_id = a.appointment_id " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "WHERE b.bill_id LIKE ?";
                    case "By Appointment ID" -> "SELECT b.bill_id, b.appointment_id, p.name as patient_name, " +
                            "b.amount, b.status " +
                            "FROM BILLING b " +
                            "JOIN APPOINTMENTS a ON b.appointment_id = a.appointment_id " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "WHERE b.appointment_id LIKE ?";
                    case "By Patient Name" -> "SELECT b.bill_id, b.appointment_id, p.name as patient_name, " +
                            "b.amount, b.status " +
                            "FROM BILLING b " +
                            "JOIN APPOINTMENTS a ON b.appointment_id = a.appointment_id " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "WHERE p.name LIKE ?";
                    case "By Date" -> "SELECT b.bill_id, b.appointment_id, p.name as patient_name, " +
                            "b.amount, b.status " +
                            "FROM BILLING b " +
                            "JOIN APPOINTMENTS a ON b.appointment_id = a.appointment_id " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "WHERE b.date = ?";
                    case "By Status" -> "SELECT b.bill_id, b.appointment_id, p.name as patient_name, " +
                            "b.amount, b.status " +
                            "FROM BILLING b " +
                            "JOIN APPOINTMENTS a ON b.appointment_id = a.appointment_id " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "WHERE b.status LIKE ?";
                    default -> "SELECT b.bill_id, b.appointment_id, p.name as patient_name, " +
                            "b.amount, b.status " +
                            "FROM BILLING b " +
                            "JOIN APPOINTMENTS a ON b.appointment_id = a.appointment_id " +
                            "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                            "WHERE b.bill_id LIKE ?";
                };

                PreparedStatement stmt = connection.prepareStatement(sql);
                if (searchType.getValue().equals("By Date")) {
                    stmt.setDate(1, Date.valueOf(searchField.getText()));
                } else {
                    stmt.setString(1, "%" + searchField.getText() + "%");
                }
                ResultSet rs = stmt.executeQuery();

                ObservableList<Bill> data = FXCollections.observableArrayList();
                while (rs.next()) {
                    data.add(new Bill(
                            rs.getString("bill_id"),
                            rs.getString("appointment_id"),
                            rs.getString("patient_name"),
                            rs.getDouble("amount"),
                            "",
                            "",
                            rs.getString("status")
                    ));
                }
                resultsTable.setItems(data);

                if (data.isEmpty()) {
                    showAlert(Alert.AlertType.INFORMATION, "No Results", "No bills found matching the criteria.");
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search bills: " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Please enter date in YYYY-MM-DD format");
            }
        });

        backBtn.setOnAction(e -> showBillingManagement());

        form.add(titleLabel, 0, 0);
        rootLayout.setCenter(form);
    }

    private void showDeleteBillForm() {
        GridPane form = new GridPane();
        form.setVgap(15);
        form.setHgap(10);
        form.setPadding(new Insets(20));
        form.setStyle("-fx-background-color: #e6e6fa; -fx-border-color: #4682b4; -fx-border-width: 2;");

        Label titleLabel = new Label("Delete Bill");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        titleLabel.setTextFill(Color.DARKBLUE);
        GridPane.setColumnSpan(titleLabel, 2);

        TextField idField = new TextField();
        Button searchBtn = new Button("Search");
        searchBtn.setStyle("-fx-background-color: #2196f3; -fx-text-fill: white;");

        TextField appointmentIdField = new TextField();
        appointmentIdField.setEditable(false);
        TextField patientNameField = new TextField();
        patientNameField.setEditable(false);
        TextField amountField = new TextField();
        amountField.setEditable(false);

        form.add(new Label("Bill ID:"), 0, 1);
        form.add(idField, 1, 1);
        form.add(searchBtn, 2, 1);
        form.add(new Label("Appointment ID:"), 0, 2);
        form.add(appointmentIdField, 1, 2);
        form.add(new Label("Patient Name:"), 0, 3);
        form.add(patientNameField, 1, 3);
        form.add(new Label("Amount:"), 0, 4);
        form.add(amountField, 1, 4);

        Button deleteBtn = new Button("Delete");
        deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
        Button backBtn = new Button("Back");
        backBtn.setStyle("-fx-background-color: #5f9ea0; -fx-text-fill: white;");

        HBox buttonBox = new HBox(10, deleteBtn, backBtn);
        buttonBox.setAlignment(Pos.CENTER);
        GridPane.setColumnSpan(buttonBox, 2);

        form.add(titleLabel, 0, 0);
        form.add(buttonBox, 0, 5);

        searchBtn.setOnAction(e -> {
            try {
                String sql = "SELECT b.bill_id, b.appointment_id, p.name as patient_name, b.amount " +
                        "FROM BILLING b " +
                        "JOIN APPOINTMENTS a ON b.appointment_id = a.appointment_id " +
                        "JOIN PATIENTS p ON a.patient_id = p.patient_id " +
                        "WHERE b.bill_id = ?";
                PreparedStatement stmt = connection.prepareStatement(sql);
                stmt.setString(1, idField.getText());
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    appointmentIdField.setText(rs.getString("appointment_id"));
                    patientNameField.setText(rs.getString("patient_name"));
                    amountField.setText(String.valueOf(rs.getDouble("amount")));
                } else {
                    showAlert(Alert.AlertType.WARNING, "Not Found", "No bill found with ID: " + idField.getText());
                }
            } catch (SQLException ex) {
                showAlert(Alert.AlertType.ERROR, "Error", "Failed to search bill: " + ex.getMessage());
            }
        });

        deleteBtn.setOnAction(e -> {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Confirm Deletion");
            confirm.setHeaderText(null);
            confirm.setContentText("Are you sure you want to delete this bill?");

            Optional<ButtonType> result = confirm.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                try {
                    String sql = "DELETE FROM BILLING WHERE bill_id = ?";
                    PreparedStatement stmt = connection.prepareStatement(sql);
                    stmt.setString(1, idField.getText());

                    if (stmt.executeUpdate() > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "Success", "Bill deleted successfully!");
                        idField.clear();
                        appointmentIdField.clear();
                        patientNameField.clear();
                        amountField.clear();
                    }
                } catch (SQLException ex) {
                    showAlert(Alert.AlertType.ERROR, "Error", "Failed to delete bill: " + ex.getMessage());
                }
            }
        });

        backBtn.setOnAction(e -> showBillingManagement());

        rootLayout.setCenter(form);
    }

    private void showReportManagement() {
        VBox reportBox = new VBox(15);
        reportBox.setPadding(new Insets(20));

        Label titleLabel = new Label("Report Generation");
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 24));
        titleLabel.setTextFill(Color.DARKBLUE);

        Button patientBtn = createActionButton("Patient Report");
        Button doctorBtn = createActionButton("Doctor Report");
        Button appointmentBtn = createActionButton("Appointment Report");
        Button billingBtn = createActionButton("Billing Report");
        Button roomBtn = createActionButton("Room Occupancy Report");
        Button backBtn = createActionButton("Back to Main Menu");

        patientBtn.setOnAction(e -> generatePatientReport());
        doctorBtn.setOnAction(e -> generateDoctorReport());
        appointmentBtn.setOnAction(e -> generateAppointmentReport());
        billingBtn.setOnAction(e -> generateBillingReport());
        roomBtn.setOnAction(e -> generateRoomOccupancyReport());
        backBtn.setOnAction(e -> showMainMenu());

        reportBox.getChildren().addAll(titleLabel, patientBtn, doctorBtn, appointmentBtn, billingBtn, roomBtn, backBtn);
        rootLayout.setCenter(reportBox);
    }

    private void generatePatientReport() {
        try {
            String sql = "SELECT gender, COUNT(*) as count FROM PATIENTS GROUP BY gender";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder report = new StringBuilder();
            report.append("Patient Demographic Report\n");
            report.append("==========================\n\n");
            report.append(String.format("%-10s %-10s\n", "Gender", "Count"));
            report.append(String.format("%-10s %-10s\n", "------", "-----"));

            int total = 0;
            while (rs.next()) {
                report.append(String.format("%-10s %-10d\n",
                        rs.getString("gender"), rs.getInt("count")));
                total += rs.getInt("count");
            }
            report.append("\nTotal Patients: ").append(total);

            TextArea reportArea = new TextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14;");

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showReportManagement());

            VBox reportBox = new VBox(10, reportArea, backBtn);
            reportBox.setPadding(new Insets(20));

            rootLayout.setCenter(reportBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate patient report: " + e.getMessage());
        }
    }

    private void generateDoctorReport() {
        try {
            String sql = "SELECT d.specialty, COUNT(*) as count, AVG(d.consultation_fee) as avg_fee " +
                    "FROM DOCTORS d GROUP BY d.specialty";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder report = new StringBuilder();
            report.append("Doctor Specialty Report\n");
            report.append("======================\n\n");
            report.append(String.format("%-20s %-10s %-15s\n", "Specialty", "Count", "Avg Fee"));
            report.append(String.format("%-20s %-10s %-15s\n", "---------", "-----", "-------"));

            while (rs.next()) {
                report.append(String.format("%-20s %-10d $%-15.2f\n",
                        rs.getString("specialty"), rs.getInt("count"), rs.getDouble("avg_fee")));
            }

            TextArea reportArea = new TextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14;");

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showReportManagement());

            VBox reportBox = new VBox(10, reportArea, backBtn);
            reportBox.setPadding(new Insets(20));

            rootLayout.setCenter(reportBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate doctor report: " + e.getMessage());
        }
    }

    private void generateAppointmentReport() {
        try {
            String sql = "SELECT a.status, COUNT(*) as count " +
                    "FROM APPOINTMENTS a GROUP BY a.status";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder report = new StringBuilder();
            report.append("Appointment Status Report\n");
            report.append("========================\n\n");
            report.append(String.format("%-15s %-10s\n", "Status", "Count"));
            report.append(String.format("%-15s %-10s\n", "------", "-----"));

            int total = 0;
            while (rs.next()) {
                report.append(String.format("%-15s %-10d\n",
                        rs.getString("status"), rs.getInt("count")));
                total += rs.getInt("count");
            }
            report.append("\nTotal Appointments: ").append(total);

            TextArea reportArea = new TextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14;");

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showReportManagement());

            VBox reportBox = new VBox(10, reportArea, backBtn);
            reportBox.setPadding(new Insets(20));

            rootLayout.setCenter(reportBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate appointment report: " + e.getMessage());
        }
    }

    private void generateBillingReport() {
        try {
            String sql = "SELECT b.status, COUNT(*) as count, SUM(b.amount) as total " +
                    "FROM BILLING b GROUP BY b.status";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder report = new StringBuilder();
            report.append("Billing Status Report\n");
            report.append("====================\n\n");
            report.append(String.format("%-15s %-10s %-15s\n", "Status", "Count", "Total Amount"));
            report.append(String.format("%-15s %-10s %-15s\n", "------", "-----", "------------"));

            double grandTotal = 0;
            int totalCount = 0;
            while (rs.next()) {
                report.append(String.format("%-15s %-10d $%-15.2f\n",
                        rs.getString("status"), rs.getInt("count"), rs.getDouble("total")));
                grandTotal += rs.getDouble("total");
                totalCount += rs.getInt("count");
            }
            report.append("\nTotal Bills: ").append(totalCount);
            report.append("\nGrand Total: $").append(String.format("%.2f", grandTotal));

            TextArea reportArea = new TextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14;");

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showReportManagement());

            VBox reportBox = new VBox(10, reportArea, backBtn);
            reportBox.setPadding(new Insets(20));

            rootLayout.setCenter(reportBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate billing report: " + e.getMessage());
        }
    }

    private void generateRoomOccupancyReport() {
        try {
            String sql = "SELECT r.type, r.status, COUNT(*) as count " +
                    "FROM ROOMS r GROUP BY r.type, r.status ORDER BY r.type, r.status";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(sql);

            StringBuilder report = new StringBuilder();
            report.append("Room Occupancy Report\n");
            report.append("====================\n\n");
            report.append(String.format("%-15s %-15s %-10s\n", "Type", "Status", "Count"));
            report.append(String.format("%-15s %-15s %-10s\n", "----", "------", "-----"));

            int total = 0;
            while (rs.next()) {
                report.append(String.format("%-15s %-15s %-10d\n",
                        rs.getString("type"), rs.getString("status"), rs.getInt("count")));
                total += rs.getInt("count");
            }
            report.append("\nTotal Rooms: ").append(total);

            TextArea reportArea = new TextArea(report.toString());
            reportArea.setEditable(false);
            reportArea.setStyle("-fx-font-family: monospace; -fx-font-size: 14;");

            Button backBtn = new Button("Back");
            backBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");
            backBtn.setOnAction(e -> showReportManagement());

            VBox reportBox = new VBox(10, reportArea, backBtn);
            reportBox.setPadding(new Insets(20));

            rootLayout.setCenter(reportBox);
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Error", "Failed to generate room occupancy report: " + e.getMessage());
        }
    }

    private void createTables(Connection conn) throws SQLException {
        String[] createTables = {
                "CREATE TABLE IF NOT EXISTS PATIENTS (patient_id VARCHAR(10) PRIMARY KEY, name VARCHAR(50) NOT NULL, age INT NOT NULL, gender VARCHAR(10) NOT NULL, phone VARCHAR(15) NOT NULL, address VARCHAR(100) NOT NULL, medical_history TEXT)",
                "CREATE TABLE IF NOT EXISTS STAFF (staff_id VARCHAR(10) PRIMARY KEY, name VARCHAR(50) NOT NULL, role VARCHAR(30) NOT NULL, phone VARCHAR(15) NOT NULL, email VARCHAR(50), department VARCHAR(30) NOT NULL)",
                "CREATE TABLE IF NOT EXISTS DOCTORS (doctor_id VARCHAR(10) PRIMARY KEY, staff_id VARCHAR(10) NOT NULL, specialty VARCHAR(30) NOT NULL, consultation_fee DECIMAL(10,2) NOT NULL, FOREIGN KEY (staff_id) REFERENCES STAFF(staff_id))",
                "CREATE TABLE IF NOT EXISTS ROOMS (room_id VARCHAR(10) PRIMARY KEY, room_number VARCHAR(10) NOT NULL, type VARCHAR(15) NOT NULL, status VARCHAR(15) NOT NULL, assign_date DATE, discharge_date DATE, staff_id VARCHAR(10), FOREIGN KEY (staff_id) REFERENCES STAFF(staff_id))",
                "CREATE TABLE IF NOT EXISTS APPOINTMENTS (appointment_id VARCHAR(15) PRIMARY KEY, patient_id VARCHAR(10) NOT NULL, doctor_id VARCHAR(10) NOT NULL, appointment_date DATE NOT NULL, appointment_time TIME NOT NULL, reason VARCHAR(100) NOT NULL, status VARCHAR(15) NOT NULL, FOREIGN KEY (patient_id) REFERENCES PATIENTS(patient_id), FOREIGN KEY (doctor_id) REFERENCES DOCTORS(doctor_id))",
                "CREATE TABLE IF NOT EXISTS BILLING (bill_id VARCHAR(15) PRIMARY KEY, appointment_id VARCHAR(15) NOT NULL, amount DECIMAL(10,2) NOT NULL, date DATE NOT NULL, description VARCHAR(100) NOT NULL, status VARCHAR(15) NOT NULL, FOREIGN KEY (appointment_id) REFERENCES APPOINTMENTS(appointment_id))"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : createTables) {
                stmt.execute(sql);
            }
        }
    }


    private void insertInitialData(Connection conn) throws SQLException {
        String[] insertStatements = {
                "INSERT IGNORE INTO PATIENTS VALUES ('p6', 'John Smith', 35, 'Male', '555-1234', '123 Main St', 'Diabetes')",
                "INSERT IGNORE INTO PATIENTS VALUES ('p7', 'Mary Johnson', 28, 'Female', '555-5678', '456 Oak Ave', 'Asthma')",
                "INSERT IGNORE INTO PATIENTS VALUES ('p8', 'Robert Brown', 45, 'Male', '555-9012', '789 Pine Rd', 'Hypertension')",
                "INSERT IGNORE INTO STAFF VALUES ('s12', 'Dr. Sarah Lee', 'Doctor', '555-1111', 'slee@hospital.com', 'Cardiology')",
                "INSERT IGNORE INTO STAFF VALUES ('s13', 'Dr. Michael Chen', 'Doctor', '555-2222', 'mchen@hospital.com', 'Neurology')",
                "INSERT IGNORE INTO STAFF VALUES ('s14', 'Nurse Emily Wilson', 'Nurse', '555-3333', 'ewilson@hospital.com', 'General')",
                "INSERT IGNORE INTO DOCTORS VALUES ('dr6', 'p6', 'Cardiology', 150.00)",
                "INSERT IGNORE INTO DOCTORS VALUES ('dr7', 'p7', 'Neurology', 175.00)",
                "INSERT IGNORE INTO ROOMS VALUES ('r6', '106', 'Private', 'Available', NULL, NULL, NULL)",
                "INSERT IGNORE INTO ROOMS VALUES ('r7', '107', 'Shared', 'Available', NULL, NULL, NULL)",
                "INSERT IGNORE INTO ROOMS VALUES ('r8', '108', 'Private', 'Occupied', '2023-01-15', NULL, 'S003')",
                "INSERT IGNORE INTO APPOINTMENTS VALUES ('a6', 'p6', 'dr6', '2023-02-01', '09:00:00', 'Heart checkup', 'Scheduled')",
                "INSERT IGNORE INTO APPOINTMENTS VALUES ('a7', 'p7', 'dr7', '2023-02-01', '10:30:00', 'Headache', 'Completed')",
                "INSERT IGNORE INTO BILLING VALUES ('b1', 'a7', 175.00, '2023-02-01', 'Neurology consultation', 'Paid')"
        };

        try (Statement stmt = conn.createStatement()) {
            for (String sql : insertStatements) {
                stmt.execute(sql);
            }
        }
    }


    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static class Patient {
        private final String id;
        private final String name;
        private final int age;
        private final String gender;
        private final String phone;
        private final String address;
        private final String medicalHistory;

        public Patient(String id, String name, int age, String gender, String phone, String address, String medicalHistory) {
            this.id = id;
            this.name = name;
            this.age = age;
            this.gender = gender;
            this.phone = phone;
            this.address = address;
            this.medicalHistory = medicalHistory;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public int getAge() { return age; }
        public String getGender() { return gender; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
        public String getMedicalHistory() { return medicalHistory; }
    }

    public static class Doctor {
        private final String id;
        private final String name;
        private final String specialty;
        private final double fee;

        public Doctor(String id, String name, String specialty, double fee) {
            this.id = id;
            this.name = name;
            this.specialty = specialty;
            this.fee = fee;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getSpecialty() { return specialty; }
        public double getFee() { return fee; }
    }

    public static class Staff {
        private final String id;
        private final String name;
        private final String role;
        private final String phone;
        private final String email;
        private final String department;

        public Staff(String id, String name, String role, String phone, String email, String department) {
            this.id = id;
            this.name = name;
            this.role = role;
            this.phone = phone;
            this.email = email;
            this.department = department;
        }

        public String getId() { return id; }
        public String getName() { return name; }
        public String getRole() { return role; }
        public String getPhone() { return phone; }
        public String getEmail() { return email; }
        public String getDepartment() { return department; }
    }

    public static class Room {
        private final String id;
        private final String number;
        private final String type;
        private final String status;
        private final String assignDate;
        private final String staffName;

        public Room(String id, String number, String type, String status, String assignDate, String staffName) {
            this.id = id;
            this.number = number;
            this.type = type;
            this.status = status;
            this.assignDate = assignDate;
            this.staffName = staffName;
        }

        public String getId() { return id; }
        public String getNumber() { return number; }
        public String getType() { return type; }
        public String getStatus() { return status; }
        public String getAssignDate() { return assignDate; }
        public String getStaffName() { return staffName; }
    }

    public static class Appointment {
        private final String id;
        private final String patientName;
        private final String doctorName;
        private final String date;
        private final String time;
        private final String reason;
        private final String status;

        public Appointment(String id, String patientName, String doctorName, String date, String time, String reason, String status) {
            this.id = id;
            this.patientName = patientName;
            this.doctorName = doctorName;
            this.date = date;
            this.time = time;
            this.reason = reason;
            this.status = status;
        }

        public String getId() { return id; }
        public String getPatientName() { return patientName; }
        public String getDoctorName() { return doctorName; }
        public String getDate() { return date; }
        public String getTime() { return time; }
        public String getReason() { return reason; }
        public String getStatus() { return status; }
    }

    public static class Bill {
        private final String id;
        private final String appointmentId;
        private final String patientName;
        private final double amount;
        private final String date;
        private final String description;
        private final String status;

        public Bill(String id, String appointmentId, String patientName, double amount, String date, String description, String status) {
            this.id = id;
            this.appointmentId = appointmentId;
            this.patientName = patientName;
            this.amount = amount;
            this.date = date;
            this.description = description;
            this.status = status;
        }

        public String getId() { return id; }
        public String getAppointmentId() { return appointmentId; }
        public String getPatientName() { return patientName; }
        public double getAmount() { return amount; }
        public String getDate() { return date; }
        public String getDescription() { return description; }
        public String getStatus() { return status; }
    }
}