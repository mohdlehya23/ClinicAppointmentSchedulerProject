/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package oneLastTime;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

/**
 *
 * @author nedal
 */
public class Signup {
    Stage stage= new Stage();
    public Signup() throws IOException{
         FXMLLoader loader = new FXMLLoader(getClass().getResource("SignIn.fxml"));
        BorderPane pane = loader.load();
        Scene scene = new Scene(pane);
        stage.setScene(scene);
        stage.setTitle("Create a new account ");
        stage.show();
        
    }
}
