/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import ch.imedias.rscc.model.Settings;
import ch.imedias.rscc.model.SupportAddress;
import java.beans.XMLEncoder;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.ConsoleHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

import javafx.beans.property.Property;
import javafx.concurrent.Task;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingWorker;


/**
 *
 * @author user
 */
public class RemoteSupportExecutor {
     
    private final static List<ProcessExecutor> TUNNEL_EXECUTORS =
            new ArrayList<ProcessExecutor>();
    private final static ProcessExecutor SEEK_PROCESS_EXECUTOR =
            new ProcessExecutor();
    private final static ProcessExecutor OFFER_PROCESS_EXECUTOR =
            new ProcessExecutor();
    
    private final static ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "ch/imedias/rscc/Bundle");
    
    public static void initLogger(){
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL);
        Logger peLogger = Logger.getLogger(ProcessExecutor.class.getName());
        peLogger.setLevel(Level.ALL);
        peLogger.addHandler(consoleHandler);
        peLogger.setUseParentHandlers(false);
    }
    
    public static void initSpinnerModels(TextField securePortsTextField,
                                         SpinnerNumberModel compressionSpinnerModel,
                                         SpinnerNumberModel qualitySpinnerModel,
                                         CheckBox bgr233CheckBox){
        
        securePortsTextField.setText(Settings.getSecurePorts());
        compressionSpinnerModel.setValue((Settings.getCompressionLevel()));
        qualitySpinnerModel.setValue(Settings.getQuality());
        bgr233CheckBox.setSelected(Settings.getBgr233());
    }
    
    public static void startOffer(String securePortsText, final Number compression, 
                                  final Number quality, final boolean isBGR233selected){
        
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

        // XXX ok?
        Task viewerTask = new Task() {
            @Override
            protected Object call() throws Exception {
                return onOfferActionDoInBackground(compression, quality, isBGR233selected);
            }
        };
        viewerTask.run();

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
            // XXX ok?
            Task tunnelTask = new Task() {
                @Override
                protected Object call() throws Exception {
                    ProcessExecutor tunnelExecutor = new ProcessExecutor();
                    tunnelExecutor.executeProcess(
                            "stunnel", "-f", "-P", "", "-p", pemFilePath,
                            "-d", securePort.toString(), "-r", "5500");
                    TUNNEL_EXECUTORS.add(tunnelExecutor);
                    return null;
                }
            };
            tunnelTask.run();
        }
        
    }
    
    /**
     * 
     * @param compression taken from compressionSpinnerModel
     * @param quality taken from qualitySpinnerModel
     * @return 
     */
    private static Object onOfferActionDoInBackground(Number compression, 
                                                        Number quality, 
                                                        boolean isBGR233selected){
        List<String> commandList = new ArrayList<String>();
        commandList.add("xtightvncviewer");
        commandList.add("-listen");
        commandList.add("-compresslevel");
        commandList.add(compression.toString());
        commandList.add("-quality");
        commandList.add(quality.toString());
        if (isBGR233selected) {
            commandList.add("-bgr233");
        }
        OFFER_PROCESS_EXECUTOR.executeProcess(commandList.toArray(
                new String[commandList.size()]));
        return null;
    }
    
    public static void stopOffer() {
        OFFER_PROCESS_EXECUTOR.destroy();
        for (ProcessExecutor tunnelExecutor : TUNNEL_EXECUTORS) {
            tunnelExecutor.destroy();
        }
        ProcessExecutor processExecutor = new ProcessExecutor();
        processExecutor.executeProcess("killall", "-9", "stunnel4");
    }
    
    private static Pattern okPlainPattern;
    private static Pattern okSSLPattern;
    private static Pattern failedPattern;
    
    /**
     * 
     * @param supportAddress
     * @param scale
     * @param statusProperty will be set to true as soon as the connection task has succeeded
     */
    public static void connect(final SupportAddress supportAddress,
                               final Double scale, Property<Boolean> statusProperty){

        final String address = supportAddress.getAddress();

        okPlainPattern = Pattern.compile(
                ".*reverse_connect: " + address + "/.* OK");
        okSSLPattern = Pattern.compile(".*created selwin:.*");
        failedPattern = Pattern.compile(
                ".*reverse_connect: " + address + " failed");

        
        // XXX ok?
        Task task = new Task() {

            @Override
            protected Object call() throws Exception {
                List<String> commandList = new ArrayList<String>();
                commandList.add("x11vnc");
                commandList.add("-connect_or_exit");
                commandList.add(address);
                if (supportAddress.isEncrypted()) {
                    commandList.add("-ssl");
                    commandList.add("TMP");
                }
                String scaleString = scale.toString();
                if (!scaleString.equals("1.0")) {
                    commandList.add("-scale");
                    commandList.add(scaleString);
                }
                String[] commandArray =
                        commandList.toArray(new String[commandList.size()]);
                SEEK_PROCESS_EXECUTOR.executeProcess(true, true, commandArray);
                return null;
            }

           /* TODO see RequestSupportController.onConnectAction()
              @Override
              protected void done() {
                rsFrame.setTitle(BUNDLE.getString("RemoteSupportFrame.title"));
                showPanel(rsFrame.getSeekSuportPanel(), "mainPanel");
                rsFrame.setExtendedState(Frame.NORMAL);
            }*/
        };
        task.run();
        task.setOnSucceeded(e -> statusProperty.setValue(true));
        //XXX the stuff below can prolly be deleted if our property stuff works
        //swingWorker.execute();
        String connectMessage = BUNDLE.getString("Connecting_To");
        connectMessage = MessageFormat.format(
                connectMessage, supportAddress.getDescription());
        // TODO GUI connectingLabel.setText(connectMessage);
        // TODO GUI showPanel(seekSupportPanel, "connectingPanel");
        
    }
    
    private static void exit(List<SupportAddress> supportAddresses){
        // save preferences
        ByteArrayOutputStream byteArrayOutputStream =
                new ByteArrayOutputStream();
        XMLEncoder encoder = new XMLEncoder(byteArrayOutputStream);
        encoder.setPersistenceDelegate(SupportAddress.class,
                SupportAddress.getPersistenceDelegate());
        encoder.writeObject(supportAddresses);
        encoder.close();
        String supportAddressesXML = byteArrayOutputStream.toString(); // TODO not used?
        Settings.save();

        // stop all external processes
        disconnect();
        stopOffer();

        System.exit(0);
    }
    
    private static void disconnect(){
        SEEK_PROCESS_EXECUTOR.destroy();
    }
    
    // TODO only GUI related stuff
    private static void changeProperty(String newValue,
                                        SupportAddress supportAddress,
                                        Label connectingLabel){
        
        if (failedPattern.matcher(newValue).matches()) {
            // TODO showPanel(seekSupportPanel, "mainPanel");
           
            /* TODO JOptionPane.showMessageDialog(rsFrame,
                    BUNDLE.getString("Connection_Failed"),
                    BUNDLE.getString("Error"),
                    JOptionPane.ERROR_MESSAGE); */
            
        } else if (okPlainPattern.matcher(newValue).matches()
                || okSSLPattern.matcher(newValue).matches()) {
            String connectedMessage = BUNDLE.getString("Connected_To");
            connectedMessage = MessageFormat.format(
                    connectedMessage, supportAddress.getDescription());
            connectingLabel.setText(connectedMessage);
           /* TODO 
                rsFrame.setTitle(connectedMessage);
                showPanel(seekSupportPanel, "connectedPanel"); */
            new SwingWorker(){

                @Override
                protected Object doInBackground() throws Exception {
                    Thread.sleep(1000);
                    return null;
                }

                @Override
                protected void done() {
                   // TODO rsFrame.setExtendedState(Frame.ICONIFIED);
                }
            }.execute();
        }
    }
    
    private static void showPortError(String portString){
        String errorMessage = BUNDLE.getString("Error_No_Port");
        errorMessage = MessageFormat.format(errorMessage, portString);
        // TODO showErrorMessage(errorMessage);
    }
}
