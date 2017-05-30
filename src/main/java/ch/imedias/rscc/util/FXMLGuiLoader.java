/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import ch.imedias.rscc.controller.ErrorDialogController;
import ch.imedias.rscc.controller.ProvideSupportController;
import ch.imedias.rscc.controller.RequestSupportConnectedController;
import ch.imedias.rscc.controller.RequestSupportConnectingController;
import ch.imedias.rscc.controller.RequestSupportController;
import java.io.IOException;
import java.util.ResourceBundle;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

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
    private Scene errorDialog;
    
    // Controllers
    private RequestSupportConnectedController requestSupportConnectedController;
    private RequestSupportConnectingController requestSupportConnectingController;
    private ErrorDialogController errorDialogController;
    private RequestSupportController requestSupportController;
    private ProvideSupportController provideSupportController;
    
    private FXMLGuiLoader() {
        // Create all instances with their controllers
        try {
	    BUNDLE = ResourceBundle.getBundle("ch/imedias/rscc/Bundle");
            remoteSupportStart = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/RemoteSupportStart.fxml"), BUNDLE));
            
            FXMLLoader loadProvSup = new FXMLLoader(getClass().getResource("../view/ProvideSupport.fxml"), BUNDLE);
            provideSupport = new Scene((Parent)loadProvSup.load());
            provideSupportController = (ProvideSupportController) loadProvSup.getController();
            
            FXMLLoader loadReqSup = new FXMLLoader(getClass().getResource("../view/RequestSupport.fxml"), BUNDLE);
            requestSupport = new Scene((Parent)loadReqSup.load());
            requestSupportController = (RequestSupportController) loadReqSup.getController();
            
            editDialog = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/EditDialog.fxml"), BUNDLE));  
            
            FXMLLoader loadConnecting = new FXMLLoader(getClass().getResource("../view/RequestSupportConnecting.fxml"), BUNDLE);
            requestSupportConnecting = new Scene((Parent) loadConnecting.load());
            requestSupportConnectingController = (RequestSupportConnectingController) loadConnecting.getController();
            
            FXMLLoader loadConnected = new FXMLLoader(getClass().getResource("../view/RequestSupportConnected.fxml"), BUNDLE);
            requestSupportConnected = new Scene((Parent) loadConnected.load());
            requestSupportConnectedController = (RequestSupportConnectedController) loadConnected.getController();
            
            FXMLLoader loadError = new FXMLLoader(getClass().getResource("../view/ErrorDialog.fxml"), BUNDLE);
            errorDialog = new Scene((Parent) loadError.load());
            errorDialogController = (ErrorDialogController) loadError.getController();
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
    
    /**
     * 
     * @param supporter name of supporter
     * @return Supporter scene
     */
    public Scene getRequestSupportConnecting(String supporter) {
        requestSupportConnectingController.setSupporter(supporter);
        return requestSupportConnecting;
    }
    
    /**
     * 
     * @param supporter name of supporter
     * @param executor remote connector, handles vnc workflow
     * @return RequestSupportConnected scene
     */
    public Scene getRequestSupportConnected(String supporter, RequestSupportExecutor executor) {
        requestSupportConnectedController.setSupporter(supporter);
        requestSupportConnectedController.setExecutor(executor);
        return requestSupportConnected;
    }
    
    /**
     * 
     * @return edit dialog
     */
    public Scene getEditDialog() {
        return editDialog;
    }
    
    /**
     * 
     * @return error dialog
     */
    public Scene getErrorDialog() {
        return errorDialog;
    }
    
    /**
     * 
     * @param parent parent
     * @param scene scene
     * @param title stage title
     * @param modal modal
     * @return 
     */
    public Stage createDialog(Stage parent, Scene scene, String title, boolean modal) {
        Stage stage = new Stage();
        stage.initOwner(parent);
        if(modal) {
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setAlwaysOnTop(modal);
        }
        stage.setTitle(title);
        stage.setScene(scene);
        return stage;
    }
    

    /**
     * Calls finalizeGuis methods on controllers
     */
    public void finalizeGuis() {
        requestSupportController.finalizeGui();
        provideSupportController.finalizeGui();
    }
    
    /**
     * 
     * @return Application title
     */
    public String getApplicationTitle() {
        return BUNDLE.getString("RemoteSupportStart.title");
    }
}
