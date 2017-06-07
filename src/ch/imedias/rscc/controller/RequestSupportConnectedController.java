/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import ch.imedias.rscc.util.FXMLGuiLoader;
import ch.imedias.rscc.util.RequestSupportExecutor;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

/**
 * FXML Controller class for the GUI when a client etablished a connection 
 * with a supporter.
 *
 * @author sschw
 */
public class RequestSupportConnectedController implements Initializable {

    /**
     * Executor which will be used to call backend operations.
     */
    private RequestSupportExecutor executor;
    
    @FXML
    private Label lblConnectedTo;
    
    /**
     * Change the text of the ConnectedTo label.<br>
     * <br>
     * Uses the already defined {@link ResourceBundle} text, which was defined
     * in the FXML and adds the supporter name by using 
     * {@link MessageFormat#format(java.lang.String, java.lang.Object...) }.
     * 
     * @param supporter the description text of the supporter.
     */
    public void setSupporter(String supporter) {
        String t = MessageFormat.format(lblConnectedTo.getText(), supporter);
        lblConnectedTo.setText(t);
    }
    
    /**
     * Sets the executor so it uses the same executor as the connection 
     * operation needed.
     * 
     * @param rse the RequestSupportExecutor which already ran {@link RequestSupportExecutor#connect(ch.imedias.rscc.model.SupportAddress, java.lang.Double) } successfully
     */
    public void setExecutor(RequestSupportExecutor rse) {
        executor = rse;
    }

    /**
     * Initializes the controller class.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) { }

    /**
     * Action handler for disconnect button.<br>
     * <br>
     * Calls {@link RequestSupportExecutor#disconnect()} and switches GUI to
     * {@link RequestSupportController}.
     * @param event
     */
    @FXML
    private void onDisconnectAction(ActionEvent event) {
        executor.disconnect();
        
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        stage.setScene(FXMLGuiLoader.getInstance().getRequestSupport());
    }
    
}
