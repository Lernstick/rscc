package ch.imedias.rscc;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */


import ch.imedias.rscc.util.FXMLGuiLoader;
import java.io.IOException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * The JavaFX Application which creates a stage and fills it with a scene.
 * @author sschw
 */
public class RemoteSupportApplication extends Application {
    
    /**
     * Starts the application by loading the first scene from the {@link FXMLGuiLoader}.
     * @param primaryStage the main stage of the application
     * @throws IOException 
     */
    @Override
    public void start(Stage primaryStage) throws IOException {
        Scene scene = FXMLGuiLoader.getInstance().getRemoteSupportStart();
        primaryStage.setTitle(FXMLGuiLoader.getInstance().getApplicationTitle());
        primaryStage.setScene(scene);
        primaryStage.setMinHeight(300);
        primaryStage.setMinWidth(400);
        primaryStage.show();
    }

    /**
     * Starts the JavaFX Application.
     * @param args the command line arguments (unused)
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called when the application is finished.<br>
     * <br>
     * Calls {@link FXMLGuiLoader#finalizeGuis()} which calls finalize on controllers.
     * @throws Exception 
     */
    @Override
    public void stop() throws Exception {
        FXMLGuiLoader.getInstance().finalizeGuis();
        Platform.exit();
    }
    
    
    
}
