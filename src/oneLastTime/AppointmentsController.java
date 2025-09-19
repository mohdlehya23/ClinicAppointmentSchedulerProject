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
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
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

public class AppointmentsController implements Initializable {

    // --- FXML Components ---
    @FXML private ComboBox<Patient> patientComboBox;
    @FXML private ComboBox<Doctor> doctorComboBox;
    @FXML private DatePicker datePicker;
    @FXML private TextField timeField;
    @FXML private TableView<Appointment> appointmentsTable;
    @FXML private TableColumn<Appointment, Integer> idCol;
    @FXML private TableColumn<Appointment, String> patientCol;
    @FXML private TableColumn<Appointment, String> doctorCol;
    @FXML private TableColumn<Appointment, LocalDate> dateCol;
    @FXML private TableColumn<Appointment, String> timeCol;
    @FXML private TextField searchField;
    @FXML private DatePicker searchDatePicker;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Button scheduleButton;
    @FXML private Button updateButton;
    @FXML private Button deleteButton;

    private final ObservableList<Appointment> appointmentList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupTableColumns();
        loadInitialData(); // Load patients and doctors into ComboBoxes
        refreshAppointmentsTable(""); 

        // Add listener to the table to populate fields when a row is selected
        appointmentsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            });

        // Setup Sort ComboBox
        sortComboBox.getItems().addAll("Date (Newest First)", "Time (Earliest First)");
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                handleSort(newVal);
            }
        });
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));
        patientCol.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        doctorCol.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        appointmentsTable.setItems(appointmentList);
    }
//لما نختار صف من الجدول بيملي الحقول بمعلومات الموعد المحدد واسم الدكتور والمريض اللي انحجز الهم الموعد
    private void populateFields(Appointment appointment) {
        patientComboBox.getSelectionModel().select(
            patientComboBox.getItems().stream()
                .filter(p -> p.getName().equals(appointment.getPatientName()))
                .findFirst().orElse(null)
        );
        doctorComboBox.getSelectionModel().select(
            doctorComboBox.getItems().stream()
                .filter(d -> d.getName().equals(appointment.getDoctorName()))
                .findFirst().orElse(null)
        );
        datePicker.setValue(appointment.getDate());
        timeField.setText(appointment.getTime());
    }
