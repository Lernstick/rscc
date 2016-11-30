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

/**
 *
 * @author user
 */
public final class FXMLGuiLoader {
    private static FXMLGuiLoader instance = new FXMLGuiLoader();
    
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
            remoteSupportStart = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/RemoteSupportStart.fxml")));
            provideSupport = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/ProvideSupport.fxml")));
            requestSupport = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/RequestSupport.fxml")));
            editDialog = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/EditDialog.fxml")));  
            requestSupportConnecting = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/RequestSupportConnecting.fxml")));
            
            FXMLLoader loadConnected = new FXMLLoader(getClass().getResource("../view/RequestSupportConnected.fxml"));
            requestSupportConnected = new Scene((Parent) loadConnected.load());
            requestSupportConnectedController = (RequestSupportConnectedController) loadConnected.getController();
        } catch(IOException ex) {
               ex.printStackTrace();
        }
    }
    
    public static FXMLGuiLoader getInstance() {
        return instance;
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
