/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

package oneLastTime;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

public class PatientsController implements Initializable {

    // --- FXML Components ---
    @FXML private TextField nameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private TextField textfieldsearch;
    @FXML private ComboBox<String> sortcombo;
    @FXML private TableView<Patient> tableview;
    @FXML private TableColumn<Patient, Integer> idColumn;
    @FXML private TableColumn<Patient, String> nameColumn;
    @FXML private TableColumn<Patient, String> phoneColumn;
    @FXML private TableColumn<Patient, String> emailColumn;

    private final ObservableList<Patient> patientList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // إعداد أعمدة الجدول
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        tableview.setItems(patientList);

        // تحميل البيانات من قاعدة البيانات عند بدء التشغيل
        refreshTable();
        
        // إعداد خيارات الفرز
        sortcombo.getItems().addAll("Name (A-Z)", "ID (Ascending)");

        // مستمع لملء الحقول عند تحديد صف
        tableview.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            }
        );
    }
    
    /**
     * تحديث الجدول بالبيانات من قاعدة البيانات.
     */
    private void refreshTable() {
        try {
            patientList.setAll(getAllPatientsFromDB("")); // جلب كل المرضى
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load patient data: " + e.getMessage());
        }
    }

    // --- CRUD Handlers ---

@FXML
private void handleAddButton(ActionEvent event) {
    if (!isInputValid()) {
        return;
    }
    
    // The call now includes the name for the duplicate check.
    if (isDuplicateInDB(nameField.getText().trim(), phoneField.getText().trim(), emailField.getText().trim(), -1)) {
        return;
    }
    
    // ... rest of the method is the same ...
    String sql = "INSERT INTO Patients (name, phone, email) VALUES (?, ?, ?)";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, nameField.getText().trim());
        pstmt.setString(2, phoneField.getText().trim());
        pstmt.setString(3, emailField.getText().trim());
        pstmt.executeUpdate();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Patient added successfully.");
        refreshTable();
        clearFields();
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to add patient: " + e.getMessage());
    }
}

