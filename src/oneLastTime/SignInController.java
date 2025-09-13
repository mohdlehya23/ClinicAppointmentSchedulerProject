/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/javafx/FXMLController.java to edit this template
 */
package oneLastTime;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ResourceBundle;
import java.util.Scanner;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import oneLastTime.Login;
import oneLastTime.Users.UserNode;

public class SignInController implements Initializable {
    
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private VBox vboxForm;
    @FXML
    private VBox vboxImage;
    @FXML
    private Label labelTtile;
    @FXML
    private Label labelSubtitle;
    @FXML
    private GridPane gridForm;
    @FXML
    private TextField pfieldPassword;
    @FXML
    private VBox vboxActions;
    @FXML
    private Button buttonCreate;
    @FXML
    private HBox hboxFooter;
    @FXML
    private Label labelAlreadyHave;
    @FXML
    private TextField texfieldFirstname;
    @FXML
    private TextField texfieldLastname;
    @FXML
    private TextField texfieldEmail;
    @FXML
    private TextField pfieldConfirmPassword;
    @FXML
    private Hyperlink hyperlinkLogin;
    @FXML
    private ImageView imageviewHi;
    @FXML
    private Label labelWelcome;
    @FXML
    private Label labelInfo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }
    
    @FXML
    private void handleLoginHyperlink(ActionEvent event) throws IOException {
        new Login();
    }
    
     Users users = new Users();
    
    @FXML
    private void handleCreate(ActionEvent event) throws NoSuchAlgorithmException, IOException {
        //Check Empty fields
        if (texfieldFirstname.getText().isEmpty() || texfieldLastname.getText().isEmpty() || texfieldEmail.getText().isEmpty()
                || pfieldPassword.getText().isEmpty() || pfieldConfirmPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Missing Fields");
            alert.showAndWait();
            
            //Check email syntax validity
        } else if (!texfieldEmail.getText().contains("@")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Email");
            alert.setContentText("Email must include @ sign");
            alert.showAndWait();
            
            //Check password and confirm identically
        } else if (!pfieldPassword.getText().equals(pfieldConfirmPassword.getText())) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Unmatched Passwords");
            alert.setContentText("The Passwords are not identical, please check them again");
            alert.showAndWait();
            
            pfieldConfirmPassword.clear();
            
        } else {
            
            //Check existence
            
            boolean notfound = true;
            try {
                Scanner in = new Scanner(new File("./src/oneLastTime/users.txt"));
                boolean exists = false;
                
                while (in.hasNext()) {
                    if (texfieldEmail.getText().equals(in.next())) {
                        exists = true;
                        break;
                    }
                }
                
                if (exists) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Account Already Exist");
                    alert.show();
                } else {
                    // Node 4 parameters Constructor adds them to the file
                    UserNode u = new UserNode(texfieldFirstname.getText(), texfieldLastname.getText(), texfieldEmail.getText(), toMD5(pfieldPassword.getText()));
                    users.add(u);
                    
                    // pass the user to the Login then to Dashboard to extract the first name
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("LogIn.fxml"));
                    Parent pane = loader.load();
                    LogInController lc = loader.getController();
                    lc.setUser(u);
                    
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Valid");
                    alert.setContentText("Access");
                    alert.show();
//                    new Login();
                }
                
                in.close();
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
    
    public String toMD5(String text) throws NoSuchAlgorithmException {
        MessageDigest md = MessageDigest.getInstance("MD5");
        md.update(text.getBytes());
        byte[] digest = md.digest();      // array of bytes to store the text
        StringBuilder sb = new StringBuilder();   
        for (byte b : digest) {
            sb.append(String.format("%02x", b));   // two digits hexadecimal
        }
        return sb.toString();
    }
}
