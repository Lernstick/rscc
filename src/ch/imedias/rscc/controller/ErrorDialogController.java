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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author user
 */
public class ErrorDialogController implements Initializable {

    @FXML
    private Label lblMessage;
    @FXML
    private Label lblTitle;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }
    
    public void setTitle(String title) {
        lblTitle.setText(title);
    }
    
    public void setMessage(String message) {
        lblMessage.setText(message);
    }

    @FXML
    private void onOkAction(ActionEvent event) {
        // Close the stage
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
    
}
