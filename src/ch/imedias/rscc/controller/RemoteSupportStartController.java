/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 * FXML Controller class
 *
 * @author user
 */
public class RemoteSupportStartController implements Initializable {
    private Parent remoteObtain;
    private Parent remoteOffer;

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
//        try {
//            m_remoteObtain = FXMLLoader.load(getClass().getResource("RemoteObtain.fxml"), rb);
//            m_remoteOffer = FXMLLoader.load(getClass().getResource("RemoteOffer.fxml"), rb);
//        } catch(IOException ex) {
//            ex.printStackTrace();
//        }
    }    

    @FXML
    private void openRemoteObtain(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        
        scene.setRoot(remoteObtain);
    }

    @FXML
    private void openRemoteOffer(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        
        scene.setRoot(remoteOffer);
    }
    
}
