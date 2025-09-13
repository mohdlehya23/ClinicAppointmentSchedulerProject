/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oneLastTime;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
/**
 *
 * @author NITRO5
 */





public class DoctorsController implements Initializable {

    // FXML Components
    @FXML private TextField nameField;
    @FXML private TextField specialtyField;
    @FXML private TableView<Doctor> doctorsTable;
    @FXML private TableColumn<Doctor, Integer> idColumn;
    @FXML private TableColumn<Doctor, String> nameColumn;
    @FXML private TableColumn<Doctor, String> specialtyColumn;

    // Data List for the TableView
    private final ObservableList<Doctor> doctorList = FXCollections.observableArrayList();

    // File paths are now defined directly here
    private static final String DOCTORS_FILE = "doctors.txt";
    private static final String APPOINTMENTS_FILE = "appointments.txt";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        specialtyColumn.setCellValueFactory(new PropertyValueFactory<>("specialty"));

        // Load data directly when the controller starts
        doctorList.setAll(loadDoctorsFromFile());
        doctorsTable.setItems(doctorList);

        // Listener to populate fields when a row is selected
        doctorsTable.getSelectionModel().selectedItemProperty().addListener(
            (obs, oldSelection, newSelection) -> {
                if (newSelection != null) {
                    populateFields(newSelection);
                }
            }
        );
    }

    @FXML
    void handleAddButton(ActionEvent event) {
        String name = nameField.getText().trim();
        String specialty = specialtyField.getText().trim();

        if (!isInputValid(name, specialty)) {
            return;
        }
        if (isDuplicate(name, specialty, -1)) {
            showAlert(Alert.AlertType.ERROR, "بيانات مكررة", "يوجد طبيب بنفس الاسم والتخصص بالفعل.");
            return;
        }

        int newId = getNextId();
        Doctor newDoctor = new Doctor(newId, name, specialty);
        doctorList.add(newDoctor);
        saveDoctorsToFile(doctorList);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "نجاح", "تمت إضافة الطبيب بنجاح.");
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

        if (!isInputValid(name, specialty)) {
            return;
        }
        if (isDuplicate(name, specialty, selectedDoctor.getId())) {
             showAlert(Alert.AlertType.ERROR, "بيانات مكررة", "يوجد طبيب آخر بنفس الاسم والتخصص.");
            return;
        }

        selectedDoctor.setName(name);
        selectedDoctor.setSpecialty(specialty);
        doctorsTable.refresh();
        saveDoctorsToFile(doctorList);
        clearFields();
        showAlert(Alert.AlertType.INFORMATION, "نجاح", "تم تعديل بيانات الطبيب بنجاح.");
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
            // Step 1: Delete associated appointments
            deleteAppointmentsByDoctorId(selectedDoctor.getId());

            // Step 2: Delete the doctor
            doctorList.remove(selectedDoctor);
            saveDoctorsToFile(doctorList);
            
            clearFields();
            showAlert(Alert.AlertType.INFORMATION, "نجاح", "تم حذف الطبيب وجميع مواعيده بنجاح.");
        }
    }

    @FXML
    void handleClearButton(ActionEvent event) {
        clearFields();
    }
    
    // This function is just an example, you should use your friend's navigation method
    @FXML
    void handleGoToDashboard(ActionEvent event) throws IOException {
        // This is how your friend switches scenes, so you should use the same method
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml")); // Adjust path if needed
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // --- File Handling Methods (moved from DAO) ---

    private List<Doctor> loadDoctorsFromFile() {
        List<Doctor> doctors = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(DOCTORS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 3) {
                    doctors.add(new Doctor(Integer.parseInt(parts[0]), parts[1], parts[2]));
                }
            }
        } catch (IOException | NumberFormatException e) {
            // File might not exist on first run, which is okay.
        }
        return doctors;
    }

    private void saveDoctorsToFile(List<Doctor> doctors) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(DOCTORS_FILE, false))) {
            for (Doctor doctor : doctors) {
                writer.println(doctor.getId() + "," + doctor.getName() + "," + doctor.getSpecialty());
            }
        } catch (IOException e) {
            System.err.println("Error saving doctors to file: " + e.getMessage());
        }
    }

    private void deleteAppointmentsByDoctorId(int doctorId) {
        List<Appointment> allAppointments = loadAppointmentsFromFile();
        List<Appointment> remainingAppointments = new ArrayList<>();

        for (Appointment app : allAppointments) {
            if (app.getDoctorId() != doctorId) {
                remainingAppointments.add(app);
            }
        }
        saveAppointmentsToFile(remainingAppointments);
    }
    
    private List<Appointment> loadAppointmentsFromFile() {
        List<Appointment> appointments = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedReader reader = new BufferedReader(new FileReader(APPOINTMENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    appointments.add(new Appointment(
                        Integer.parseInt(parts[0]),
                        Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]),
                        LocalDate.parse(parts[3], formatter),
                        parts[4]
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) {
            // File might not exist, which is okay.
        }
        return appointments;
    }

    private void saveAppointmentsToFile(List<Appointment> appointments) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPOINTMENTS_FILE, false))) {
            for (Appointment app : appointments) {
                writer.println(app.getId() + "," + app.getPatientId() + "," + app.getDoctorId() + "," + app.getDate().format(formatter) + "," + app.getTime());
            }
        } catch (IOException e) {
            System.err.println("Error saving appointments to file: " + e.getMessage());
        }
    }

    // --- Alert Methods (moved from AlertHelper) ---

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

    // --- Helper Methods (Validation, UI, etc.) ---

    private boolean isInputValid(String name, String specialty) {
        if (name.isEmpty() || specialty.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "خطأ في الإدخال", "الرجاء ملء جميع الحقول.");
            return false;
        }
        if (!name.matches("[a-zA-Z\\u0600-\\u06FF\\s]+") || !specialty.matches("[a-zA-Z\\u0600-\\u06FF\\s]+")) {
            showAlert(Alert.AlertType.ERROR, "خطأ في الإدخال", "حقول الاسم والتخصص يجب أن تحتوي على حروف فقط.");
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

    private int getNextId() {
        int maxId = 0;
        for(Doctor doc : doctorList) {
            if(doc.getId() > maxId) {
                maxId = doc.getId();
            }
        }
        return maxId + 1;
    }
    
    private boolean isDuplicate(String name, String specialty, int currentId) {
        for (Doctor doc : doctorList) {
            if (doc.getId() != currentId && doc.getName().equalsIgnoreCase(name) && doc.getSpecialty().equalsIgnoreCase(specialty)) {
                return true;
            }
        }
        return false;
    }
}
