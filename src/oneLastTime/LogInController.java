/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
//package oneLastTime;
//
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.IOException;
//import java.net.URL;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.util.ResourceBundle;
//import java.util.Scanner;
//import javafx.event.ActionEvent;
//import javafx.fxml.FXML;
//import javafx.fxml.FXMLLoader;
//import javafx.fxml.Initializable;
//import javafx.scene.Node;
//import javafx.scene.Parent;
//import javafx.scene.Scene;
//import javafx.scene.control.Alert;
//import javafx.scene.control.Button;
//import javafx.scene.control.Hyperlink;
//import javafx.scene.control.Label;
//import javafx.scene.control.TextField;
//import javafx.scene.image.ImageView;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.GridPane;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.VBox;
//import javafx.stage.Stage;
//import oneLastTime.Users.UserNode;
//
///**
// * FXML Controller class
// *
// * @author nedal
// */
//public class LogInController implements Initializable {
//
//    @FXML
//    private BorderPane mainBorderPane;
//    @FXML
//    private VBox vboxForm;
//    @FXML
//    private Label labelTtile;
//    @FXML
//    private Label labelSubtitle;
//    @FXML
//    private GridPane gridForm;
//    @FXML
//    private TextField textfieldUsername;
//    @FXML
//    private TextField pfieldPassword;
//    @FXML
//    private VBox vboxActions;
//    @FXML
//    private Button buttonCreate;
//    @FXML
//    private HBox hboxFooter;
//    @FXML
//    private Label labelAlreadyHave;
//    @FXML
//    private Hyperlink hyperlinkSignup;
//    @FXML
//    private VBox vboxImage;
//    @FXML
//    private ImageView imageRes;
//    @FXML
//    private Label resGreeting;
//    @FXML
//    private Label info;
//
//    private UserNode user;
//
//    public void setUser(UserNode user) {
//        this.user = user;
//    }
//
//    @Override
//    public void initialize(URL url, ResourceBundle rb) {
//        // TODO
//    }
//
//    @FXML
//    private void handleLogin(ActionEvent event) throws IOException, NoSuchAlgorithmException {
//        //Check for empty fields
//        if (textfieldUsername.getText().isEmpty() || pfieldPassword.getText().isEmpty()) {
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setContentText("Missing Fields");
//            alert.showAndWait();
//        } else {
//            Scanner in = new Scanner(new File("./src/oneLastTime/users.txt"));
//            boolean exists = false;
//            while (in.hasNextLine()) {
//                String line = in.nextLine();
//                String[] parts = line.trim().split("\\s+");  // split on 1+ spaces
//                String fname = parts[0];
//                String lname = parts[1];
//                String email = parts[2];
//                String hash = parts[3];
//
//                if (textfieldUsername.getText().equals(email)) {
//                    exists = true;
//                    
//
//                    if (toMD5(pfieldPassword.getText()).equals(hash)) {
//
//                        user = new UserNode(fname); 
//
//                        // Load Dashboard
//                        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
//                        Parent pane = loader.load();
//                        DashboardController dc = loader.getController();
//                        dc.setUser(user);
//
//                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
//                        Scene scene = new Scene(pane);
//                        stage.setScene(scene);
//                        stage.setTitle("Dashboard");
//                        stage.show();
//
//                        // I had to load the dashboard here in order to apply the transition 
////                        DashboardController dc = new DashboardController();
////                        dc.setFirstName(labelUsername.getText());
////                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
////                    alert.setTitle("Valid");
////                    alert.setContentText("Welcome " + textfieldUsername.getText());
////                    alert.show();
//                    } else {
//                        Alert alert = new Alert(Alert.AlertType.ERROR);
//                        alert.setTitle("Error");
//                        alert.setContentText("Incorrect Password");
//                        alert.show();
//
//                    }
//
//                } 
//                
//
////             try {
////                Scanner in = new Scanner(new File("./src/Project_Final/users.txt"));
////                while (in.hasNextLine()) {
////                    if (textfieldUsername.getText().equals(in.next())) {
////                        if (!toMD5(passwordfieldPassword.getText()).equals(in.next())) {
////                            Alert alert = new Alert(Alert.AlertType.ERROR);
////                            alert.setTitle("Error");
////                            alert.setContentText("Incorrect Password");
////                            alert.showAndWait();
////                            break;
////                        } else {
////                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
////                            alert.setTitle("Valid");
////                            alert.setContentText("Welcome " + textfieldUsername.getText());
////                            alert.showAndWait();
////                            break;
////                        }
////                    } else {
////                        Alert alert = new Alert(Alert.AlertType.ERROR);
////                        alert.setTitle("Error");
////                        alert.setContentText("User does NOT exist");
////                        alert.showAndWait();
////                    }
////                }    
////                    in.close();
////                }catch (FileNotFoundException ex) {
////                ex.printStackTrace();
////            }catch (NoSuchAlgorithmException ex) {
////                ex.printStackTrace();
////            }
//            }
//            in.close();
//            if(!exists){
//                
//                    Alert alert = new Alert(Alert.AlertType.ERROR);
//                    alert.setTitle("Error");
//                    alert.setContentText("User does NOT exist");
//                    alert.showAndWait();
//            }
//            
//
//        }
//    }
//
//    public String toMD5(String text) throws NoSuchAlgorithmException {
//        MessageDigest md = MessageDigest.getInstance("MD5");
//        md.update(text.getBytes());
//        byte[] digest = md.digest();
//        StringBuilder sb = new StringBuilder();
//        for (byte b : digest) {
//            sb.append(String.format("%02x", b));
//        }
//        return sb.toString();
//    }
//
//    @FXML
//    private void handleLoginHyperlink(ActionEvent event) throws IOException {
//        textfieldUsername.clear();
//        pfieldPassword.clear();
//        new Signup();
//    }
//
//}
package oneLastTime;

