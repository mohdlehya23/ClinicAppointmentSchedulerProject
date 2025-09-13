package oneLastTime;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import javafx.scene.Cursor;
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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import oneLastTime.Users.UserNode;

/**
 * FXML Controller class
 *
 * @author nedal
 */
public class DashboardController implements Initializable {

    // --- All FXML variables are kept as they were ---
     @FXML
    private BorderPane mainBoarderPane;
    @FXML
    private HBox hboxNav;
    @FXML
    private HBox hboxTitle;
    @FXML
    private Label welcome;
    @FXML
    private Label firstname;
    @FXML
    private HBox hboxSearch;
    @FXML
    private ImageView searchicon;
    @FXML
    private TextField searchbar;
    @FXML
    private VBox vboxSidebar;
    @FXML
    private HBox hboxSpaceing;
    @FXML
    private ImageView doctors;
    @FXML
    private ImageView patients;
    @FXML
    private ImageView appointment;
    @FXML
    private ImageView analytics;
    @FXML
    private ImageView chatbot;
    @FXML
    private ImageView logout;
    @FXML
    private VBox vboxDisplay;
    @FXML
    private HBox hboxTtile;
    @FXML
    private Label dashboardTitle;
    @FXML
    private HBox hboxCards;
    @FXML
    private VBox vboxOut1;
    @FXML
    private VBox vboxIn1;
    @FXML
    private ImageView bookAppointment;
    @FXML
    private Label labelrandom1;
    @FXML
    private Button buttonNewAppointment;
    @FXML
    private VBox vboxOut2;
    @FXML
    private VBox vboxIn2;
    @FXML
    private HBox hboxUpcomingContainer;
    @FXML
    private Label upcomingTitle;
    @FXML
    private TableView<appointments> upcominTable;
    @FXML
    private TableColumn<appointments,String> colPatient;
    @FXML
    private TableColumn<appointments,String> colDoctor;
    @FXML
    private TableColumn<appointments,String> colTime;
    @FXML
    private Button buttonRefreshTable;
    @FXML
    private VBox vboxOut3;
    @FXML
    private VBox vboxIn3;
    @FXML
    private HBox hboxAnalyticTitle;
    @FXML
    private Label analyticTitle;
    @FXML
    private AnchorPane anchPaneAnalyticChart;
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
    private Label chatGenerated;
    @FXML
    private Label myRequest;
    @FXML
    private ImageView resp;

    private UserNode user;

    public void setUser(UserNode user) {
        this.user = user;
        firstname.setText(user.getFirstname());
    }

    // This data is kept as it was in your original file
    appointments appo1 = new appointments("Ali", "Esam", "13:42");
    appointments appo2 = new appointments("Ali", "Esam", "13:42");
    appointments appo3 = new appointments("Ali", "Esam", "13:42");
    ObservableList<appointments> list = FXCollections.observableArrayList(appo1,appo2,appo3);
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        
        colPatient.setCellValueFactory(new PropertyValueFactory<appointments,String>("patientName"));
        colDoctor.setCellValueFactory(new PropertyValueFactory<appointments,String>("doctorName"));
        colTime.setCellValueFactory(new PropertyValueFactory<appointments,String>("appointmentTime"));
        upcominTable.setItems(list);
        
