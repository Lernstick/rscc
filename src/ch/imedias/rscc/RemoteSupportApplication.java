/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc;

import ch.imedias.rscc.util.FXMLGuiLoader;
import java.io.IOException;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 *
 * @author user
 */
public class RemoteSupportApplication extends Application {
    
    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = FXMLGuiLoader.getInstance().getRemoteSupportStart();
        primaryStage.setTitle(FXMLGuiLoader.getInstance().getApplicationTitle());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() throws Exception {
        FXMLGuiLoader.getInstance().finalizeGuis();
    }
    
    
    
}
