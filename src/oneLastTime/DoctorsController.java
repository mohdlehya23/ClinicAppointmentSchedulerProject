/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
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

public class DoctorsController implements Initializable {

    // --- FXML Components ---
    @FXML private TextField nameField;
    @FXML private TextField specialtyField;
    @FXML private TableView<Doctor> doctorsTable;
    @FXML private TableColumn<Doctor, Integer> idColumn;
    @FXML private TableColumn<Doctor, String> nameColumn;
    @FXML private TableColumn<Doctor, String> specialtyColumn;

    private final ObservableList<Doctor> doctorList = FXCollections.observableArrayList();
    @FXML private ComboBox<String> sortComboBox;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // إعداد أعمدة الجدول
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));
        doctorsTable.setItems(doctorList);

        // تحميل البيانات الأولية من قاعدة البيانات
        refreshTable("");

        // مستمع لملء الحقول عند تحديد صف في الجدول
        doctorsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            }
        );
        // In initialize() method of DoctorsController.java
// Assuming your ComboBox is named sortComboBox
sortComboBox.getItems().addAll("Name (A-Z)", "Specialty (A-Z)");
sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
    if (newVal != null) {
        String orderByClause = "";
        if (newVal.equals("Name (A-Z)")) {
            orderByClause = "ORDER BY name ASC";
        } else if (newVal.equals("Specialty (A-Z)")) {
            orderByClause = "ORDER BY specialty ASC";
        }
        // Assuming you have a method like this to refresh the table
        refreshTable(orderByClause);
    }
});
    }

    /**
     * تحديث الجدول بالبيانات من قاعدة البيانات.
     */
    /**
 * Updates the table with data from the database, sorted according to the provided clause.
 * @param orderByClause The SQL ORDER BY clause (e.g., "ORDER BY name ASC").
 */
private void refreshTable(String orderByClause) {
    try {
        doctorList.setAll(getAllDoctorsFromDB(orderByClause));
        doctorsTable.refresh();
    } catch (SQLException e) {
        showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load doctor data: " + e.getMessage());
    }
}
    // --- CRUD Handlers ---

    @FXML
    void handleAddButton(ActionEvent event) {
        String name = nameField.getText().trim();
        String specialty = specialtyField.getText().trim();

        if (!isInputValid(name, specialty) || isDuplicateInDB(name, -1)) {
            return;
        }

        String sql = "INSERT INTO Doctors (name, specialty) VALUES (?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setString(2, specialty);

            if (pstmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "نجاح", "تمت إضافة الطبيب بنجاح.");
                 refreshTable("");
                clearFields();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطأ في قاعدة البيانات", "فشل إضافة الطبيب: " + e.getMessage());
        }
    }

    @FXML
    void handleUpdateButton(ActionEvent event) {
        Doctor selectedDoctor = doctorsTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor == null) {
            showAlert(Alert.AlertType.WARNING, "لم يتم التحديد", "الرجاء تحديد طبيب لتعديله.");
            return;
        }

        String name = nameField.getText().trim();
        String specialty = specialtyField.getText().trim();

        if (!isInputValid(name, specialty) || isDuplicateInDB(name, selectedDoctor.getId())) {
            return;
        }

        String sql = "UPDATE Doctors SET name = ?, specialty = ? WHERE id = ?";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setString(1, name);
            pstmt.setString(2, specialty);
            pstmt.setInt(3, selectedDoctor.getId());

            if (pstmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "نجاح", "تم تعديل بيانات الطبيب بنجاح.");
                 refreshTable("");
                clearFields();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطأ في قاعدة البيانات", "فشل تعديل بيانات الطبيب: " + e.getMessage());
        }
    }

    @FXML
    void handleDeleteButton(ActionEvent event) {
        Doctor selectedDoctor = doctorsTable.getSelectionModel().getSelectedItem();
        if (selectedDoctor == null) {
            showAlert(Alert.AlertType.WARNING, "لم يتم التحديد", "الرجاء تحديد طبيب لحذفه.");
            return;
        }

        Optional<ButtonType> result = showConfirmation(
            "تأكيد الحذف",
            "هل أنت متأكد من حذف الطبيب: " + selectedDoctor.getName() + "؟\nسيتم حذف جميع المواعيد المرتبطة به أيضاً."
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try (Connection conn = DatabaseManager.getConnection()) {
                // الخطوة 1: حذف المواعيد المرتبطة (مهم جداً بسبب Foreign Key)
                String deleteAppointmentsSQL = "DELETE FROM Appointments WHERE doctor_id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteAppointmentsSQL)) {
                    pstmt.setInt(1, selectedDoctor.getId());
                    pstmt.executeUpdate();
                }

                // الخطوة 2: حذف الطبيب نفسه
                String deleteDoctorSQL = "DELETE FROM Doctors WHERE id = ?";
                try (PreparedStatement pstmt = conn.prepareStatement(deleteDoctorSQL)) {
                    pstmt.setInt(1, selectedDoctor.getId());
                    if (pstmt.executeUpdate() > 0) {
                        showAlert(Alert.AlertType.INFORMATION, "نجاح", "تم حذف الطبيب وجميع مواعيده بنجاح.");
                        refreshTable("");
                        clearFields();
                    }
                }
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "خطأ في قاعدة البيانات", "فشل حذف الطبيب: " + e.getMessage());
            }
        }
    }

    // --- Database Methods ---

   private List<Doctor> getAllDoctorsFromDB(String orderByClause) throws SQLException {
    List<Doctor> doctors = new ArrayList<>();
    String sql = "SELECT id, name, specialty FROM Doctors " + orderByClause;

    try (Connection conn = DatabaseManager.getConnection();
         PreparedStatement pstmt = conn.prepareStatement(sql);
         ResultSet rs = pstmt.executeQuery()) {
        
        while (rs.next()) {
            doctors.add(new Doctor(rs.getInt("id"), rs.getString("name"), rs.getString("specialty")));
        }
    }
    return doctors;
}
    private boolean isDuplicateInDB(String name, int currentId) {
        String sql = "SELECT id FROM Doctors WHERE name = ? AND id != ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, name);
            pstmt.setInt(2, currentId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) { // إذا وجد سجل
                    showAlert(Alert.AlertType.ERROR, "بيانات مكررة", "يوجد طبيب بنفس الاسم بالفعل.");
                    return true;
                }
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "خطأ في قاعدة البيانات", "فشل التحقق من البيانات المكررة.");
            return true; // نمنع العملية في حالة حدوث خطأ
        }
        return false;
    }

    // --- Helper & UI Methods ---
    
    @FXML void handleClearButton(ActionEvent event) { clearFields(); }
    @FXML void handleGoToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    private boolean isInputValid(String name, String specialty) {
        if (name.isEmpty() || specialty.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "خطأ في الإدخال", "الرجاء ملء جميع الحقول.");
            return false;
        }
        return true;
    }
    
    private void populateFields(Doctor doctor) {
        nameField.setText(doctor.getName());
        specialtyField.setText(doctor.getSpecialty());
    }

    private void clearFields() {
        nameField.clear();
        specialtyField.clear();
        doctorsTable.getSelectionModel().clearSelection();
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