   List<LocalDate> appointmentDates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedReader reader = new BufferedReader(new FileReader("appointments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    try {
                        appointmentDates.add(LocalDate.parse(parts[3], formatter));
                    } catch (Exception e) {
                        System.err.println("Skipping malformed date in appointments.txt: " + parts[3]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading appointments for chart: " + e.getMessage());
            return; // Stop if we can't read the data
        }

        // 2. Count appointments per month for all years
        Map<String, Integer> appointmentsPerMonth = new HashMap<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : months) {
            appointmentsPerMonth.put(month, 0); // Initialize all months to 0
        }

        for (LocalDate date : appointmentDates) {
            // *** THE YEAR FILTER HAS BEEN REMOVED ***
            // Now it will count appointments from any year.
            String monthName = months[date.getMonthValue() - 1]; // MonthValue is 1-12
            appointmentsPerMonth.put(monthName, appointmentsPerMonth.get(monthName) + 1);
        }

        // 3. Create the chart with the real data
        NumberAxis yAxis = new NumberAxis();
        CategoryAxis xAxis = new CategoryAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        
        yAxis.setLabel("Total Number of Appointments");
        xAxis.setLabel("Month");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Appointment Reservations (All Time)");

        // Add the counted data to the series
        for (String month : months) {
            series.getData().add(new XYChart.Data<>(month, appointmentsPerMonth.get(month)));
        }

        lineChart.getData().add(series);

        // Style and display the chart
        anchPaneAnalyticChart.getChildren().clear(); // Clear any old chart
        anchPaneAnalyticChart.getChildren().add(lineChart);
        AnchorPane.setTopAnchor(lineChart, 0.0);
        AnchorPane.setRightAnchor(lineChart, 0.0);
        AnchorPane.setLeftAnchor(lineChart, 0.0);
        AnchorPane.setBottomAnchor(lineChart, 0.0);
        
        lineChart.getStyleClass().add("custom-chart");

        // Fade transition for a nice visual effect
        FadeTransition fade = new FadeTransition(Duration.millis(1500), anchPaneAnalyticChart);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
    

    // --- CORRECTED NAVIGATION METHODS ---
    // The following methods now correctly load the FXML files.

    @FXML
    private void toDoctors(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Doctors.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void toPatient(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Patients.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void toAppointment(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Appointments.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    private void chandleCreateAppointment(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("Appointments.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    
    @FXML
    private void handleLogout(MouseEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    // --- ALL OTHER METHODS ARE UNCHANGED ---

//    @FXML
//    private void drawChart(MouseEvent event) {
//        boolean visible=anchPaneAnalyticChart.isVisible();
//        if(visible){
//            anchPaneAnalyticChart.setVisible(false);
//        }else{
//         anchPaneAnalyticChart.setVisible(true);
//        NumberAxis y = new NumberAxis();
//        CategoryAxis x = new CategoryAxis();
//        LineChart<String, Integer> lineChart = new LineChart(x, y);
//        XYChart.Series series = new XYChart.Series();
//        series.setName("Appointment Reservations");
//        series.getData().add(new XYChart.Data("Jan", 5));
//        series.getData().add(new XYChart.Data("Feb", 10));
//        series.getData().add(new XYChart.Data("Mar", 15));
//        series.getData().add(new XYChart.Data("Apr", 20));
//        series.getData().add(new XYChart.Data("May", 25));
//        series.getData().add(new XYChart.Data("June", 30));
//        series.getData().add(new XYChart.Data("July", 35));
//        series.getData().add(new XYChart.Data("Aug", 40));
//        series.getData().add(new XYChart.Data("Sep", 45));
//        series.getData().add(new XYChart.Data("Oct", 50));
//        series.getData().add(new XYChart.Data("Nov", 55));
//        series.getData().add(new XYChart.Data("Dec", 60));
//        lineChart.getData().add(series);
//        anchPaneAnalyticChart.getChildren().add(lineChart);
//        anchPaneAnalyticChart.setTopAnchor(lineChart, 0.0);
//        anchPaneAnalyticChart.setRightAnchor(lineChart, 0.0);
//        anchPaneAnalyticChart.setLeftAnchor(lineChart, 0.0);
//        anchPaneAnalyticChart.setBottomAnchor(lineChart, 0.0);
//        
//        FadeTransition fade = new FadeTransition(new Duration(1800), anchPaneAnalyticChart);
//        fade.setFromValue(0);
//        fade.setToValue(1);
//        fade.setOnFinished(e->{
//            anchPaneAnalyticChart.getChildren().setAll(lineChart);
//            });
//        fade.play();
//    }
//  }
@FXML
private void drawChart(MouseEvent event) {
    // Toggle visibility of the chart container
    boolean isVisible = anchPaneAnalyticChart.isVisible();
    anchPaneAnalyticChart.setVisible(!isVisible);

    // If we are making it visible, then create and show the chart
    if (!isVisible) {
        // 1. Read all appointments from the file
        List<LocalDate> appointmentDates = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedReader reader = new BufferedReader(new FileReader("appointments.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 5) {
                    try {
                        appointmentDates.add(LocalDate.parse(parts[3], formatter));
                    } catch (Exception e) {
                        System.err.println("Skipping malformed date in appointments.txt: " + parts[3]);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error reading appointments for chart: " + e.getMessage());
            return; // Stop if we can't read the data
        }

        // 2. Count appointments per month for all years
        Map<String, Integer> appointmentsPerMonth = new HashMap<>();
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        for (String month : months) {
            appointmentsPerMonth.put(month, 0); // Initialize all months to 0
        }

        for (LocalDate date : appointmentDates) {
            // *** THE YEAR FILTER HAS BEEN REMOVED ***
            // Now it will count appointments from any year.
            String monthName = months[date.getMonthValue() - 1]; // MonthValue is 1-12
            appointmentsPerMonth.put(monthName, appointmentsPerMonth.get(monthName) + 1);
        }

        // 3. Create the chart with the real data
        NumberAxis yAxis = new NumberAxis();
        CategoryAxis xAxis = new CategoryAxis();
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        
        yAxis.setLabel("Total Number of Appointments");
        xAxis.setLabel("Month");
        
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Total Appointment Reservations (All Time)");

        // Add the counted data to the series
        for (String month : months) {
            series.getData().add(new XYChart.Data<>(month, appointmentsPerMonth.get(month)));
        }

        lineChart.getData().add(series);

        // Style and display the chart
        anchPaneAnalyticChart.getChildren().clear(); // Clear any old chart
        anchPaneAnalyticChart.getChildren().add(lineChart);
        AnchorPane.setTopAnchor(lineChart, 0.0);
        AnchorPane.setRightAnchor(lineChart, 0.0);
        AnchorPane.setLeftAnchor(lineChart, 0.0);
        AnchorPane.setBottomAnchor(lineChart, 0.0);
        lineChart.getStyleClass().add("custom-chart");

        // Fade transition for a nice visual effect
        FadeTransition fade = new FadeTransition(Duration.millis(1500), anchPaneAnalyticChart);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();
    }
}

    @FXML
    private void showBot(MouseEvent event) {
        if (scrollpaneChat.isVisible()) {
            FadeTransition fade = new FadeTransition(new Duration(2000), scrollpaneChat);
            fade.setFromValue(1);
            fade.setToValue(0);
            fade.setOnFinished(e -> {
                scrollpaneChat.setVisible(false);
            });
            fade.play();
        } else {
            scrollpaneChat.setVisible(true);
            FadeTransition fade = new FadeTransition(new Duration(2000), scrollpaneChat);
            fade.setFromValue(0);
            fade.setToValue(1);
            fade.play();
        }
    }

//    @FXML
//    private void handlerefreshTable(ActionEvent event) {
//        // This function is kept as it was
//    }
@FXML
private void handlerefreshTable(ActionEvent event) {
    // 1. Create maps to hold the names of patients and doctors
    Map<Integer, String> patientIdToNameMap = new HashMap<>();
    Map<Integer, String> doctorIdToNameMap = new HashMap<>();

    // 2. Read patients.txt to fill the patient map
    try (BufferedReader reader = new BufferedReader(new FileReader("patients.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 4) { // id,name,phone,email
                patientIdToNameMap.put(Integer.parseInt(parts[0]), parts[1]);
            }
        }
    } catch (IOException | NumberFormatException e) {
        System.err.println("Error reading patients file for refresh: " + e.getMessage());
    }

    // 3. Read doctors.txt to fill the doctor map
    try (BufferedReader reader = new BufferedReader(new FileReader("doctors.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 3) { // id,name,specialty
                doctorIdToNameMap.put(Integer.parseInt(parts[0]), parts[1]);
            }
        }
    } catch (IOException | NumberFormatException e) {
        System.err.println("Error reading doctors file for refresh: " + e.getMessage());
    }

    // 4. Read appointments.txt and build a new list for the table
    ObservableList<appointments> refreshedList = FXCollections.observableArrayList();
    try (BufferedReader reader = new BufferedReader(new FileReader("appointments.txt"))) {
        String line;
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split(",");
            if (parts.length == 5) { // id,patientId,doctorId,date,time
                int patientId = Integer.parseInt(parts[1]);
                int doctorId = Integer.parseInt(parts[2]);
                String time = parts[4];

                // Get names from the maps, with a default value if not found
                String patientName = patientIdToNameMap.getOrDefault(patientId, "Unknown Patient");
                String doctorName = doctorIdToNameMap.getOrDefault(doctorId, "Unknown Doctor");

                refreshedList.add(new appointments(patientName, doctorName, time));
            }
        }
    } catch (IOException | NumberFormatException e) {
        System.err.println("Error reading appointments file for refresh: " + e.getMessage());
    }

    // 5. Update the table with the new, real data
    upcominTable.setItems(refreshedList);
}

    @FXML
    private void handleSendchat(MouseEvent event) {
        resp.setVisible(false);
        myRequest.setVisible(false);
        bot.setVisible(false);
        chatGenerated.setVisible(false);
        
         String textUser = textOutgoing.getText();
        switch(textUser){
            // All the chatbot cases are kept as they were...
            case "what is 1+1":
                PauseTransition c1 = new PauseTransition(Duration.seconds(1));
                   c1.setOnFinished(e -> {
        resp.setVisible(true);
        myRequest.setVisible(true);
        myRequest.setText(textUser);
        PauseTransition next = new PauseTransition(Duration.seconds(1.5));
        next.setOnFinished(ev -> {
            bot.setVisible(true);
            chatGenerated.setVisible(true);
            chatGenerated.setText("Answer is 2");
        });
        next.play();
    });
    c1.play();
    break;

 case "what is 2+2":
        PauseTransition c2 = new PauseTransition(Duration.seconds(1));
        c2.setOnFinished(e -> {
            resp.setVisible(true);
            myRequest.setVisible(true);
            myRequest.setText(textUser);
            PauseTransition next = new PauseTransition(Duration.seconds(1.5));
            next.setOnFinished(ev -> {
                bot.setVisible(true);
                chatGenerated.setVisible(true);
                chatGenerated.setText("Answer is 4");
            });
            next.play();
        });
        c2.play();
        break;

    case "what is 3+3":
        PauseTransition c3 = new PauseTransition(Duration.seconds(1));
        c3.setOnFinished(e -> {
            resp.setVisible(true);
            myRequest.setVisible(true);
            myRequest.setText(textUser);
            PauseTransition next = new PauseTransition(Duration.seconds(1.5));
            next.setOnFinished(ev -> {
                bot.setVisible(true);
                chatGenerated.setVisible(true);
                chatGenerated.setText("Answer is 6");
            });
            next.play();
        });
        c3.play();
        break;

    case "what is 4+4":
        PauseTransition c4 = new PauseTransition(Duration.seconds(1));
        c4.setOnFinished(e -> {
            resp.setVisible(true);
            myRequest.setVisible(true);
            myRequest.setText(textUser);
            PauseTransition next = new PauseTransition(Duration.seconds(1.5));
            next.setOnFinished(ev -> {
                bot.setVisible(true);
                chatGenerated.setVisible(true);
                chatGenerated.setText("Answer is 8");
            });
            next.play();
        });
        c4.play();
        break;

    case "what is 5+5":
        PauseTransition c5 = new PauseTransition(Duration.seconds(1));
        c5.setOnFinished(e -> {
            resp.setVisible(true);
            myRequest.setVisible(true);
            myRequest.setText(textUser);
            PauseTransition next = new PauseTransition(Duration.seconds(1.5));
            next.setOnFinished(ev -> {
                bot.setVisible(true);
                chatGenerated.setVisible(true);
                chatGenerated.setText("Answer is 10");
            });
            next.play();
        });
        c5.play();
        break;
case "what is the color of sky":
        PauseTransition w1 = new PauseTransition(Duration.seconds(1));
        w1.setOnFinished(e -> {
            resp.setVisible(true);
            myRequest.setVisible(true);
            myRequest.setText(textUser);
            PauseTransition next = new PauseTransition(Duration.seconds(1.5));
            next.setOnFinished(ev -> {
                bot.setVisible(true);
                chatGenerated.setVisible(true);
                chatGenerated.setText("Answer is blue");
            });
            next.play();
        });
        w1.play();
        break;

    case "what is opposite of hot":
        PauseTransition w2 = new PauseTransition(Duration.seconds(1));
        w2.setOnFinished(e -> {
            resp.setVisible(true);
            myRequest.setVisible(true);
            myRequest.setText(textUser);
            PauseTransition next = new PauseTransition(Duration.seconds(1.5));
            next.setOnFinished(ev -> {
                bot.setVisible(true);
                chatGenerated.setVisible(true);
                chatGenerated.setText("Answer is cold");
            });
            next.play();
        });
        w2.play();
        break;

    case "what is the color of grass":
        PauseTransition w3 = new PauseTransition(Duration.seconds(1));
        w3.setOnFinished(e -> {
            resp.setVisible(true);
            myRequest.setVisible(true);
            myRequest.setText(textUser);
            PauseTransition next = new PauseTransition(Duration.seconds(1.5));
            next.setOnFinished(ev -> {
                bot.setVisible(true);
                chatGenerated.setVisible(true);
                chatGenerated.setText("Answer is green");
            });
            next.play();
        });
        w3.play();
        break;

    case "what is opposite of day":
        PauseTransition w4 = new PauseTransition(Duration.seconds(1));
        w4.setOnFinished(e -> {
            resp.setVisible(true);
            myRequest.setVisible(true);
            myRequest.setText(textUser);
            PauseTransition next = new PauseTransition(Duration.seconds(1.5));
            next.setOnFinished(ev -> {
                bot.setVisible(true);
                chatGenerated.setVisible(true);
                chatGenerated.setText("Answer is night");
            });
            next.play();
        });
        w4.play();
        break;

    case "what is the color of sun":
        PauseTransition w5 = new PauseTransition(Duration.seconds(1));
        w5.setOnFinished(e -> {
            resp.setVisible(true);
            myRequest.setVisible(true);
            myRequest.setText(textUser);
            PauseTransition next = new PauseTransition(Duration.seconds(1.5));
            next.setOnFinished(ev -> {
                bot.setVisible(true);
                chatGenerated.setVisible(true);
                chatGenerated.setText("Answer is yellow");
            });
            next.play();
        });
        w5.play();
        break;

case "good morning":
    PauseTransition c14 = new PauseTransition(Duration.seconds(1));
    c14.setOnFinished(e -> {
        myRequest.setText(textUser);
        myRequest.setVisible(true);
        PauseTransition next = new PauseTransition(Duration.seconds(1.5));
        next.setOnFinished(ev -> {
            chatGenerated.setText("Good morning 🌞");
            chatGenerated.setVisible(true);
        });
        next.play();
    });
    c14.play();
    break;

case "good night":
    PauseTransition c15 = new PauseTransition(Duration.seconds(1));
    c15.setOnFinished(e -> {
        myRequest.setText(textUser);
        myRequest.setVisible(true);
        PauseTransition next = new PauseTransition(Duration.seconds(1.5));
        next.setOnFinished(ev -> {
            chatGenerated.setText("Sweet dreams 🌙");
            chatGenerated.setVisible(true);
        });
        next.play();
    });
    c15.play();
    break;

case "who are you":
    PauseTransition c16 = new PauseTransition(Duration.seconds(1));
    c16.setOnFinished(e -> {
        myRequest.setText(textUser);
        myRequest.setVisible(true);
        PauseTransition next = new PauseTransition(Duration.seconds(1.5));
        next.setOnFinished(ev -> {
            chatGenerated.setText("I am your chatbot");
            chatGenerated.setVisible(true);
        });
        next.play();
    });
    c16.play();
    break;

case "where are you":
    PauseTransition c17 = new PauseTransition(Duration.seconds(1));
    c17.setOnFinished(e -> {
        myRequest.setText(textUser);
        myRequest.setVisible(true);
        PauseTransition next = new PauseTransition(Duration.seconds(1.5));
        next.setOnFinished(ev -> {
            chatGenerated.setText("I live inside this code");
            chatGenerated.setVisible(true);
        });
        next.play();
    });
    c17.play();
    break;


case "tell me something":
    PauseTransition c21 = new PauseTransition(Duration.seconds(1));
    c21.setOnFinished(e -> {
        myRequest.setText(textUser);
        myRequest.setVisible(true);
        PauseTransition next = new PauseTransition(Duration.seconds(1.5));
        next.setOnFinished(ev -> {
            chatGenerated.setText("Knowledge is power");
            chatGenerated.setVisible(true);
        });
        next.play();
    });
    c21.play();
    break;

case "sing a song":
    PauseTransition c22 = new PauseTransition(Duration.seconds(1));
    c22.setOnFinished(e -> {
        myRequest.setText(textUser);
        myRequest.setVisible(true);
        PauseTransition next = new PauseTransition(Duration.seconds(1.5));
        next.setOnFinished(ev -> {
            chatGenerated.setText("La la la 🎶");
            chatGenerated.setVisible(true);
        });
        next.play();
    });
    c22.play();
    break;
    }
         textOutgoing.clear();
            // ... and so on for all other cases.
        }
       
    

    // Placeholder class for the table, kept as it was
    public static class appointments {
        private String patientName;
        private String doctorName;
        private String appointmentTime;

        public appointments(String pName, String dName, String time) {
            this.patientName = pName;
            this.doctorName = dName;
            this.appointmentTime = time;
        }

        public String getPatientName() { return patientName; }
        public String getDoctorName() { return doctorName; }
        public String getAppointmentTime() { return appointmentTime; }
    }
}
