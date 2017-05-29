/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import ch.imedias.rscc.model.Settings;
import ch.imedias.rscc.util.FXMLGuiLoader;
import ch.imedias.rscc.util.PasswordChanger;
import ch.imedias.rscc.util.ProcessExecutorFactory;
import ch.imedias.rscc.util.ProvideSupportExecutor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
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
    private CheckBox chkSSHPort;
    @FXML
    private Button cmdStartService;
    @FXML
    private Label lbPort;
    @FXML
    private TextField tfPort;
    @FXML
    private Label lbPWLabel;
    @FXML
    private Label lbPWValue;
    
    // Language bundle
    private static ResourceBundle BUNDLE;
    
    // Describes if the service is started
    private SimpleBooleanProperty serviceStarted = new SimpleBooleanProperty(false);
    
    private ProvideSupportExecutor executor;
    @FXML

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        BUNDLE = rb;
        executor = new ProvideSupportExecutor(new ProcessExecutorFactory());

        cboCompression.getItems().addAll(1,2,3,4,5,6,7,8,9);
        cboQuality.getItems().addAll(1,2,3,4,5,6,7,8,9);
        
        // Read saved values
        cboCompression.setValue(Settings.getCompressionLevel());
        cboQuality.setValue(Settings.getQuality());
        chk8BitColor.setSelected(Settings.getBgr233());
        chkSSHPort.setSelected(Settings.getUseHttpsPort());
        
        // Disable controls after starting remote
        cboCompression.disableProperty().bind(serviceStarted);
        cboQuality.disableProperty().bind(serviceStarted);
        chk8BitColor.disableProperty().bind(serviceStarted);
        chkSSHPort.disableProperty().bind(serviceStarted);
        
        lbPort.visibleProperty().setValue(false);
        tfPort.visibleProperty().setValue(false);
        lbPWLabel.visibleProperty().setValue(false);
        lbPWValue.visibleProperty().setValue(false);
        
        // Set and generate new Password
        lbPWValue.setText(PasswordChanger.setRandomPassword());
    }    

    @FXML
    private void onBackAction(ActionEvent event) {
        Scene scene = ((Node)event.getSource()).getScene();
        Stage stage = (Stage)scene.getWindow();
        stage.setScene(FXMLGuiLoader.getInstance().getRemoteSupportStart());
    }

    @FXML
    private void onStartServiceAction(ActionEvent event) {
        
        if (serviceStarted.get()){
            // Stop service
            executor.stopOffer();
            
            cmdStartService.setText(BUNDLE.getString("Start_Service"));
            serviceStarted.set(false);
        } else {
            // Start service
            executor.startOffer( 
                        cboCompression.getValue(), 
                        cboQuality.getValue(), 
                        chk8BitColor.isSelected(),
                        chkSSHPort.isSelected(),
                        tfPort.getText());
            
            cmdStartService.setText(BUNDLE.getString("Stop_Service"));
            serviceStarted.set(true);
        }
    }

    @FXML
    private void onCompressionChangedAction(ActionEvent event) {
        Settings.setCompressionLevel(cboCompression.getValue());
    }

    @FXML
    private void on8BitChangedAction(ActionEvent event) {
        Settings.setBgr233(chk8BitColor.isSelected());
    }

    @FXML
    private void onQualityChangedAction(ActionEvent event) {
        Settings.setQuality(cboQuality.getValue());
    }
    
    @FXML
    private void onSSHChangedAction(ActionEvent event) {
        Settings.setUseSSHPort(chkSSHPort.isSelected());
        if (chkSSHPort.isSelected()) {
            lbPort.visibleProperty().setValue(true);
            tfPort.visibleProperty().setValue(true);
            lbPWLabel.visibleProperty().setValue(true);
            lbPWValue.visibleProperty().setValue(true); 
        } else {
            lbPort.visibleProperty().setValue(false);
            tfPort.visibleProperty().setValue(false);
            lbPWLabel.visibleProperty().setValue(false);
            lbPWValue.visibleProperty().setValue(false);
        }
    }
    public void finalizeGui() {
        executor.exit();
        executor = null;
        
        // Save settings on application close
        Settings.save();
    }
    
}
