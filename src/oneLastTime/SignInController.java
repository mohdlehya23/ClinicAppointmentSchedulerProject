/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */

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

public class SignInController implements Initializable {

    // --- FXML Components ---
    @FXML private TextField texfieldFirstname;
    @FXML private TextField texfieldLastname;
    @FXML private TextField texfieldEmail;
    @FXML private TextField pfieldPassword;
    @FXML private TextField pfieldConfirmPassword;
    @FXML private Button buttonCreate;
    @FXML private Hyperlink hyperlinkLogin;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialization logic can be added here if needed
    }
    
    @FXML
    private void handleCreate(ActionEvent event) throws NoSuchAlgorithmException {
        if (!isInputValid()) {
            return; // التوقف إذا كانت المدخلات غير صالحة
        }
        
        if (emailExistsInDB(texfieldEmail.getText().trim())) {
            showAlert(Alert.AlertType.ERROR, "Email Exists", "This email is already registered.");
            return;
        }

        String sql = "INSERT INTO Users (first_name, last_name, email, password_hash) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, texfieldFirstname.getText().trim());
            pstmt.setString(2, texfieldLastname.getText().trim());
            pstmt.setString(3, texfieldEmail.getText().trim());
            pstmt.setString(4, toMD5(pfieldPassword.getText()));

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                showAlert(Alert.AlertType.INFORMATION, "Success", "Account created successfully! Please log in.");
                // العودة إلى شاشة تسجيل الدخول
                handleLoginHyperlink(event);
            }

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to create account: " + e.getMessage());
        }
    }
    
    private boolean emailExistsInDB(String email) {
        String sql = "SELECT id FROM Users WHERE email = ?";
        try (Connection conn = DatabaseManager.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, email);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next(); // إذا وجد سجل، يعيد true
            }
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Database Error", "Failed to check email existence.");
            return true; // نمنع التسجيل في حالة حدوث خطأ
        }
    }
    
    private boolean isInputValid() {
        if (texfieldFirstname.getText().isEmpty() || texfieldLastname.getText().isEmpty() || 
            texfieldEmail.getText().isEmpty() || pfieldPassword.getText().isEmpty() || 
            pfieldConfirmPassword.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Missing Fields", "All fields are required.");
            return false;
        }
        if (!texfieldEmail.getText().contains("@")) {
            showAlert(Alert.AlertType.ERROR, "Invalid Email", "Email must include the '@' symbol.");
            return false;
        }
        if (!pfieldPassword.getText().equals(pfieldConfirmPassword.getText())) {
            showAlert(Alert.AlertType.WARNING, "Unmatched Passwords", "The passwords do not match.");
            return false;
        }
        return true;
    }

    @FXML
    private void handleLoginHyperlink(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("LogIn.fxml"));
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle("Login");
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