/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import ch.imedias.rscc.model.Settings;
import ch.imedias.rscc.util.FXMLGuiLoader;
import ch.imedias.rscc.util.ProcessExecutorFactory;
import ch.imedias.rscc.util.ProvideSupportExecutor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
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
    
    // Language bundle
    private static ResourceBundle BUNDLE;
    
    // Describes if the service is started
    private SimpleBooleanProperty serviceStarted = new SimpleBooleanProperty(false);
    
    private ProvideSupportExecutor executor;

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
        chkHttpsPort.setSelected(Settings.getUseHttpsPort());
        txtSafePorts.setText(Settings.getSecurePorts());
        
        // Disable controls after starting remote
        cboCompression.disableProperty().bind(serviceStarted);
        cboQuality.disableProperty().bind(serviceStarted);
        chk8BitColor.disableProperty().bind(serviceStarted);
        chkHttpsPort.disableProperty().bind(serviceStarted);
        
        // Also disable SafePorts if https is selected
        txtSafePorts.disableProperty().bind(Bindings.or(serviceStarted, chkHttpsPort.selectedProperty()));
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
            executor.startOffer(txtSafePorts.getText(), 
                        cboCompression.getValue(), 
                        cboQuality.getValue(), 
                        chk8BitColor.isSelected());
            
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
    private void onHttpsChangedAction(ActionEvent event) {
        Settings.setUseHttpsPort(chkHttpsPort.isSelected());
    }

    @FXML
    private void OnSecurePortsTyped(KeyEvent event) {
        Settings.setSecurePorts(txtSafePorts.getText());
    }

    public void finalizeGui() {
        executor.stopOffer();
        executor = null;
        
        // Save settings on application close
        Settings.save();
    }
    
}
