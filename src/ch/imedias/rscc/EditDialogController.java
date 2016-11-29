/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc;

import java.net.URL;
import java.util.ArrayList;
import static java.util.Collections.swap;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
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
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn.CellDataFeatures;
import javafx.scene.control.TableColumn.CellEditEvent;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.util.Callback;
import javafx.util.StringConverter;

/**
 * FXML Controller class
 *
 * @author user
 */
public class EditDialogController extends Application implements Initializable {

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
    
    //private final SupportAddressesTableModel tableModel;
    
    private ObservableList<SupportAddress> supportAddresses;
    private boolean okPressed;
    private SelectionModel<SupportAddress> selectionModel;
    @FXML
    private TableView<SupportAddress> table;
    @FXML
    private TableColumn<SupportAddress, String> name;
    @FXML
    private TableColumn<SupportAddress, String> address;
    @FXML
    private TableColumn<SupportAddress, Boolean> encrypted;
    
    
    private List<SupportAddress> staticDefaultAddressList;
    private List<SupportAddress> staticAddressList;

    
    /**
     * Initializes the controller class.
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        System.out.println("ch.imedias.rscc.EditDialogController.initialize()");
        staticAddressList = new ArrayList();
        staticDefaultAddressList = new ArrayList();

        
        staticAddressList.add(new SupportAddress("Hallo", "hallo.hallo", false));
        staticAddressList.add(new SupportAddress("Fritz", "fritz.hallo", true));
        staticAddressList.add(new SupportAddress("Meier", "Meier.hallo", false));

        staticDefaultAddressList.add(new SupportAddress("eins", "eins.hallo", false));
        staticDefaultAddressList.add(new SupportAddress("zwei", "zwei.hallo", true));
        staticDefaultAddressList.add(new SupportAddress("drei", "drei.hallo", false));
        
        table.setEditable(true);
        
        name.setCellValueFactory(new PropertyValueFactory<SupportAddress, String>("description"));
        name.setCellFactory(TextFieldTableCell.<SupportAddress, String>forTableColumn(new StringConverter<String>() {
            @Override
            public String toString(String t) {
                return t;
            }

            @Override
            public String fromString(String string) {
               return string; 
            }
        }));
        name.setOnEditCommit(
            new EventHandler<CellEditEvent<SupportAddress, String>>() {
                @Override
                public void handle(CellEditEvent<SupportAddress, String> t) {
                    ((SupportAddress) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setDescription(t.getNewValue());
                }
            }
        );

        address.setCellValueFactory(new PropertyValueFactory<SupportAddress, String>("address"));
        address.setCellFactory(TextFieldTableCell.<SupportAddress, String>forTableColumn(new StringConverter<String>() {
            @Override
            public String toString(String t) {
                return t;
            }

            @Override
            public String fromString(String string) {
               return string; 
            }
        }));
        address.setOnEditCommit(
            new EventHandler<CellEditEvent<SupportAddress, String>>() {
                @Override
                public void handle(CellEditEvent<SupportAddress, String> t) {
                    ((SupportAddress) t.getTableView().getItems().get(
                            t.getTablePosition().getRow())
                            ).setAddress(t.getNewValue());
                }
            }
        );
        
        encrypted.setCellValueFactory(new Callback<CellDataFeatures<SupportAddress, Boolean>, ObservableValue<Boolean>>() {
            @Override
            public ObservableValue<Boolean> call(CellDataFeatures<SupportAddress, Boolean> param) {
                SupportAddress sa = param.getValue();
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

        // make a deep copy of the list
       
        if(staticAddressList != null)
        {
             this.supportAddresses = FXCollections.observableArrayList(staticAddressList);
        }else{
            this.supportAddresses = FXCollections.observableArrayList(staticDefaultAddressList);
        }
        //this.selectionModel = table.getSelectionModel();
        //table.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        
        //tableModel = new SupportAddressesTableModel();
        //initComponents();
        //table.setModel(tableModel);
        
        
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
        swap(supportAddresses, index, index - 1);
    }

    @FXML
    private void down(MouseEvent event) {
        selectionModel = table.getSelectionModel();
        SupportAddress temp = selectionModel.selectedItemProperty().getValue();
        int index = supportAddresses.indexOf(temp);
        swap(supportAddresses, index, index + 1);
    }

    @FXML
    private void reset(MouseEvent event) {
        this.supportAddresses = FXCollections.observableArrayList(staticDefaultAddressList);
    }

    @FXML
    private void selectAddress(MouseEvent event) {

    }

    @Override
    public void start(Stage mainStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("editDialog.fxml"));
        Scene myScene = new Scene(root); 
        mainStage.setScene(myScene);
        mainStage.setMinWidth(300);
        mainStage.setMinHeight(300); 
        mainStage.show();
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
        /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
      /*  EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                RemoteSupportFrame frame = new RemoteSupportFrame();
                SEEK_PROCESS_EXECUTOR.addPropertyChangeListener(frame);
                frame.setVisible(true);
            }
        
        });*/
      launch(args);
    }
    
}
