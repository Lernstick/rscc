/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author sschw
 */
public class RequestSupportConnectedController implements Initializable {

    private ResourceBundle bundle;
    
    @FXML
    private Label lblConnectedTo;
    
    public void setSupporter(String supporter) {
        lblConnectedTo.setText(bundle.getString("ConnectedTo") + supporter);
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
    }
    
}
