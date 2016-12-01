/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import ch.imedias.rscc.model.SupportAddress;
import ch.imedias.rscc.util.FXMLGuiLoader;
import ch.imedias.rscc.util.RequestSupportExecutor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author sschw
 */
public class RequestSupportController implements Initializable {
    
    private final static double[] IMAGESCALES = { 0.5, 1, 2};
    
    @FXML
    private ComboBox<Double> cboImagescale;
    @FXML
    private ComboBox<SupportAddress> cboSupporter;
    
    private ResourceBundle bundle;
    private RequestSupportExecutor executor;

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        
        for(double val : IMAGESCALES) {
            cboImagescale.getItems().add(val);
        }
        cboImagescale.getSelectionModel().select(1.0);
        
        cboSupporter.getItems().addAll(SupportAddress.getAll());
        cboSupporter.getSelectionModel().selectFirst();
    }

    @FXML
    private void onBackAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        
        stage.setScene(FXMLGuiLoader.getInstance().getRemoteSupportStart());
    }

    @FXML
    private void onConnectAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        
        SupportAddress supportAddress = cboSupporter.getValue();
        Double scale = cboImagescale.getValue();
        
        stage.setScene(FXMLGuiLoader.getInstance().getRequestSupportConnecting(supportAddress.getDescription()));
        
        // Initialize connecter giving the current stage
        if(executor == null)
            executor = new RequestSupportExecutor(() -> openConnected(stage), () -> openConnectedFailed(stage));
        // Start executor to connect
        executor.connect(supportAddress, scale);
    }

    private void openConnected(Stage stage) {
        stage.setScene(FXMLGuiLoader.getInstance().getRequestSupportConnected(cboSupporter.getValue().getDescription(), executor));
    }

    private void openConnectedFailed(Stage stage) {
        stage.setScene(FXMLGuiLoader.getInstance().getRequestSupport());
        
        Scene errorDialog = FXMLGuiLoader.getInstance().getErrorDialog();
        FXMLGuiLoader.getInstance().createDialog(stage, errorDialog, bundle.getString("Error"), true).show();
    }

    @FXML
    private void onEditSupporterlistAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        
        Scene editDialog = FXMLGuiLoader.getInstance().getEditDialog();
        //TODO Set title
        FXMLGuiLoader.getInstance().createDialog(stage, editDialog, bundle.getString("EditDialog.title"), true).showAndWait();
        
        SupportAddress sa = cboSupporter.getValue();
        cboSupporter.getItems().clear();
        cboSupporter.getItems().addAll(SupportAddress.getAll());
        cboSupporter.getSelectionModel().select(sa);
    }

    public void finalizeGui() {
        if(executor != null) {
            executor.disconnect();
            executor = null;
        }
    }
}
