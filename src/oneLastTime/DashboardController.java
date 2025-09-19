
package oneLastTime;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javafx.animation.FadeTransition;
import javafx.animation.PauseTransition;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Duration;

public class DashboardController implements Initializable {

    // --- FXML Variables ---
    @FXML private BorderPane mainBoarderPane;
    @FXML private Label firstname;
    @FXML private ImageView doctors;
    @FXML private ImageView patients;
    @FXML private ImageView appointment;
    @FXML private ImageView analytics;
    @FXML private ImageView logout;
    @FXML private Button buttonNewAppointment;
    @FXML private TableView<Appointment> upcomingTable; // Changed to use the main Appointment model
    @FXML private TableColumn<Appointment, String> colPatient;
    @FXML private TableColumn<Appointment, String> colDoctor;
    @FXML private TableColumn<Appointment, String> colTime;
    @FXML private Button buttonRefreshTable;
    @FXML private AnchorPane anchPaneAnalyticChart;

      @FXML
    private HBox hboxChatbot;
    @FXML
    private ScrollPane scrollpaneChat;
    @FXML
    private AnchorPane anchChatBox;
    @FXML
    private TextField textOutgoing;
    @FXML
    private ImageView sendChat;
    @FXML
    private ImageView bot;
    @FXML
    private java.lang.classfile.Label chatGenerated;
    @FXML
    private java.lang.classfile.Label myRequest;
    @FXML
    private ImageView resp;

   // NEW CORRECT CODE
public void setUser(Users user) {
    // this.user = user; // You can update or remove this line as needed
    if (user != null) {
        firstname.setText(user.getFirstname());
    }
}

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        setupUpcomingAppointmentsTable();
        handleRefreshTable(null); // Load data on initialization
        // The chart can be loaded on-demand when the user clicks the analytics icon
    }
    
    /**
     * Sets up the columns for the "Upcoming Appointments" table.
     */
    private void setupUpcomingAppointmentsTable() {
        // These now refer to the fields in the main 'Appointment' class
        colPatient.setCellValueFactory(new PropertyValueFactory<>("patientName"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<>("doctorName"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("time"));
    }

    /**
     * Fetches today's and future appointments from the database and updates the table.
     */
    @FXML
    private void handleRefreshTable(ActionEvent event) {
        ObservableList<Appointment> upcomingAppointments = FXCollections.observableArrayList();
        String sql = "SELECT p.name as patient_name, d.name as doctor_name, a.time " +
                     "FROM Appointments a " +
                     "JOIN Patients p ON a.patient_id = p.id " +
                     "JOIN Doctors d ON a.doctor_id = d.id " +
                     "WHERE a.date >= ? " + // Select appointments from today onwards
                     "ORDER BY a.date, a.time " +
                     "LIMIT 10"; // Limit to the next 10 appointments

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setDate(1, java.sql.Date.valueOf(LocalDate.now())); // Set today's date as parameter
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                // Using a constructor that fits the query result
                upcomingAppointments.add(new Appointment(0, rs.getString("patient_name"), rs.getString("doctor_name"), null, rs.getString("time")));
            }
            upcomingTable.setItems(upcomingAppointments);

        } catch (SQLException e) {
            System.err.println("Database Error: Failed to refresh upcoming appointments.");
            e.printStackTrace();
        }
    }

    /**
     * Fetches appointment data from the database and draws a monthly analysis chart.
     */
    @FXML
    private void drawChart(MouseEvent event) {
        anchPaneAnalyticChart.setVisible(!anchPaneAnalyticChart.isVisible());
        if (!anchPaneAnalyticChart.isVisible()) return;

        // 1. Fetch all appointment dates from the database
        List<LocalDate> appointmentDates = new ArrayList<>();
        String sql = "SELECT date FROM Appointments";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            
            while (rs.next()) {
                appointmentDates.add(rs.getDate("date").toLocalDate());
            }
        } catch (SQLException e) {
            System.err.println("Database Error: Could not fetch appointment dates for chart.");
            e.printStackTrace();
            return;
        }

        // 2. Count appointments per month
        Map<String, Integer> appointmentsPerMonth = new HashMap<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : months) {
            appointmentsPerMonth.put(month, 0);
        }

        for (LocalDate date : appointmentDates) {
            String monthName = months[date.getMonthValue() - 1];
            appointmentsPerMonth.put(monthName, appointmentsPerMonth.get(monthName) + 1);
        }

        // 3. Create and display the chart
        NumberAxis yAxis = new NumberAxis();
        CategoryAxis xAxis = new CategoryAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        yAxis.setLabel("Total Number of Appointments");
        xAxis.setLabel("Month");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Reservations");

        for (String month : months) {
            series.getData().add(new XYChart.Data<>(month, appointmentsPerMonth.get(month)));
        }
        lineChart.getData().add(series);

        anchPaneAnalyticChart.getChildren().clear();
        anchPaneAnalyticChart.getChildren().add(lineChart);
        AnchorPane.setTopAnchor(lineChart, 0.0);
        AnchorPane.setBottomAnchor(lineChart, 0.0);
        AnchorPane.setLeftAnchor(lineChart, 0.0);
        AnchorPane.setRightAnchor(lineChart, 0.0);

        FadeTransition fade = new FadeTransition(Duration.millis(1000), anchPaneAnalyticChart);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }

    // علشان ننتقل للصفحة اللي بدنا اياها بنستخدمه بيبحث عن ملف fxml في ال resources وبيودينا عليه
    private void navigateTo(String fxmlFile, Node sourceNode) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlFile));
        Stage stage = (Stage) sourceNode.getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
      

    @FXML private void toDoctors(MouseEvent event) throws IOException { navigateTo("Doctors.fxml", (Node) event.getSource()); }
    @FXML private void toPatient(MouseEvent event) throws IOException { navigateTo("Patients.fxml", (Node) event.getSource()); }
    @FXML private void toAppointment(MouseEvent event) throws IOException { navigateTo("Appointments.fxml", (Node) event.getSource()); }
    @FXML private void chandleCreateAppointment(ActionEvent event) throws IOException { navigateTo("Appointments.fxml", (Node) event.getSource()); }
    @FXML private void handleLogout(MouseEvent event) throws IOException { navigateTo("LogIn.fxml", (Node) event.getSource()); }
}