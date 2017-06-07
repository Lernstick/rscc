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
import javafx.stage.Stage;

/**
 * FXML Controller class for the Error Dialog.<br>
 * <br>
 * JavaFX does not have a standard error dialog.
 *
 * @author sschw
 */
public class ErrorDialogController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) { }

    @FXML
    private void onOkAction(ActionEvent event) {
        // Close the stage
        ((Stage)((Node)event.getSource()).getScene().getWindow()).close();
    }
    
}
