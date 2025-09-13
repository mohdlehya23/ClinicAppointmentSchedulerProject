/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package oneLastTime;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Scanner;
import java.util.stream.Collectors;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author pc
 */
public class PatientsController implements Initializable {

    @FXML
    private Label pageLabel;
    @FXML
    private Label newLabel;
    @FXML
    private GridPane grid;
    @FXML
    private TextField nameField;
    @FXML
    private TextField phoneField;
    @FXML
    private TextField emailField;
    @FXML
    private Label nameLabel;
    @FXML
    private Label phoneLabel;
    @FXML
    private Label emailLabel;
    @FXML
    private VBox VBox;
    @FXML
    private Button addBtn;
    @FXML
    private Button editBtn;
    @FXML
    private TableView<Patient> tableview;
    @FXML
    private TableColumn<Patient, Integer> idColumn;
    @FXML
    private TableColumn<Patient, String> nameColumn;
    @FXML
    private TableColumn<Patient, String> phoneColumn;
    @FXML
    private TableColumn<Patient, String> emailColumn;
    /**
     * Initializes the controller class.
     */
    
    private ObservableList<Patient> patientList = FXCollections.observableArrayList();
    @FXML
    private HBox hboxbottom;
    @FXML
    private Label labelsearch;
    @FXML
    private TextField textfieldsearch;
    @FXML
    private Button btnsearch;
    @FXML
    private ComboBox<String> sortcombo;
    @FXML
    private VBox Vbottom;
    @FXML
    private HBox Hsearch;
    @FXML
    private HBox Hcombo;
    @FXML
    private Label labelsort;
    
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        phoneColumn.setCellValueFactory(new PropertyValueFactory<>("phone"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        
        //Loading patient into Table
        patientList.clear();
        Scanner scanner ;
        try {
            File file = new File("patient.txt");
            scanner = new Scanner(file);
        
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    Patient p = new Patient(Integer.parseInt(parts[0]),parts[1], parts[2], parts[3]);
                   
                    patientList.add(p);
                    
                }
            }scanner.close();
            tableview.setItems(patientList);
            
      
      }catch(IOException e){
            System.out.println("cannot found file");
      }
       sortcombo.getItems().addAll("Name (A-Z)", "Name (Z-A)");
    }

    @FXML
    private void handleAddbtn(ActionEvent event) {
        
        String name = nameField.getText();
        String phone = phoneField.getText();
        String email = emailField.getText();
        
        
        if (name.isEmpty() || phone.isEmpty() || email.isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("All field are required");
        alert.showAndWait();
        return;
        }
       
        if (!phone.matches("\\d+")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Phone must contain only digits");
        alert.showAndWait();
           return;
        }
        if (!email.contains("@")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Invalid Email format");
            alert.showAndWait();
            return;
        }
        // تحقق من المكرر
        for (Patient p : patientList) {
            if (p.getName().equalsIgnoreCase(name) && p.getPhone().equals(phone)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setContentText("Duplicated patient");
                alert.showAndWait();
                return;
            }
        }
        
        int newId = patientList.size() + 1;
        Patient newPatient = new Patient(newId, name, phone, email);
        patientList.add(newPatient);
        tableview.setItems(patientList);

        savePatientsToFile();
        clearFields();
    }

    @FXML
    private void handleEditPatient(ActionEvent event) {
        
        Patient selected = tableview.getSelectionModel().getSelectedItem();
        if (selected == null) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText("Select a patient to edit");
        alert.showAndWait();            
        }

        selected.setName(nameField.getText());
        selected.setPhone(phoneField.getText());
        selected.setEmail(emailField.getText());

        tableview.refresh();
        savePatientsToFile();
        clearFields();
    }
    
        private void savePatientsToFile() {
        try (PrintWriter writer = new PrintWriter(new FileWriter("patient.txt"))) {
            for (Patient p : patientList) {
                writer.println(p.getId() + "," + p.getName() + "," + p.getPhone() + "," + p.getEmail());
                
            }
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Failed to save patients");
            alert.showAndWait();
        }
    }
        
        private void clearFields() {
        nameField.clear();
        phoneField.clear();
        emailField.clear();
    }

    @FXML
    private void handleGoDashboard(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Dashboard.fxml")); // Adjust path
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void handlesearch(ActionEvent event) {
           String keyword = textfieldsearch.getText().trim();

    if (keyword.isEmpty()) {
        new Alert(Alert.AlertType.WARNING, "Search field cannot be empty").showAndWait();
       
    }

    List<Patient> filtered = patientList.stream()
            .filter(p -> p.getName().toLowerCase().contains(keyword.toLowerCase()))
            .collect(Collectors.toList());

    if (filtered.isEmpty()) {
        new Alert(Alert.AlertType.INFORMATION, "No matching patients found").showAndWait();
    } else {
        tableview.setItems(FXCollections.observableArrayList(filtered));
    }
    }

    @FXML
    private void handlesort(ActionEvent event) {
            String choice = sortcombo.getValue();

    if (!(choice == null)){ 

    List<Patient> sorted = new ArrayList<>(patientList);

    if (choice.equals("Name (A-Z)")) {
        sorted = patientList.stream()
                .sorted(Comparator.comparing(Patient::getName, String.CASE_INSENSITIVE_ORDER))
                .collect(Collectors.toList());
    } else if (choice.equals("Name (Z-A)")) {
        sorted = patientList.stream()
                .sorted(Comparator.comparing(Patient::getName, String.CASE_INSENSITIVE_ORDER).reversed())
                .collect(Collectors.toList());
    }

    tableview.setItems(FXCollections.observableArrayList(sorted));
    }
    }    
}
