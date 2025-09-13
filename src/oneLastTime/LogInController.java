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
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
import javafx.stage.Stage;
import oneLastTime.Users.UserNode;

/**
 * FXML Controller class
 *
 * @author nedal
 */
public class LogInController implements Initializable {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private VBox vboxForm;
    @FXML
    private Label labelTtile;
    @FXML
    private Label labelSubtitle;
    @FXML
    private GridPane gridForm;
    @FXML
    private TextField textfieldUsername;
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
    private Hyperlink hyperlinkSignup;
    @FXML
    private VBox vboxImage;
    @FXML
    private ImageView imageRes;
    @FXML
    private Label resGreeting;
    @FXML
    private Label info;

    private UserNode user;

    public void setUser(UserNode user) {
        this.user = user;
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
    }

    @FXML
    private void handleLogin(ActionEvent event) throws IOException, NoSuchAlgorithmException {
        //Check for empty fields
        if (textfieldUsername.getText().isEmpty() || pfieldPassword.getText().isEmpty()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setContentText("Missing Fields");
            alert.showAndWait();
        } else {
            Scanner in = new Scanner(new File("./src/oneLastTime/users.txt"));
            boolean exists = false;
            while (in.hasNextLine()) {
                String line = in.nextLine();
                String[] parts = line.trim().split("\\s+");  // split on 1+ spaces
                String fname = parts[0];
                String lname = parts[1];
                String email = parts[2];
                String hash = parts[3];

                if (textfieldUsername.getText().equals(email)) {
                    exists = true;
                    

                    if (toMD5(pfieldPassword.getText()).equals(hash)) {

                        user = new UserNode(fname); 

                        // Load Dashboard
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("Dashboard.fxml"));
                        Parent pane = loader.load();
                        DashboardController dc = loader.getController();
                        dc.setUser(user);

                        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
                        Scene scene = new Scene(pane);
                        stage.setScene(scene);
                        stage.setTitle("Dashboard");
                        stage.show();

                        // I had to load the dashboard here in order to apply the transition 
//                        DashboardController dc = new DashboardController();
//                        dc.setFirstName(labelUsername.getText());
//                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                    alert.setTitle("Valid");
//                    alert.setContentText("Welcome " + textfieldUsername.getText());
//                    alert.show();
                    } else {
                        Alert alert = new Alert(Alert.AlertType.ERROR);
                        alert.setTitle("Error");
                        alert.setContentText("Incorrect Password");
                        alert.show();

                    }

                } 
                

//             try {
//                Scanner in = new Scanner(new File("./src/Project_Final/users.txt"));
//                while (in.hasNextLine()) {
//                    if (textfieldUsername.getText().equals(in.next())) {
//                        if (!toMD5(passwordfieldPassword.getText()).equals(in.next())) {
//                            Alert alert = new Alert(Alert.AlertType.ERROR);
//                            alert.setTitle("Error");
//                            alert.setContentText("Incorrect Password");
//                            alert.showAndWait();
//                            break;
//                        } else {
//                            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
//                            alert.setTitle("Valid");
//                            alert.setContentText("Welcome " + textfieldUsername.getText());
//                            alert.showAndWait();
//                            break;
//                        }
//                    } else {
//                        Alert alert = new Alert(Alert.AlertType.ERROR);
//                        alert.setTitle("Error");
//                        alert.setContentText("User does NOT exist");
//                        alert.showAndWait();
//                    }
//                }    
//                    in.close();
//                }catch (FileNotFoundException ex) {
//                ex.printStackTrace();
//            }catch (NoSuchAlgorithmException ex) {
//                ex.printStackTrace();
//            }
            }
            in.close();
            if(!exists){
                
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("User does NOT exist");
                    alert.showAndWait();
            }
            

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

    @FXML
    private void handleLoginHyperlink(ActionEvent event) throws IOException {
        textfieldUsername.clear();
        pfieldPassword.clear();
        new Signup();
    }

}
