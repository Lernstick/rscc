/*
 * This file contains the implementation of the FXML Controller class of the EditDialog view
 *
 * This class is manualy tested by the systemtest "Edit supporter list"
 * @see "https://www.cs.technik.fhnw.ch/confluence16/display/VTDESGA/Test+Plan+for+Lernstick_1"
 *
 * @author Jan Hitz, Line Stettler
 *
 *
 */
package ch.imedias.rscc.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import static java.util.Collections.list;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import ch.imedias.rscc.model.SupportAddress;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionModel;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

/**
 * FXML Controller class for EditDialog
 * 
 * This Controller class uses the Model class SupportAddress 
 * 
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
    @FXML
    private TableView<SupportAddress> table;
    @FXML
    private TableColumn<SupportAddress, String> name;
    @FXML
    private TableColumn<SupportAddress, String> address;
    @FXML
    private TableColumn<SupportAddress, Boolean> encrypted;
    
    // Not in use  
    private final static ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "ch/imedias/rscc/Bundle");
    private final static Logger LOGGER
            = Logger.getLogger(EditDialogController.class.getName());
    
    
    private ObservableList<SupportAddress> supportAddresses;
    
    private List<SupportAddress> staticDefaultAddressList;
    private List<SupportAddress> staticAddressList;

    
    /**
     * Initializes the controller class.
     * 
     * @parm url  The url used to resolve relative paths for the root 
     *            object, or null if the location is not known 
     * @param rb  The resources used to localize the root object, or null if the
     *            root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize supporter address lists (default and actual)
        staticAddressList = SupportAddress.getAll();
        staticDefaultAddressList = SupportAddress.getAll();
          
        table.setEditable(true);
        
        // Initialize columns
        Callback<TableColumn<SupportAddress, String>, TableCell<SupportAddress, String>> cellFactory = (TableColumn<SupportAddress, String> p) -> new EditingCell();
        
        name.setCellValueFactory(new PropertyValueFactory<SupportAddress, String>("description"));
        name.setCellFactory(cellFactory);
        name.setOnEditCommit((CellEditEvent<SupportAddress, String> t) -> {
            ((SupportAddress) t.getTableView().getItems().get(t.getTablePosition().getRow())).setDescription(t.getNewValue());
        });

        address.setCellValueFactory(new PropertyValueFactory<SupportAddress, String>("address"));
        address.setCellFactory(cellFactory);
        address.setOnEditCommit((CellEditEvent<SupportAddress, String> t) -> {
            ((SupportAddress) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAddress(t.getNewValue());
        });
        
        encrypted.setCellValueFactory(new Callback<CellDataFeatures<SupportAddress, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<SupportAddress, Boolean> param) {
                final SupportAddress sa = param.getValue();
                SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(sa.isEncrypted());
                booleanProp.addListener(new ChangeListener<Boolean>() {
                    @Override
                    public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                        sa.setEncrypted(newValue);
                    }
                });
                return booleanProp;
            }
        });
        
        // Adds Checkbox in table cell
        encrypted.setCellFactory(new Callback<TableColumn<SupportAddress, Boolean>,
        TableCell<SupportAddress, Boolean>>() {
            @Override
            public TableCell<SupportAddress, Boolean> call(TableColumn<SupportAddress, Boolean> p) {
                CheckBoxTableCell<SupportAddress, Boolean> cell = new CheckBoxTableCell<SupportAddress, Boolean>();
                cell.setAlignment(Pos.CENTER);
                return cell;
            }
        });
        encrypted.setEditable(true);

        table.getColumns().setAll(name, address, encrypted);

        // Make a deep copy of the list
        if(staticAddressList != null)
        {
            this.supportAddresses = FXCollections.observableArrayList(staticAddressList);
        } else {
            this.supportAddresses = FXCollections.observableArrayList(staticDefaultAddressList);
        }
       
        // Enable to select mulitple table entries
        table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        table.itemsProperty().setValue(supportAddresses);
        
        ObservableList<SupportAddress> selectedItems = table.getSelectionModel().getSelectedItems();
        selectedItems.addListener(new ListChangeListener<SupportAddress>() {
            public void onChanged(Change c) {
                manageUpDownButtons();
            }
        });
    }
    
    /**
     * @brief This function disables the up/down buttons if an entry can not 
     *        been moved any further up/down
     */
    private void manageUpDownButtons() {
        ObservableList<SupportAddress> selectedItems = table.getSelectionModel().getSelectedItems();
        UpButton.setDisable(false);
        DownButton.setDisable(false);
        if (supportAddresses.size() >= 2) {
            for (SupportAddress s : selectedItems) {
                int index = supportAddresses.indexOf(s);
                if (index <= 0) {
                    UpButton.setDisable(true);
                } else if (index >= (supportAddresses.size()-1)) {
                    DownButton.setDisable(true);
                }
            }
        } else {
            UpButton.setDisable(true);
            DownButton.setDisable(true);
        }
    }

    /**
     * @param event MouseClick event if cancel button is clicked
     */
    @FXML
    private void onCancelClickedAction(MouseEvent event) {
        ((Node)(event.getSource())).getScene().getWindow().hide();
    }

    /**
     * @param event MouseClick event if save button is clicked
     */
    @FXML
    private void onSaveClickedAction(MouseEvent event) {
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
        SupportAddress.setAll(staticAddressList);
        SupportAddress.saveAll();
    }

    /**
     * @param event MouseClick event if add button is clicked
     */
    @FXML
    private void onAddClickedAction(MouseEvent event) {
        SupportAddress addressNew = new SupportAddress("","", false);
        supportAddresses.add(addressNew);
        table.getSelectionModel().select(addressNew);
        manageUpDownButtons();
    }
    /**
     * @param event MouseClick event if delete button is clicked
     */
    @FXML
    private void onDeleteClickedAction(MouseEvent event) {
        ObservableList<SupportAddress> temp = table.getSelectionModel().getSelectedItems();
        supportAddresses.removeAll(temp);
        manageUpDownButtons();
    }

    /**
     * @param event MouseClick event if up button is clicked
     */
    @FXML
    private void onUpClickedAction(MouseEvent event) {
        List<Integer> changedIndexes = new LinkedList<Integer>();
        ObservableList<SupportAddress> selectedItems = table.getSelectionModel().getSelectedItems();
        List<SupportAddress> objects = new LinkedList<SupportAddress>();        
        for (SupportAddress sa : selectedItems) {
            objects.add(supportAddresses.get(supportAddresses.indexOf(sa)));
        }
        for (SupportAddress sa : objects) {
            int indexPrevious = supportAddresses.indexOf(sa)-1;
            SupportAddress previous = supportAddresses.get(indexPrevious);
            int indexSelected = indexPrevious+1;
            SupportAddress selected = supportAddresses.get(supportAddresses.indexOf(sa));
            supportAddresses.remove(selected);
            supportAddresses.add(indexPrevious, selected);
            changedIndexes.add(indexPrevious);
        }
        table.getSelectionModel().clearSelection();
        for (Integer i: changedIndexes) {
            table.getSelectionModel().select(i);
        }
    }
    
    /**
     * @param event MouseClick event if down button is clicked
     */
    @FXML
    private void onDownClickedAction(MouseEvent event) {
        List<Integer> changedIndexes = new LinkedList<Integer>();
        ObservableList<SupportAddress> selectedItems = table.getSelectionModel().getSelectedItems();
        List<SupportAddress> objects = new LinkedList<SupportAddress>();
        for (int i = selectedItems.size()-1; i >= 0; i--) {
            objects.add(supportAddresses.get(supportAddresses.indexOf(selectedItems.get(i))));
        }
        for (SupportAddress sa : objects) {
            int indexNext = supportAddresses.indexOf(sa)+1;
            SupportAddress next = supportAddresses.get(indexNext);
            int indexSelected = indexNext-1;
            SupportAddress selected = supportAddresses.get(supportAddresses.indexOf(sa));
            supportAddresses.remove(selected);
            supportAddresses.add(indexNext, selected);
            changedIndexes.add(indexNext);
        }
        table.getSelectionModel().clearSelection();
        for (Integer i: changedIndexes) {
            table.getSelectionModel().select(i);
        }
    }

    /**
     * @param event MouseClick event if reset button is clicked
     */
    @FXML
    private void onResetClickedAction(MouseEvent event) {
    this.supportAddresses.clear();
        for (SupportAddress sa : staticDefaultAddressList) {
            this.supportAddresses.add(sa);
        }
    }
    
    /**
     * This Class is used to eddit new added SupportAddress entries in table
     * and handles the input and editing events
     */
    class EditingCell extends TableCell<SupportAddress, String> {
        private TextField textField;

        /**
         * Default constructor
         */
        public EditingCell() {
        }

        /**
         * This method adds the textfield and enebles editing
         */
        @Override
        public void startEdit() {
            if (!isEmpty()) {
                super.startEdit();
                createTextField();
                setText(null);
                setGraphic(textField);
                textField.selectAll();
            }
        }

        /**
         * This method handles the cancelation of the eding process
         */
        @Override
        public void cancelEdit() {
            super.cancelEdit();

            setText((String) getItem());
            setGraphic(null);
        }

        /**
         * This method updates the updating of the list when an entry is added and edited
         * @param item
         * @param empty 
         */
        @Override
        public void updateItem(String item, boolean empty) {
            super.updateItem(item, empty);

            if (empty) {
                setText(null);
                setGraphic(null);
            } else {
                if (isEditing()) {
                    if (textField != null) {
                        textField.setText(getString());
                    }
                    setText(null);
                    setGraphic(textField);
                } else {
                    setText(getString());
                    setGraphic(null);
                }
            }
        }

        /**
         * This method creates the textfield and handles the eding of the textfield
         */
        private void createTextField() {
            textField = new TextField(getString());
            textField.setMinWidth(this.getWidth() - this.getGraphicTextGap() * 2);
            textField.focusedProperty().addListener(
                    (ObservableValue<? extends Boolean> arg0, Boolean arg1, Boolean arg2) -> {
                        if (!arg2) {
                            commitEdit(textField.getText());
                        }
                    });
            textField.textProperty().addListener((observable, oldValue, newValue)->{
                updateItem(newValue, false);
            });
        }

        /**
         * This method is the getter of the text entry of the item
         * @return string property of item
         */
        private String getString() {
            return getItem() == null ? "" : getItem().toString();
        }
    }
}
