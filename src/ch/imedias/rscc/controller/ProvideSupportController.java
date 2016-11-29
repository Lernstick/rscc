/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import ch.imedias.rscc.util.FXMLGuiLoader;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author user
 */
public class ProvideSupportController implements Initializable {

    @FXML
    private ComboBox<Integer> cboCompression;
    @FXML
    private CheckBox chk8BitColor;
    @FXML
    private ComboBox<Integer> cboQuality;
    @FXML
    private TextField txtSafePorts;
    @FXML
    private CheckBox chkHttpsPort;
    @FXML
    private Button cmdStartService;
    
    // Describes if the service is started
    private boolean serviceStarted = false;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        cboCompression.getItems().addAll(1,2,3,4,5,6,7,8,9);
        // TODO: set value from model
        cboQuality.getItems().addAll(1,2,3,4,5,6,7,8,9);
        // TODO: set value from model

    }    

    @FXML
    private void onBackAction(ActionEvent event) {
        Scene scene = ((Node)event.getSource()).getScene();
        Stage stage = (Stage)scene.getWindow();
        stage.setScene(FXMLGuiLoader.getInstance().getRemoteSupportStart());
    }

    @FXML
    private void onStartServiceAction(ActionEvent event) {
        if (serviceStarted){
            // Stop service
            cmdStartService.setText("Dienst starten");
            serviceStarted = false;
            
            // Enable components
            cboCompression.setDisable(false);
            cboQuality.setDisable(false);
            chk8BitColor.setDisable(false);
            chkHttpsPort.setDisable(false);
            txtSafePorts.setDisable(false);
        } else {
            // Start service
            
            // Disable controls
            cboCompression.setDisable(true);
            cboQuality.setDisable(true);
            chk8BitColor.setDisable(true);
            chkHttpsPort.setDisable(true);
            txtSafePorts.setDisable(true);
            
            cmdStartService.setText("Dienst stoppen");
            serviceStarted = true;
        }
    }
    
}
