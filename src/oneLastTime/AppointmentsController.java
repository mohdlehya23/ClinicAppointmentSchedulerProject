/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oneLastTime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.beans.property.SimpleStringProperty;
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

    // fxml variables
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
    
   // new fxml componant
    @FXML private TextField searchField;
    @FXML private DatePicker searchDatePicker;
    @FXML private ComboBox<String> sortComboBox;
    
    // to view the data in the table
    private final ObservableList<Appointment> displayedAppointmentList = FXCollections.observableArrayList();
    
    // قائمة List أصلية لتخزين جميع المواعيد في الذاكرة (متطلب المرحلة الثانية)
    // to store all appointments in the memory in the list
    private List<Appointment> masterAppointmentList = new ArrayList<>();

    // Maps لعرض الأسماء بدلاً من المعرفات في الجدول
    private Map<Integer, String> patientIdToNameMap = new HashMap<>();
    private Map<Integer, String> doctorIdToNameMap = new HashMap<>();

    // مسارات الملفات
    private static final String APPOINTMENTS_FILE = "appointments.txt";
    private static final String PATIENTS_FILE = "patients.txt";
    private static final String DOCTORS_FILE = "doctors.txt";

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        setupTableColumns();
        loadInitialData(); // تحميل جميع البيانات في الذاكرة
        
        // ربط الجدول بالقائمة القابلة للعرض
        appointmentsTable.setItems(displayedAppointmentList);
        
        // combo box in sort operation
        sortComboBox.setItems(FXCollections.observableArrayList("Date", "Time"));
        
        // إضافة مستمع (listener) للفرز باستخدام Lambda expression (متطلب المرحلة الثانية)
        sortComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                sortDisplayedData(newVal);
            }
        });
    }

    
    //search by doctor or patient name or according the appointment day
    @FXML
    void handleSearchButton(ActionEvent event) {
        String searchText = searchField.getText().trim().toLowerCase();
        LocalDate searchDate = searchDatePicker.getValue();
        
     
        if (searchText.isEmpty() && searchDate == null) {
            showAlert(Alert.AlertType.ERROR, "خطأ في البحث", "الرجاء إدخال اسم للبحث أو تحديد تاريخ.");
            return;
        }

        //  لفلترة القائمة الرئيسية 
        List<Appointment> filteredList = masterAppointmentList.stream()
            .filter(app -> {
                // فلترة بالاسم
                boolean matchesName = true;
                if (!searchText.isEmpty()) {
                    String patientName = patientIdToNameMap.getOrDefault(app.getPatientId(), "").toLowerCase();
                    String doctorName = doctorIdToNameMap.getOrDefault(app.getDoctorId(), "").toLowerCase();
                    matchesName = patientName.contains(searchText) || doctorName.contains(searchText);
                }

                // فلترة بالتاريخ 
                boolean matchesDate = true;
                if (searchDate != null) {
                    matchesDate = app.getDate().equals(searchDate);
                }
                
                return matchesName && matchesDate;
            })
            .collect(Collectors.toList()); // تجميع النتائج في قائمة جديدة

        // عرض النتائج في الجدول
        displayedAppointmentList.setAll(filteredList);
        
        // عرض تنبيه في حالة عدم وجود نتائج 
        if (filteredList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "لا توجد نتائج", "لم يتم العثور على مواعيد تطابق معايير البحث.");
        }
    }

   // clear the any word written in the text field and return the selection to default mode (Without any selection)
    @FXML
    void handleClearSearchButton(ActionEvent event) {
        searchField.clear();
        searchDatePicker.setValue(null);
        sortComboBox.getSelectionModel().clearSelection();
        displayedAppointmentList.setAll(masterAppointmentList); // إعادة عرض القائمة الكاملة
    }

    // sort the data in the table by date or time
    private void sortDisplayedData(String criteria) {
        Comparator<Appointment> comparator;
        
        if (criteria.equals("Date")) {
            //   على التاريخ
            comparator = Comparator.comparing(Appointment::getDate);
        } else { // "Time"
            //   على الوقت
            comparator = Comparator.comparing(Appointment::getTime);
        }
        

        List<Appointment> sortedList = displayedAppointmentList.stream()
                                           .sorted(comparator)
                                           .collect(Collectors.toList());
        
        displayedAppointmentList.setAll(sortedList);
    }
    
   
    @FXML
    void handleScheduleButton(ActionEvent event) {
        if (!isAppointmentInputValid()) return;

        Patient selectedPatient = patientComboBox.getValue();
        Doctor selectedDoctor = doctorComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();
        String timeText = timeField.getText().trim();
        
        int newId = getNextAppointmentId();
        Appointment newAppointment = new Appointment(newId, selectedPatient.getId(), selectedDoctor.getId(), selectedDate, timeText);
        
        // الإضافة إلى كلتا القائمتين
        masterAppointmentList.add(newAppointment);
        displayedAppointmentList.setAll(masterAppointmentList); // تحديث العرض
        
        saveAppointmentsToFile(masterAppointmentList); // الحفظ من القائمة الرئيسية
        
        clearForm();
        showAlert(Alert.AlertType.INFORMATION, "نجاح", "تم جدولة الموعد بنجاح.");
    }

    @FXML
    void handleGoToDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    private void loadInitialData() {
        // تحميل المرضى والأطباء لملء الخرائط والقوائم المنسدلة
        List<Patient> patients = loadPatientsFromFile();
        patients.forEach(p -> patientIdToNameMap.put(p.getId(), p.getName()));
        patientComboBox.setItems(FXCollections.observableArrayList(patients));

        List<Doctor> doctors = loadDoctorsFromFile();
        doctors.forEach(d -> doctorIdToNameMap.put(d.getId(), d.getName()));
        doctorComboBox.setItems(FXCollections.observableArrayList(doctors));

        // تحميل جميع المواعيد في القائمة الرئيسية
        masterAppointmentList = loadAppointmentsFromFile();
        // عرض جميع المواعيد عند بدء التشغيل
        displayedAppointmentList.setAll(masterAppointmentList);
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
                        Integer.parseInt(parts[0]), Integer.parseInt(parts[1]),
                        Integer.parseInt(parts[2]), LocalDate.parse(parts[3], formatter), parts[4]
                    ));
                }
            }
        } catch (IOException | NumberFormatException e) { /* File might not exist */ }
        return appointments;
    }

    private void saveAppointmentsToFile(List<Appointment> appointments) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (PrintWriter writer = new PrintWriter(new FileWriter(APPOINTMENTS_FILE, false))) {
            for (Appointment app : appointments) {
                writer.println(app.getId() + "," + app.getPatientId() + "," + app.getDoctorId() + "," + app.getDate().format(formatter) + "," + app.getTime());
            }
        } catch (IOException e) {
            System.err.println("Error saving appointments: " + e.getMessage());
        }
    }

    private List<Patient> loadPatientsFromFile() {
        List<Patient> patients = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(PATIENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 2) {
                    patients.add(new Patient(Integer.parseInt(parts[0]), parts[1]));
                }
            }
        } catch (IOException | NumberFormatException e) { /* File might not exist */ }
        return patients;
    }

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
        } catch (IOException | NumberFormatException e) { /* File might not exist */ }
        return doctors;
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void setupTableColumns() {
        idCol.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateCol.setCellValueFactory(new PropertyValueFactory<>("date"));
        timeCol.setCellValueFactory(new PropertyValueFactory<>("time"));

        //  Lambda expressions لعرض أسماء المرضى والأطباء
        patientCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(patientIdToNameMap.getOrDefault(cellData.getValue().getPatientId(), "مريض محذوف"))
        );
        doctorCol.setCellValueFactory(cellData -> 
            new SimpleStringProperty(doctorIdToNameMap.getOrDefault(cellData.getValue().getDoctorId(), "طبيب محذوف"))
        );
    }

    private boolean isAppointmentInputValid() {
        if (patientComboBox.getValue() == null || doctorComboBox.getValue() == null || datePicker.getValue() == null || timeField.getText().trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "بيانات ناقصة", "الرجاء تحديد المريض والطبيب والتاريخ والوقت.");
            return false;
        }
        if (datePicker.getValue().isBefore(LocalDate.now())) {
            showAlert(Alert.AlertType.ERROR, "تاريخ غير صالح", "لا يمكن حجز موعد في الماضي.");
            return false;
        }
        if (!timeField.getText().trim().matches("^([01]?[0-9]|2[0-3]):[0-5][0-9]$")) {
            showAlert(Alert.AlertType.ERROR, "صيغة وقت غير صالحة", "الرجاء إدخال الوقت بصيغة HH:mm (مثال: 14:30).");
            return false;
        }
        return true;
    }

    private void clearForm() {
        patientComboBox.getSelectionModel().clearSelection();
        doctorComboBox.getSelectionModel().clearSelection();
        datePicker.setValue(null);
        timeField.clear();
    }

    private int getNextAppointmentId() {
        // استخدام stream للحصول على أكبر ID (طريقة محسنة)
        return masterAppointmentList.stream()
                   .mapToInt(Appointment::getId)
                   .max()
                   .orElse(0) + 1;
    }
}