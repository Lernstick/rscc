/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import ch.imedias.rscc.util.FXMLGuiLoader;
import ch.imedias.rscc.util.RequestSupportExecutor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author sschw
 */
public class RequestSupportConnectedController implements Initializable {

    private ResourceBundle bundle;
    
    private RequestSupportExecutor executor;
    
    @FXML
    private Label lblConnectedTo;
    
    public void setSupporter(String supporter) {
        //TODO String from bundle
        lblConnectedTo.setText("Connected to " + supporter);
    }
    
    public void setExecutor(RequestSupportExecutor rse) {
        executor = rse;
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
    }

    @FXML
    private void onDisconnectAction(ActionEvent event) {
        //TODO execute disconnect
        //executor.disconnect();
        
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        stage.setScene(FXMLGuiLoader.getInstance().getRequestSupport());
    }
    
}
