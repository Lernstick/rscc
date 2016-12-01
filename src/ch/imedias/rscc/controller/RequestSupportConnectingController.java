/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import java.net.URL;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class
 *
 * @author user
 */
public class RequestSupportConnectingController implements Initializable {

    @FXML
    private Label lblConnectingTo;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public void setSupporter(String supporter) {
        //TODO get string from bundle
        lblConnectingTo.setText("Connecting to " + supporter + "...");
    }
    
}
