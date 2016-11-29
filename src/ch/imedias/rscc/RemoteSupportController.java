/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc;

import ch.imedias.rscc.RemoteSupportFrame.MyComboboxModel;
import ch.imedias.rscc.model.SupportAddress;
import ch.imedias.rscc.utils.ProcessExecutor;
import java.awt.CardLayout;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.prefs.Preferences;
import java.util.regex.Pattern;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JSpinner;
import javax.swing.JTabbedPane;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;

/**
 *
 * @author user
 */
public class RemoteSupportController implements Initializable{
    
    private RemoteSupportFrame rsFrame;
    
    private List<SupportAddress> supportAddresses;
    private Preferences preferences;
    
    private final List<ProcessExecutor> TUNNEL_EXECUTORS =
            new ArrayList<ProcessExecutor>();
    private final ProcessExecutor SEEK_PROCESS_EXECUTOR =
            new ProcessExecutor();
    private final ProcessExecutor OFFER_PROCESS_EXECUTOR =
            new ProcessExecutor();
    private final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "ch/imedias/rscc/Bundle");
    
    public final String SUPPORT_ADDRESSES = "supportAddresses";
    private final String SECURE_PORTS = "securePorts";
    private final String COMPRESSION_LEVEL = "compressionLevel";
    private final String QUALITY = "quality";
    private final String BGR233 = "bgr233";
    
    private SupportAddress supportAddress;
    private Pattern okPlainPattern;
    private Pattern okSSLPattern;
    private Pattern failedPattern;
    
    private MyComboboBoxModel comboboxModel;
    
    @FXML
    SpinnerNumberModel scaleSpinnerModel;
    @FXML
    SpinnerNumberModel compressionSpinnerModel;
    @FXML
    SpinnerNumberModel qualitySpinnerModel;
    
    @FXML
    JSpinner compressionSpinner;
    @FXML
    JSpinner qualitySpinner;
    
    @FXML
    JPanel seekSupportPanel;
    @FXML
    JLabel connectedLabel;
    @FXML
    JLabel connectingLabel;
    
    @FXML
    JButton offerSupportButton;
    @FXML
    JButton connectButton;
    @FXML
    JButton disconnectButton;
    @FXML
    JButton quitButton;
    
    @FXML
    JCheckBox bgr233CheckBox;
    
    @FXML
    JTextField securePortsTextField;
    
    
    
    /* maybe not used here  */
    @FXML
    JPanel buttonPanel;
    @FXML
    JLabel compressionLabel;
    @FXML
    JComboBox comboBox;
    @FXML
    JPanel connectedPanel;
    @FXML
    JPanel connectingPanel;
    @FXML
    JButton editButton;
    @FXML
    JPanel mainPanel;
    @FXML
    JPanel offerSupportPanel;
    @FXML
    JPanel picturePropertiesPanel;
    @FXML
    JProgressBar progressBar;
    @FXML
    JLabel qualityLabel;
    @FXML
    JLabel scaleLabel;
    @FXML
    JSpinner scaleSpinner;
    @FXML
    JLabel securePortsLabel;
    @FXML
    JLabel supportAddressLabel;
    @FXML
    JTabbedPane tabbedPane;
    

    public RemoteSupportController(RemoteSupportFrame rsFrame){
        this.rsFrame = rsFrame;
    }    
    /**
     * Initializes the controller class.
     */
 /*   @Override
    public void initialize(URL url, ResourceBundle rb) {
        preferences = Preferences.userNodeForPackage(RemoteSupportFrame.class);
        
        initLogger();
        initRemoteSupportFrameDefualtAddresses();
        initComboBoxModel();
        addHandlers();
    }    */
    
  /*  private void initLogger(){
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL);
        Logger peLogger = Logger.getLogger(ProcessExecutor.class.getName());
        peLogger.setLevel(Level.ALL);
        peLogger.addHandler(consoleHandler);
        pe/Logger.setUseParentHandlers(false);
    }*/
    
 /*   private void initRemoteSupportFrameDefualtAddresses(){
        SupportAddress.makeSupportAddresses();
        supportAddresses = SupportAddress.getSupportAddresses();
        securePortsTextField.setText(preferences.get(SECURE_PORTS, null));
        compressionSpinnerModel.setValue((preferences.getInt(COMPRESSION_LEVEL, 6)));
        qualitySpinnerModel.setValue(preferences.getInt(QUALITY, 6));
        bgr233CheckBox.setSelected(preferences.getBoolean(BGR233, false));
        TODO maybe not used 
        rsFrame.setDefaultAddresses(supportAddresses); 
    }*/
    
    private void setDefaultAddresses(List<SupportAddress> defaultAddresses){
        this.supportAddresses = defaultAddresses;
    }
    
    private void initComboBoxModel(){
        comboboxModel = new MyComboboBoxModel();
        comboBox.setModel(comboboxModel);
        comboBox.setSelectedIndex((comboboxModel.getSize() > 0)? 0 : -1);
    }
    
    

    /**
     * Executed when clicking offerSupportButton
     * @param evt Action Event
     */
    /*
    public void onOfferSupportButtonAction(java.awt.event.ActionEvent evt){
        if (offerSupportButton.getActionCommand().equals("start")) {

            String securePortsText = securePortsTextField.getText();
         
            
            String[] securePortsStrings = securePortsText.split(",");
            List<Integer> securePorts = new ArrayList<Integer>();
            for (String securePortsString : securePortsStrings) {
                String trimmed = securePortsString.trim();
                if (trimmed.isEmpty()) {
                    continue;
                }
                try {
                    int securePort = Integer.parseInt(trimmed);
                    if ((securePort >= 0) && (securePort <= 65535)) {
                        securePorts.add(securePort);
                    } else {
                        showPortError(securePortsString);
                        return;
                    }
                } catch (NumberFormatException ex) {
                    showPortError(securePortsString);
                    return;
                }
            }
            
            SwingWorker viewerSwingWorker = new SwingWorker() {

                @Override
                protected Object doInBackground() throws Exception {
                    return onOfferActionDoInBackground();
                }

                @Override
                protected void done() {
                    onOfferActionDone();
                }
            };
            viewerSwingWorker.execute();

            // check that the pem file for stunnel is there
            final String pemFilePath = System.getProperty("user.home")
                    + "/.local/stunnel.pem";
            if (!securePorts.isEmpty()) {
                File pemFile = new File(pemFilePath);
                if (!pemFile.exists()) {
                    ProcessExecutor processExecutor = new ProcessExecutor();
                    processExecutor.executeProcess("openssl", "req", "-x509",
                            "-nodes", "-days", "36500", "-subj",
                            "/C=/ST=/L=/CN=tmp", "-newkey",
                            "rsa:1024",
                            "-keyout", pemFilePath, "-out", pemFilePath);
                }
            }

            for (final Integer securePort : securePorts) {
                SwingWorker tunnelSwingWorker = new SwingWorker() {

                    @Override
                    protected Object doInBackground() throws Exception {
                        ProcessExecutor tunnelExecutor = new ProcessExecutor();
                        tunnelExecutor.executeProcess(
                                "stunnel", "-f", "-P", "", "-p", pemFilePath,
                                "-d", securePort.toString(), "-r", "5500");
                        TUNNEL_EXECUTORS.add(tunnelExecutor);
                        return null;
                    }
                };
                tunnelSwingWorker.execute();
            }

            afterOfferActionPerformed();
           
        } else {
            stopOffer();
        }
    }
    */
    /**
     * Executed when clicking editButton
     * @param evt Action Event
     */
    @FXML
    private void onEditButtonAction(java.awt.event.ActionEvent evt) {                                           
        EditDialog editDialog = new EditDialog(rsFrame, supportAddresses);
        editDialog.setVisible(true);
        if (editDialog.okPressed()) {
            supportAddresses = editDialog.getSupportAddresses();
            comboboxModel.fireContentsChanged();
            if (comboboxModel.getSize() > 0) {
                comboBox.setSelectedIndex(0);
            } else {
                comboBox.setSelectedIndex(-1);
            }
        }
    } 
    

    /**
     * Executed when clicking connectButton
     * @param evt Action Event
     */
    /*
     private void onConnectButtonAction(java.awt.event.ActionEvent evt) {                                              
        int selectedIndex = comboBox.getSelectedIndex();
        if (selectedIndex == -1) {
            return;
        }
        supportAddress = supportAddresses.get(selectedIndex);
        final String address = supportAddress.getAddress();

        okPlainPattern = Pattern.compile(
                ".*reverse_connect: " + address + "/.* OK");
        okSSLPattern = Pattern.compile(".*created selwin:.*");
        failedPattern = Pattern.compile(
                ".*reverse_connect: " + address + " failed");

        SwingWorker swingWorker = new SwingWorker() {

            @Override
            protected Object doInBackground() throws Exception {
                List<String> commandList = new ArrayList<String>();
                commandList.add("x11vnc");
                commandList.add("-connect_or_exit");
                commandList.add(address);
                if (supportAddress.isEncrypted()) {
                    commandList.add("-ssl");
                    commandList.add("TMP");
                }
                String scaleString = scaleSpinnerModel.getNumber().toString();
                if (!scaleString.equals("1.0")) {
                    commandList.add("-scale");
                    commandList.add(scaleString);
                }
                scaleSpinnerModel.getNumber();
                String[] commandArray =
                        commandList.toArray(new String[commandList.size()]);
                SEEK_PROCESS_EXECUTOR.executeProcess(true, true, commandArray);
                return null;
            }

            @Override
            protected void done() {
                rsFrame.setTitle(BUNDLE.getString("RemoteSupportFrame.title"));
                showPanel(rsFrame.getSeekSuportPanel(), "mainPanel");
                rsFrame.setExtendedState(Frame.NORMAL);
            }
        };
        swingWorker.execute();
        String connectMessage = BUNDLE.getString("Connecting_To");
        connectMessage = MessageFormat.format(
                connectMessage, supportAddress.getDescription());
        connectingLabel.setText(connectMessage);
        showPanel(seekSupportPanel, "connectingPanel");
    }
   */

    /**
     * Executed when clicking quitButton
     * @param evt Action Event
     */
    @FXML
     private void onQuitButtonAction(java.awt.event.ActionEvent evt) {                                           
       exit();
    }                                          

 /*   private void exit(){
    // save preferences
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(byteArrayOutputStream);
        encoder.setPersistenceDelegate(SupportAddress.class,
                SupportAddress.getPersistenceDelegate());
        encoder.writeObject(supportAddresses);
        encoder.close();
        String supportAddressesXML = byteArrayOutputStream.toString();
        preferences.put(SUPPORT_ADDRESSES, supportAddressesXML);
        preferences.put(SECURE_PORTS, securePortsTextField.getText());
        preferences.putInt(COMPRESSION_LEVEL,
                compressionSpinnerModel.getNumber().intValue());
        preferences.putInt(QUALITY,
                qualitySpinnerModel.getNumber().intValue());
        preferences.putBoolean(BGR233, bgr233CheckBox.isSelected());

        // stop all external processes
        disconnect();
        stopOffer();

        System.exit(0);
    }
*/
    /**
     * Executed when clicking disconnectButton
     * @param evt Action Event
     */
    @FXML
    private void onDisconnectButtonAction(java.awt.event.ActionEvent evt) {                                                 
        disconnect();
    }   

    /**
     * added as WindowListener
     * @param evt Action Event
     */
    @FXML
    private void formWindowClosing(java.awt.event.WindowEvent evt) {                                   
        exit();
    } 
    
   /* private void disconnect() {
        SEEK_PROCESS_EXECUTOR.destroy();
    }*/

  /*  private void stopOffer() {
        OFFER_PROCESS_EXECUTOR.destroy();
        for (ProcessExecutor tunnelExecutor : TUNNEL_EXECUTORS) {
            tunnelExecutor.destroy();
        }
        ProcessExecutor processExecutor = new ProcessExecutor();
        processExecutor.executeProcess("killall", "-9", "stunnel4");
    }*/
    /*
    public void onPropertyChangeAction(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ProcessExecutor.LINE)) {
            String line = (String) evt.getNewValue();
            if (failedPattern.matcher(line).matches()) {
                showPanel(seekSupportPanel, "mainPanel");
                JOptionPane.showMessageDialog(rsFrame,
                        BUNDLE.getString("Connection_Failed"),
                        BUNDLE.getString("Error"),
                        JOptionPane.ERROR_MESSAGE);
            } else if (okPlainPattern.matcher(line).matches()
                    || okSSLPattern.matcher(line).matches()) {
                String connectedMessage = BUNDLE.getString("Connected_To");
                connectedMessage = MessageFormat.format(
                        connectedMessage, supportAddress.getDescription());
                connectingLabel.setText(connectedMessage);
                rsFrame.setTitle(connectedMessage);
                showPanel(seekSupportPanel, "connectedPanel");
                new SwingWorker(){

                    @Override
                    protected Object doInBackground() throws Exception {
                        Thread.sleep(1000);
                        return null;
                    }

                    @Override
                    protected void done() {
                        rsFrame.setExtendedState(Frame.ICONIFIED);
                    }
                }.execute();
            }
        }
    }
*/
    /**
     * executed in the background while executing onOfferSupportButtonAction
     * @param evt Action Event
     */
   /* public Object onOfferActionDoInBackground(){
        Number compression = compressionSpinnerModel.getNumber();
        Number quality = qualitySpinnerModel.getNumber();
        List<String> commandList = new ArrayList<String>();
        commandList.add("xtightvncviewer");
        commandList.add("-listen");
        commandList.add("-compresslevel");
        commandList.add(compression.toString());
        commandList.add("-quality");
        commandList.add(quality.toString());
        if (bgr233CheckBox.isSelected()) {
            commandList.add("-bgr233");
        }
        OFFER_PROCESS_EXECUTOR.executeProcess(commandList.toArray(
                new String[commandList.size()]));
        return null;
    }*/
    
    /**
     * executed after executing onOfferSupportButtonAction
     * @param evt Action Event
     */
    public void onOfferActionDone(){
        compressionSpinner.setEnabled(true);
        qualitySpinner.setEnabled(true);
        bgr233CheckBox.setEnabled(true);
        securePortsTextField.setEnabled(true);
        offerSupportButton.setActionCommand("start");
        offerSupportButton.setText(BUNDLE.getString("Start_Service"));
        offerSupportButton.setIcon(new ImageIcon(getClass().getResource(
                "/ch/imedias/rscc/icons/16x16/fork.png")));
    }
    
    /**
     * executed at the end of onOfferSupportButtonAction
     * @param evt Action Event
     */
    public void afterOfferActionPerformed(){
        compressionSpinner.setEnabled(false);
        qualitySpinner.setEnabled(false);
        bgr233CheckBox.setEnabled(false);
        securePortsTextField.setEnabled(false);
        offerSupportButton.setActionCommand("stop");
        offerSupportButton.setText(BUNDLE.getString("Stop_Service"));
        offerSupportButton.setIcon(new ImageIcon(getClass().getResource(
                "/ch/imedias/rscc/icons/16x16/"
                + "process-stop.png")));
    }
    
   /* public void showPortError(String portString) {
        String errorMessage = BUNDLE.getString("Error_No_Port");
        errorMessage = MessageFormat.format(errorMessage, portString);
        showErrorMessage(errorMessage);
    }*/

    public void showErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(rsFrame, errorMessage,
                BUNDLE.getString("Error"), JOptionPane.ERROR_MESSAGE);
    }

    public void showPanel(JPanel panel, String cardName) {
        CardLayout cardLayout = (CardLayout) panel.getLayout();
        cardLayout.show(panel, cardName);
    }

    private class MyComboboBoxModel extends DefaultComboBoxModel{
    
        @Override
        public int getSize(){
            return supportAddresses.size();
        }
        
        @Override
        public Object getElementAt(int index){
            SupportAddress supportAddress = supportAddresses.get(index);
            if(supportAddress == null){
                return null;
            }else{
                return supportAddress.getDescription();
            }
        }
        
        public void fireContentsChanged(){
            fireContentsChanged(this, 0, getSize()-1);
        }
        
    }
}