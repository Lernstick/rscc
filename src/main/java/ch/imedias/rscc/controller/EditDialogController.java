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

import ch.imedias.rscc.model.SupportAddress;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.util.Callback;

/**
 * FXML Controller class for EditDialog
 * 
 * This Controller class uses the Model class SupportAddress 
 * 
 */
public class EditDialogController implements Initializable {

    @FXML
    private Button cancelButton;
    @FXML
    private Button saveButton;
    @FXML
    private Button addButton;
    @FXML
    private Button deleteButton;
    @FXML
    private Button upButton;
    @FXML
    private Button downButton;
    @FXML
    private Button resetButton;
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
     * @param url The URL used to resolve relative paths for the root 
     *            object, or null if the location is not known 
     * @param rb  The resources used to localize the root object, or null if the
     *            root object was not localized
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Initialize supporter address lists (default and actual)
        staticAddressList = SupportAddress.getAll();
        SupportAddress.resetAllToDefault();
        staticDefaultAddressList = SupportAddress.getAll();
        SupportAddress.setAll(staticAddressList);
          
        table.setEditable(true);
        
        // Initialize columns
        Callback<TableColumn<SupportAddress, String>, TableCell<SupportAddress, String>> dexcriptionCellFactory = (TableColumn<SupportAddress, String> p) -> new DescriptionCell();      
        name.setCellValueFactory(new PropertyValueFactory<>("description"));
        name.setCellFactory(dexcriptionCellFactory);
        name.setOnEditCommit((CellEditEvent<SupportAddress, String> t) -> {
            ((SupportAddress) t.getTableView().getItems().get(t.getTablePosition().getRow())).setDescription(t.getNewValue());
        });

        Callback<TableColumn<SupportAddress, String>, TableCell<SupportAddress, String>> addressCellFactory = (TableColumn<SupportAddress, String> p) -> new AddressCell();
        address.setCellValueFactory(new PropertyValueFactory<>("address"));
        address.setCellFactory(addressCellFactory);
        address.setOnEditCommit((CellEditEvent<SupportAddress, String> t) -> {
            ((SupportAddress) t.getTableView().getItems().get(t.getTablePosition().getRow())).setAddress(t.getNewValue());
        });
        
        encrypted.setCellValueFactory((CellDataFeatures<SupportAddress, Boolean> param) -> {
            final SupportAddress sa = param.getValue();
            SimpleBooleanProperty booleanProp = new SimpleBooleanProperty(sa.isEncrypted());
            booleanProp.addListener((observable, oldValue, newValue) -> sa.setEncrypted(newValue));
            return booleanProp;
        });
        
        // Adds Checkbox in table cell
        encrypted.setCellFactory(new Callback<TableColumn<SupportAddress, Boolean>,
        TableCell<SupportAddress, Boolean>>() {
            @Override
            public TableCell<SupportAddress, Boolean> call(TableColumn<SupportAddress, Boolean> p) {
                CheckBoxTableCell<SupportAddress, Boolean> cell = new CheckBoxTableCell<>();
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
            @Override
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
        upButton.setDisable(false);
        downButton.setDisable(false);
        if (supportAddresses.size() >= 2) {
            for (SupportAddress s : selectedItems) {
                int index = supportAddresses.indexOf(s);
                if (index <= 0) {
                    upButton.setDisable(true);
                } else if (index >= (supportAddresses.size()-1)) {
                    downButton.setDisable(true);
                }
            }
        } else {
            upButton.setDisable(true);
            downButton.setDisable(true);
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
        staticAddressList = new ArrayList<>();
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
        table.getSelectionModel().clearSelection();
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
        List<Integer> changedIndexes = new LinkedList<>();
        ObservableList<SupportAddress> selectedItems = table.getSelectionModel().getSelectedItems();
        List<SupportAddress> objects = new LinkedList<>();        
        for (SupportAddress sa : selectedItems) {
            objects.add(supportAddresses.get(supportAddresses.indexOf(sa)));
        }
        for (SupportAddress sa : objects) {
            int indexPrevious = supportAddresses.indexOf(sa)-1;
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
        List<Integer> changedIndexes = new LinkedList<>();
        ObservableList<SupportAddress> selectedItems = table.getSelectionModel().getSelectedItems();
        List<SupportAddress> objects = new LinkedList<>();
        for (int i = selectedItems.size()-1; i >= 0; i--) {
            objects.add(supportAddresses.get(supportAddresses.indexOf(selectedItems.get(i))));
        }
        for (SupportAddress sa : objects) {
            int indexNext = supportAddresses.indexOf(sa)+1;
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
     * This Class is used to edit new added SupportAddress entries in table
     * and handles the input and editing events
     */
    class EditingCell extends TableCell<SupportAddress, String> {
        protected TextField textField;

        /**
         * Default constructor
         */
        public EditingCell() {
        }

        /**
         * This method adds the TextField and enables editing
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
         * This method handles the cancellation of the editing process
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
         * This method creates the TextField and handles the editing of the TextField
         */
        protected void createTextField() {
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
                table.getSelectionModel().getSelectedItem().setDescription(getString());
            });
        }

        /**
         * This method is the getter of the text entry of the item
         * @return string property of item
         */
        protected String getString() {
            return getItem() == null ? "" : getItem();
        }
    }
    
    class DescriptionCell extends EditingCell {
        /**
         * This method creates the TextField and handles the editing of the TextField
         */
        @Override
        protected void createTextField() {
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
                table.getSelectionModel().getSelectedItem().setDescription(getString());
            });
        }
    }
    
    class AddressCell extends EditingCell {
        /**
         * This method creates the TextField and handles the editing of the TextField
         */
        @Override
        protected void createTextField() {
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
                table.getSelectionModel().getSelectedItem().setAddress(getString());
            });
        }
    }
}
