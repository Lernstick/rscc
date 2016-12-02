/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.controller;

import ch.imedias.rscc.model.SupportAddress;
import ch.imedias.rscc.util.FXMLGuiLoader;
import ch.imedias.rscc.util.ProcessExecutorFactory;
import ch.imedias.rscc.util.RequestSupportExecutor;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.stage.Stage;

/**
 * FXML Controller class for the RequestSupport view
 *
 * @author sschw
 */
public class RequestSupportController implements Initializable {
    
    /**
     * Available imagescales in the GUI.
     */
    private final static double[] IMAGESCALES = { 0.5, 1, 2};
    
    @FXML
    private ComboBox<Double> cboImagescale;
    @FXML
    private ComboBox<SupportAddress> cboSupporter;
    
    private ResourceBundle bundle;
    
    /**
     * Executor which will be used to call backend operations.
     */
    private RequestSupportExecutor executor;

    /**
     * Initializes the controller class. <br>
     * <br>
     * Initializes the comboboxes with all values and sets a default value.
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        bundle = rb;
        
        for(double val : IMAGESCALES) {
            cboImagescale.getItems().add(val);
        }
        cboImagescale.getSelectionModel().select(1.0);
        
        cboSupporter.getItems().addAll(SupportAddress.getAll());
        cboSupporter.getSelectionModel().selectFirst();
    }

    /**
     * Action handler for back button.<br>
     * <br>
     * Switches GUI to {@link RemoteSupportStartController}.
     * @param event
     */
    @FXML
    private void onBackAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        
        stage.setScene(FXMLGuiLoader.getInstance().getRemoteSupportStart());
    }

    /**
     * Action handler for disconnect button.<br>
     * <ol>
     *  <li>Switches GUI to {@link RequestSupportConnectingController}.</li>
     *  <li>
     *      Initializes the {@link RequestSupportExecutor} if it isn't already initialized.<br>
     *      <small>
     *          Register callback methods {@link RequestSupportController#openConnected(javafx.stage.Stage) openConnected} 
     *          and {@link RequestSupportController#openConnectedFailed(javafx.stage.Stage) openConnectedFailed}.</small>
     * </li>
     *  <li>Calls {@link RequestSupportExecutor#connect(ch.imedias.rscc.model.SupportAddress, java.lang.Double) }.</li>
     * </ol>
     * 
     * @param event
     */
    @FXML
    private void onConnectAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        
        SupportAddress supportAddress = cboSupporter.getValue();
        Double scale = cboImagescale.getValue();
        
        stage.setScene(FXMLGuiLoader.getInstance().getRequestSupportConnecting(supportAddress.getDescription()));
        
        // Initialize connecter giving the current stage
        if(executor == null)
            executor = new RequestSupportExecutor(new ProcessExecutorFactory(), () -> openConnected(stage), () -> openConnectedFailed(stage));
        // Start executor to connect
        executor.connect(supportAddress, scale);
    }

    /**
     * Callback method for {@link RequestSupportExecutor} if if client successfully connected to supporter.
     * @param stage window that needs to be changed
     */
    private void openConnected(Stage stage) {
        stage.setScene(FXMLGuiLoader.getInstance().getRequestSupportConnected(cboSupporter.getValue().getDescription(), executor));
    }

    /**
     * Callback method for {@link RequestSupportExecutor} if connecting to supporter failed.
     * @param stage window that needs to be changed
     */
    private void openConnectedFailed(Stage stage) {
        stage.setScene(FXMLGuiLoader.getInstance().getRequestSupport());
        
        Scene errorDialog = FXMLGuiLoader.getInstance().getErrorDialog();
        FXMLGuiLoader.getInstance().createDialog(stage, errorDialog, bundle.getString("Error"), true).show();
    }

    /**
     * Action handler for edit supporter link<br>
     * <br>
     * Opens dialog for {@link EditDialogController} using {@link FXMLGuiLoader#createDialog(javafx.stage.Stage, javafx.scene.Scene, java.lang.String, boolean) } 
     * and reloads data of this controller.
     * @param event 
     */
    @FXML
    private void onEditSupporterlistAction(ActionEvent event) {
        Scene scene = ((Node)(event.getSource())).getScene();
        Stage stage = (Stage)scene.getWindow();
        
        Scene editDialog = FXMLGuiLoader.getInstance().getEditDialog();
        FXMLGuiLoader.getInstance().createDialog(stage, editDialog, bundle.getString("EditDialog.title"), true).showAndWait();
        
        // Reload supporter list after dialog while keeping current selection
        SupportAddress sa = cboSupporter.getValue();
        cboSupporter.getItems().clear();
        cboSupporter.getItems().addAll(SupportAddress.getAll());
        cboSupporter.getSelectionModel().select(sa);
        if(cboSupporter.getSelectionModel().isEmpty())
            cboSupporter.getSelectionModel().selectFirst();
    }

    /**
     * Finalize the GUI. <br>
     * <br>
     * Calls {@link RequestSupportExecutor#exit() }
     */
    public void finalizeGui() {
        if(executor != null) {
            executor.exit();
            executor = null;
        }
    }
}