@FXML
private void handleEditButton(ActionEvent event) {
    Patient selected = tableview.getSelectionModel().getSelectedItem();
    if (selected == null) {
        showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a patient to edit.");
        return;
    }
    if (!isInputValid()) {
        return;
    }
    
    // The call now includes the name for the duplicate check.
    if (isDuplicateInDB(nameField.getText().trim(), phoneField.getText().trim(), emailField.getText().trim(), selected.getId())) {
        return;
    }
    
    // ... rest of the method is the same ...
    String sql = "UPDATE Patients SET name = ?, phone = ?, email = ? WHERE id = ?";
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        pstmt.setString(1, nameField.getText().trim());
        pstmt.setString(2, phoneField.getText().trim());
        pstmt.setString(3, emailField.getText().trim());
        pstmt.setInt(4, selected.getId());
        pstmt.executeUpdate();
        showAlert(Alert.AlertType.INFORMATION, "Success", "Patient data updated successfully.");
        refreshTable();
        clearFields();
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update patient: " + e.getMessage());
    }
}

    @FXML
    private void handleDeleteButton(ActionEvent event) {
        Patient selected = tableview.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select a patient to delete.");
            return;
        }
        
        Optional<ButtonType> result = showConfirmation(
            "Confirm Deletion", 
            "Are you sure you want to delete this patient? All associated appointments will also be deleted."
        );
        
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DatabaseManager.getConnection()) {
                // Step 1: Delete associated appointments
                String deleteAppointmentsSQL = "DELETE FROM Appointments WHERE patient_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteAppointmentsSQL)) {
                    pstmt.setInt(1, selected.getId());
                    pstmt.executeUpdate();
                }

                // Step 2: Delete the patient
                String deletePatientSQL = "DELETE FROM Patients WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deletePatientSQL)) {
                    pstmt.setInt(1, selected.getId());
                    pstmt.executeUpdate();
                }
                
                showAlert(Alert.AlertType.INFORMATION, "Success", "Patient and all related appointments deleted successfully.");
                refreshTable();
                clearFields();

            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to delete patient: " + e.getMessage());
            }
        }
    }

    // --- Search and Sort Handlers ---
    
    @FXML
    private void handlesearch(ActionEvent event) {
        String keyword = textfieldsearch.getText().trim();
        try {
            List<Patient> searchResults = getAllPatientsFromDB("WHERE name LIKE ?");
            
            // Re-populate the list with search results
            patientList.setAll(searchResults);
            
            if (searchResults.isEmpty()) {
                showAlert(Alert.AlertType.INFORMATION, "No Results", "No patients found with that name.");
            }
        } catch (SQLException e) {
             showAlert(Alert.AlertType.ERROR, "Database Error", "Search failed: " + e.getMessage());
        }
    }

    @FXML
    private void handlesort(ActionEvent event) {
        String choice = sortcombo.getValue();
        String orderByClause = "";
        if ("Name (A-Z)".equals(choice)) {
            orderByClause = "ORDER BY name ASC";
        } else if ("ID (Ascending)".equals(choice)) {
            orderByClause = "ORDER BY id ASC";
        }
        
        try {
            patientList.setAll(getAllPatientsFromDB(orderByClause));
        } catch (SQLException e) {
             showAlert(Alert.AlertType.ERROR, "Database Error", "Sort failed: " + e.getMessage());
        }
    }

    // --- Database Methods ---
    
    private List<Patient> getAllPatientsFromDB(String condition) throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT id, name, phone, email FROM Patients " + condition;
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (condition.contains("LIKE")) {
                pstmt.setString(1, "%" + textfieldsearch.getText().trim() + "%");
            }
            
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                patients.add(new Patient(
                    rs.getInt("id"), 
                    rs.getString("name"), 
                    rs.getString("phone"), 
                    rs.getString("email"))
                );
            }
        }
        return patients;
    }

    // --- Helper & UI Methods ---
    
    @FXML private void handleClearButton(ActionEvent event) { clearFields(); }
    @FXML private void handleGoDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    /**
 * يتحقق من وجود رقم هاتف أو بريد إلكتروني مكرر في قاعدة البيانات.
 * @param phone رقم الهاتف للتحقق منه.
 * @param email البريد الإلكتروني للتحقق منه.
 * @param currentId معرف المريض الحالي (يتم تجاهله عند التعديل).
 * @return true إذا تم العثور على تكرار، وإلا false.
 */
/**
 * Checks for a duplicate name, phone number, or email in the database.
 * @param name The name to check.
 * @param phone The phone number to check.
 * @param email The email to check.
 * @param currentId The ID of the current patient (ignored when editing).
 * @return true if a duplicate is found, otherwise false.
 */
private boolean isDuplicateInDB(String name, String phone, String email, int currentId) {
    // This SQL query now checks for a matching name OR phone OR email.
    String sql = "SELECT id FROM Patients WHERE (name = ? OR phone = ? OR email = ?) AND id != ?";
    
    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql)) {
        
        pstmt.setString(1, name);
        pstmt.setString(2, phone);
        pstmt.setString(3, email);
        pstmt.setInt(4, currentId);
        
        try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                showAlert(Alert.AlertType.ERROR, "Duplicate Data", "This name, phone number, or email is already registered.");
                return true;
            }
        }
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to check for duplicate data.");
        return true;
    }
    
    return false;
}

    
    private void populateFields(Patient patient) {
        nameField.setText(patient.getName());
        phoneField.setText(patient.getPhone());
        emailField.setText(patient.getEmail());
    }
    
    private void clearFields() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
        tableview.getSelectionModel().clearSelection();
    }

    private boolean isInputValid() {
        if (nameField.getText().isEmpty() || phoneField.getText().isEmpty() || emailField.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Error", "All fields are required.");
            return false;
        }
        if (!emailField.getText().matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}$")) {
            showAlert(Alert.AlertType.ERROR, "Error", "Invalid email format.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private Optional<ButtonType> showConfirmation(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        return alert.showAndWait();
    }
}