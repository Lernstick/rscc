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
import javafx.stage.Stage;

/**
 * FXML Controller class for the startup screen.
 *
 * @author sschw
 */
public class RemoteSupportStartController implements Initializable {

    @Override
    public void initialize(URL url, ResourceBundle rb) {
    }    

    /**
     * Open other view by clicking on request support button
     * @param event 
     */
    @FXML
    private void onRequestSupportAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        
        stage.setScene(FXMLGuiLoader.getInstance().getRequestSupport());
    }

    /**
     * Open other view by clicking on provide support button
     * @param event 
     */
    @FXML
    private void onProvideSupportAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        
        stage.setScene(FXMLGuiLoader.getInstance().getProvideSupport());
    }

}