// لتحميل الداتا تبعت الدكاترة او المرضى في الكومبو بوكس
    private void loadInitialData() {
        try {
            patientComboBox.setItems(FXCollections.observableArrayList(getAllPatientsFromDB()));
            doctorComboBox.setItems(FXCollections.observableArrayList(getAllDoctorsFromDB()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to load initial data: " + e.getMessage());
        }
    }
//تعيد تحميل المواعيد من قاعدة البيانات مع خيار ترتيب وتعرضهم بالجدول.
    private void refreshAppointmentsTable(String orderByClause) {
        try {
            appointmentList.setAll(getAllAppointmentsFromDB(orderByClause));
            appointmentsTable.refresh();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Could not refresh appointments: " + e.getMessage());
            e.printStackTrace();
        }
    }

  

    @FXML
    void handleScheduleButton(ActionEvent event) {
        if (!isInputValid()) return;
        String sql = "INSERT INTO Appointments (patient_id, doctor_id, date, time) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientComboBox.getValue().getId());
            pstmt.setInt(2, doctorComboBox.getValue().getId());
            pstmt.setDate(3, java.sql.Date.valueOf(datePicker.getValue()));
            pstmt.setString(4, timeField.getText().trim());
            // للتحقق اذا ما تم اضافة صف جديد للجدول في قاعدة البيانات 
            if (pstmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment scheduled successfully.");
                refreshAppointmentsTable("");
                clearForm();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to schedule appointment: " + e.getMessage());
        }
    }

    @FXML
    void handleUpdateButton(ActionEvent event) {
        Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an appointment to update.");
            return;
        }
        if (!isInputValid()) return;
        String sql = "UPDATE Appointments SET patient_id = ?, doctor_id = ?, date = ?, time = ? WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, patientComboBox.getValue().getId());
            pstmt.setInt(2, doctorComboBox.getValue().getId());
            pstmt.setDate(3, java.sql.Date.valueOf(datePicker.getValue()));
            pstmt.setString(4, timeField.getText().trim());
            pstmt.setInt(5, selected.getId());
            //(اي عند عمل تعديل) للتحقق اذا تأثر صف او اكثر ينفذ هذه الدالة
            if (pstmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment updated successfully.");
                refreshAppointmentsTable("");
                clearForm();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to update appointment: " + e.getMessage());
        }
    }

    @FXML
    void handleDeleteButton(ActionEvent event) {
        Appointment selected = appointmentsTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No Selection", "Please select an appointment to delete.");
            return;
        }
        String sql = "DELETE FROM Appointments WHERE id = ?";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, selected.getId());
            if (pstmt.executeUpdate() > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Appointment canceled successfully.");
                refreshAppointmentsTable("");
                clearForm();
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to cancel appointment: " + e.getMessage());
        }
    }

    // --- Search, Sort, and Clear Handlers ---

    @FXML
    void handleClearSearchButton(ActionEvent event) {
        searchField.clear();
        searchDatePicker.setValue(null);
        sortComboBox.getSelectionModel().clearSelection();
        refreshAppointmentsTable("");
    }

    @FXML
    void handleSearchButton(ActionEvent event) {
        String searchText = searchField.getText().trim();
        LocalDate searchDate = searchDatePicker.getValue();
        if (searchText.isEmpty() && searchDate == null) {
            showAlert(Alert.AlertType.ERROR, "Invalid Search", "Please enter a name or select a date to search.");
            return;
        }
        List<Appointment> searchResult = new ArrayList<>();
        String baseQuery = "SELECT a.id, p.name as patient_name, d.name as doctor_name, a.date, a.time "
                + "FROM Appointments a "
                + "JOIN Patients p ON a.patient_id = p.id "
                + "JOIN Doctors d ON a.doctor_id = d.id ";
        StringBuilder whereClause = new StringBuilder();
        List<Object> params = new ArrayList<>();
        if (!searchText.isEmpty()) {
            whereClause.append("(LOWER(p.name) LIKE ? OR LOWER(d.name) LIKE ?)");
            params.add("%" + searchText.toLowerCase() + "%");
            params.add("%" + searchText.toLowerCase() + "%");
        }
        if (searchDate != null) {
            if (whereClause.length() > 0) {
                whereClause.append(" AND ");
            }
            whereClause.append("a.date = ?");
            params.add(java.sql.Date.valueOf(searchDate));
        }
        String finalQuery = baseQuery + "WHERE " + whereClause.toString();
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(finalQuery)) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                searchResult.add(new Appointment(
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("time")
                ));
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Error during search: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        appointmentList.setAll(searchResult);
        if (searchResult.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "No Results", "No appointments found matching your criteria.");
        }
    }

    private void handleSort(String criteria) {
        String orderByClause = "";
        if ("Date (Newest First)".equals(criteria)) {
            orderByClause = "ORDER BY date DESC, time ASC";
        } else if ("Time (Earliest First)".equals(criteria)) {
            orderByClause = "ORDER BY time ASC";
        }
        refreshAppointmentsTable(orderByClause);
    }

    // --- Database Retrieval Methods ---

    public List<Appointment> getAllAppointmentsFromDB(String orderByClause) throws SQLException {
        List<Appointment> appointments = new ArrayList<>();
        // join لربط جدول المواعيد بجدول المرضى او الدكاترة
        // orderByClause للترتيب  علشان هيلزمنا قدام
        String sql = "SELECT a.id, p.name as patient_name, d.name as doctor_name, a.date, a.time "
                + "FROM Appointments a "
                + "JOIN Patients p ON a.patient_id = p.id "
                + "JOIN Doctors d ON a.doctor_id = d.id "
                + orderByClause;
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                // هان جبنا البينات من قاعدة البيانات وخزناهم في اري ليست علشان نعرضهم لاحقا
                appointments.add(new Appointment(
                        rs.getInt("id"),
                        rs.getString("patient_name"),
                        rs.getString("doctor_name"),
                        rs.getDate("date").toLocalDate(),
                        rs.getString("time")
                ));
            }
        }
        return appointments;
    }
// هان عملنا اري ليست علشان نجيب البيانات المخزنة في قاعدة البيانات ونحطهم في الاري ليست
    public List<Patient> getAllPatientsFromDB() throws SQLException {
        List<Patient> patients = new ArrayList<>();
        String sql = "SELECT id, name FROM Patients";
        // هان بنعمل اتصال في قاعدة البيانات وبعدين بنفذ الكويري تبعت اس كيو ال  
        //بعدين بنضيف البيانات اللي جبناها من قاعدة البيانات وبنحطها في الاري ليست
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                patients.add(new Patient(rs.getInt("id"), rs.getString("name")));
            }
        }
        return patients;
    }
//نفس اللوجيك فوق
    public List<Doctor> getAllDoctorsFromDB() throws SQLException {
        List<Doctor> doctors = new ArrayList<>();
        String sql = "SELECT id, name, specialty FROM Doctors";
        try (Connection conn = DatabaseManager.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql); ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                doctors.add(new Doctor(rs.getInt("id"), rs.getString("name"), rs.getString("specialty")));
            }
        }
        return doctors;
    }

   

    @FXML
    void handleGoToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void clearForm() {
        patientComboBox.getSelectionModel().clearSelection();
        doctorComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        timeField.clear();
        appointmentsTable.getSelectionModel().clearSelection();
    }

    private boolean isInputValid() {
        if (patientComboBox.getValue() == null || doctorComboBox.getValue() == null
                || datePicker.getValue() == null || timeField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Data", "Please fill in all fields.");
            return false;
        }
        LocalDate selectedDate = datePicker.getValue();
        if (selectedDate.isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "Invalid Date", "You cannot schedule an appointment in the past.");
            return false;
        }
        if (!timeField.getText().trim().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Time Format", "Please enter the time in HH:mm format (e.g., 14:30).");
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
}