import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class LogInController implements Initializable {

    // --- FXML Components ---
    @FXML private TextField textfieldUsername; // This is the email field
    @FXML private TextField pfieldPassword;
    @FXML private Button buttonCreate;
    @FXML private Hyperlink hyperlinkSignup;
    
    // This variable will hold the logged-in user's data
    private Users loggedInUser;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic can be added here if needed
    }

    @FXML
    private void handleLogin(ActionEvent event) throws NoSuchAlgorithmException {
        String email = textfieldUsername.getText().trim();
        String password = pfieldPassword.getText();

        if (email.isEmpty() || password.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "Email and password are required.");
            return;
        }

        String sql = "SELECT id, first_name, last_name, password_hash FROM Users WHERE email = ?";
        
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            ResultSet rs = pstmt.executeQuery();

            if (rs.next()) { // إذا تم العثور على المستخدم
                String storedHash = rs.getString("password_hash");
                String enteredHash = toMD5(password);

                if (storedHash.equals(enteredHash)) {
                    // كلمة المرور صحيحة، قم بتسجيل الدخول
                    int id = rs.getInt("id");
                    String firstName = rs.getString("first_name");
                    String lastName = rs.getString("last_name");
                    
                    // إنشاء كائن للمستخدم الذي سجل دخوله
                    loggedInUser = new Users(id, firstName, lastName, email);

                    // الانتقال إلى لوحة التحكم
                    loadDashboard(event);
                } else {
                    // كلمة مرور خاطئة
                    showAlert(Alert.AlertType.ERROR, "Login Failed", "Incorrect password.");
                }
            } else {
                // البريد الإلكتروني غير موجود
                showAlert(Alert.AlertType.ERROR, "Login Failed", "No account found with this email.");
            }
            
        } catch (SQLException | IOException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to log in: " + e.getMessage());
        }
    }

    private void loadDashboard(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
        Parent root = loader.load();

        // تمرير اسم المستخدم إلى DashboardController
        DashboardController dashboardController = loader.getController();
        // Since the UserNode is gone, we create a temporary object or pass the string directly
        // For simplicity, let's assume setUser in dashboard can handle a simple User object or just the name
        // Let's create a dummy UserNode just for passing the name as per your old structure
       dashboardController.setUser(loggedInUser); // Adjust this if you refactor DashboardController
        
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Clinic Dashboard");
        stage.show();
    }

    @FXML
    private void handleSignupHyperlink(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("SignIn.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Create a new account");
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String toMD5(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes());
        byte[] digest = md.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : digest) {
            sb.append(String.format("%02x", b));
        }
        return sb.toString();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}