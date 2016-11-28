/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc;

import java.net.URL;
import java.util.ArrayList;
import static java.util.Collections.swap;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.logging.Logger;
import java.util.Map;
import javafx.scene.Node;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

/**
 * FXML Controller class
 *
 * @author user
 */
public class EditDialogController implements Initializable {

    @FXML
    private Button CancelButton;
    @FXML
    private Button SaveButton;
    @FXML
    private Button AddButton;
    @FXML
    private Button DeleteButton;
    @FXML
    private Button UpButton;
    @FXML
    private Button DownButton;
    @FXML
    private Button ResetButton;
    
     private final static ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "ch/imedias/rscc/Bundle");
    private final static Logger LOGGER
            = Logger.getLogger(EditDialog.class.getName());
    
    private final SupportAddressesTableModel tableModel;
    
    private ObservableList<SupportAddress> supportAddresses;
    private boolean okPressed;
    private SelectionModel<SupportAddress> selectionModel;
    @FXML
    private TableView<SupportAddress> table;
    @FXML
    private TableColumn<Map, String> name;
    @FXML
    private TableColumn<Map, String> address;
    @FXML
    private TableColumn<Map, Boolean> encrypted;
    
    

    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {

        // make a deep copy of the list
        this.supportAddresses = FXCollections.observableArrayList();
        for (SupportAddress supportAddress : staticAddressList) {
            if (supportAddress != null) {
            SupportAddress addressCopy = new SupportAddress(
                    supportAddress.getDescription(),
                    supportAddress.getAddress(),
                    supportAddress.isEncrypted());
            this.supportAddresses.add(addressCopy);
            }
        }
        this.selectionModel = table.getSelectionModel();
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        tableModel = new SupportAddressesTableModel();
        //initComponents();
        table.setModel(tableModel);
        
        
        table.itemsProperty().setValue(supportAddresses);
       // table.setEditable(true);
        //table.
        // determine size and location
        //adjustColumnPreferredWidths(table);
        //pack();
        //setLocationRelativeTo(parent);
    }    

    @FXML
    private void cancel(MouseEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    private void save(MouseEvent event) {
        staticAddressList = new ArrayList<SupportAddress>();
        for (SupportAddress supportAddress : supportAddresses) {
            if (supportAddress != null) {
                SupportAddress addressCopy = new SupportAddress(
                        supportAddress.getDescription(),
                        supportAddress.getAddress(),
                        supportAddress.isEncrypted());
                staticAddressList.add(addressCopy);
            }
        }
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    @FXML
    private void add(MouseEvent event) {
        SupportAddress addressNew = new SupportAddress("","", false);
        supportAddresses.add(addressNew);
    }

    @FXML
    private void delete(MouseEvent event) {
        ObservableList<SupportAddress> temp = table.getSelectionModel().getSelectedItems();
        supportAddresses.removeAll(temp);
    }

    @FXML
    private void up(MouseEvent event) {
        selectionModel = table.getSelectionModel();
        SupportAddress temp = selectionModel.selectedItemProperty().getValue();
        int index = supportAddresses.indexOf(temp);
        swap(supportAddresses, index, index + 1);
    }

    @FXML
    private void down(MouseEvent event) {
        selectionModel = table.getSelectionModel();
        SupportAddress temp = selectionModel.selectedItemProperty().getValue();
        int index = supportAddresses.indexOf(temp);
        swap(supportAddresses, index, index - 1);
    }

    @FXML
    private void reset(MouseEvent event) {
        this.supportAddresses = FXCollections.observableArrayList();
        for (SupportAddress supportAddress : staticDefaultAddressList) {
            if (supportAddress != null) {
            SupportAddress addressCopy = new SupportAddress(
                    supportAddress.getDescription(),
                    supportAddress.getAddress(),
                    supportAddress.isEncrypted());
            this.supportAddresses.add(addressCopy);
            }
        }
    }

    @FXML
    private void selectAddress(MouseEvent event) {

    }
    
}
