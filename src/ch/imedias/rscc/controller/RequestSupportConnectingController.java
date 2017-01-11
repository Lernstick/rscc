/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

/**
 * FXML Controller class for the GUI when a client tries to connect with a
 * supporter.
 *
 * @author sschw
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
    
    /**
     * Change the text of the ConnectingTo label.<br>
     * <br>
     * Uses the already defined {@link ResourceBundle} text, which was defined
     * in the FXML and adds the supporter name by using 
     * {@link MessageFormat#format(java.lang.String, java.lang.Object...) }.
     * 
     * @param supporter the description text of the supporter.
     */
    public void setSupporter(String supporter) {
        String t = MessageFormat.format(lblConnectingTo.getText(), supporter);
        lblConnectingTo.setText(t);
    }
    
}
