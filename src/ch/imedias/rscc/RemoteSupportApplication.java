/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc;

import java.io.IOException;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *
 * @author user
 */
public class RemoteSupportApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        //java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ch/imedias/rscc/Bundle"); // NOI18N
        Parent parent = FXMLLoader.load(getClass().getResource("view/RemoteSupportStart.fxml")/*, bundle*/);
        Scene scene = new Scene(parent);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
}
