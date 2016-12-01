/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import ch.imedias.rscc.controller.RequestSupportConnectedController;
import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import java.util.ResourceBundle;


/**
 *
 * @author Roger Obrist
 */
public final class FXMLGuiLoader {
    private static ResourceBundle BUNDLE;
    private static final FXMLGuiLoader INSTANCE = new FXMLGuiLoader();
    
    private Scene remoteSupportStart;
    private Scene provideSupport;
    private Scene requestSupport;
    private Scene editDialog;
    private Scene requestSupportConnecting;
    private Scene requestSupportConnected;
    
    // Controller
    private RequestSupportConnectedController requestSupportConnectedController;
    
    private FXMLGuiLoader() {
        // Create all instances
        try {
            BUNDLE = ResourceBundle.getBundle("ch/imedias/rscc/Bundle");
            remoteSupportStart = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/RemoteSupportStart.fxml"), BUNDLE));
            provideSupport = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/ProvideSupport.fxml"), BUNDLE));
            requestSupport = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/RequestSupport.fxml"), BUNDLE));
            editDialog = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/EditDialog.fxml"), BUNDLE));  
            requestSupportConnecting = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/RequestSupportConnecting.fxml"), BUNDLE));
            
            FXMLLoader loadConnected = new FXMLLoader(getClass().getResource("../view/RequestSupportConnected.fxml"), BUNDLE);
            requestSupportConnected = new Scene((Parent) loadConnected.load());
            requestSupportConnectedController = (RequestSupportConnectedController) loadConnected.getController();
        } catch(IOException ex) {
               ex.printStackTrace();
        }
    }
    
    public static FXMLGuiLoader getInstance() {
        return INSTANCE;
    }
    
    public Scene getRemoteSupportStart() {
        return remoteSupportStart;
    }
    
    public Scene getProvideSupport() {
        return provideSupport;
    }
    
    public Scene getRequestSupport() {
        return requestSupport;
    }
    
    public Scene getRequestSupportConnecting() {
        return requestSupportConnecting;
    }
    
    public Scene getRequestSupportConnected(String supporter) {
        requestSupportConnectedController.setSupporter(supporter);
        return requestSupportConnected;
    }
    
    public Scene getEditDialog() {
        return editDialog;
    }
}
