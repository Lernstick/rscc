/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import ch.imedias.rscc.model.SupportAddress;
import ch.imedias.rscc.util.FXMLGuiLoader;
import ch.imedias.rscc.util.RemoteSupportExecutor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * FXML Controller class
 *
 * @author user
 */
public class RequestSupportController implements Initializable {

    @FXML
    private ComboBox<Double> cbx_imagescale;
    @FXML
    private ComboBox<SupportAddress> cbx_supporter;
    
    private final static double[] IMAGESCALES = { 0.5, 1, 2};

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        for(double val : IMAGESCALES) {
            cbx_imagescale.getItems().add(val);
        }
        cbx_imagescale.setValue(1.0);
        
    }

    @FXML
    private void onBackAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        
        stage.setScene(FXMLGuiLoader.getInstance().getRemoteSupportStart());
    }

    @FXML
    private void onConnectAction(ActionEvent event) {
        SupportAddress supportAddress = cbx_supporter.getValue();
        Double scale = cbx_imagescale.getValue();
        
        RemoteSupportExecutor.connect(supportAddress, scale);
        
        // TODO onDone frame.setTitle((BUNDLE.getString("RemoteSupportFrame.title"));
        // TODO showPanel(getSeekSupportPanel(), "mainPanel");
        // TODO frame.setExtendedState(Frame.NORMAL);
    }

    @FXML
    private void onEditSupporterlistAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        Stage dialog = new Stage();
        
        dialog.initOwner(stage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.setScene(FXMLGuiLoader.getInstance().getEditDialog());
        dialog.showAndWait();
    }
    
}
