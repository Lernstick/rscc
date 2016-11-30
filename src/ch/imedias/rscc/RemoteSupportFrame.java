package ch.imedias.rscc;

import java.awt.CardLayout;
import java.awt.EventQueue;
import java.awt.Frame;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.*;
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
import javax.swing.*;

/**
 * the main application frame
 *
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>
 */
public class RemoteSupportFrame
        extends JFrame
        implements PropertyChangeListener {

    private static final Logger LOGGER =
            Logger.getLogger(RemoteSupportFrame.class.getName());
    private static final ResourceBundle BUNDLE = ResourceBundle.getBundle(
            "ch/imedias/rscc/Bundle");
    private final static ProcessExecutor SEEK_PROCESS_EXECUTOR =
            new ProcessExecutor();
    private final static ProcessExecutor OFFER_PROCESS_EXECUTOR =
            new ProcessExecutor();
    private final static List<ProcessExecutor> TUNNEL_EXECUTORS =
            new ArrayList<ProcessExecutor>();
    private final static String SUPPORT_ADDRESSES = "supportAddresses";
    private final static String SECURE_PORTS = "securePorts";
    private final static String COMPRESSION_LEVEL = "compressionLevel";
    private final static String QUALITY = "quality";
    private final static String BGR233 = "bgr233";
    private final Preferences preferences;
    private final SpinnerNumberModel compressionSpinnerModel;
    private final SpinnerNumberModel qualitySpinnerModel;
    private final SpinnerNumberModel scaleSpinnerModel;
    private SupportAddress supportAddress;
    private Pattern okPlainPattern;
    private Pattern okSSLPattern;
    private Pattern failedPattern;
    private List<SupportAddress> supportAddresses;
    private MyComboboxModel comboboxModel;

    /**
     * Creates new form RemoteSupportFrame
     */
    public RemoteSupportFrame() {
        preferences = Preferences.userNodeForPackage(RemoteSupportFrame.class);
        compressionSpinnerModel = new SpinnerNumberModel(6, 0, 9, 1);
        qualitySpinnerModel = new SpinnerNumberModel(6, 0, 9, 1);
        scaleSpinnerModel = new SpinnerNumberModel(1.0, 0.1, null, 0.1);

        initComponents();
        setIconImage(new ImageIcon(getClass().getResource(
                "/ch/imedias/rscc/krdc.png")).getImage());

        // log to console
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setFormatter(new SimpleFormatter());
        consoleHandler.setLevel(Level.ALL);
        Logger peLogger = Logger.getLogger(ProcessExecutor.class.getName());
        peLogger.setLevel(Level.ALL);
        peLogger.addHandler(consoleHandler);
        peLogger.setUseParentHandlers(false);

        // load preferences
        String supportAddressesXML = preferences.get(SUPPORT_ADDRESSES, null);
        if (supportAddressesXML == null) {
            // use some hardcoded defaults
            supportAddresses = getDefaultList();
        } else {
            byte[] array = supportAddressesXML.getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(array);
            XMLDecoder decoder = new XMLDecoder(inputStream);
            supportAddresses = (List<SupportAddress>) decoder.readObject();
        }
        securePortsTextField.setText(preferences.get(SECURE_PORTS, null));
        compressionSpinnerModel.setValue(
                preferences.getInt(COMPRESSION_LEVEL, 6));
        qualitySpinnerModel.setValue(preferences.getInt(QUALITY, 6));
        bgr233CheckBox.setSelected(preferences.getBoolean(BGR233, false));

        comboboxModel = new MyComboboxModel();
        comboBox.setModel(comboboxModel);
        if (comboboxModel.getSize() > 0) {
            comboBox.setSelectedIndex(0);
        } else {
            comboBox.setSelectedIndex(-1);
        }

        pack();
        setLocationRelativeTo(null);
    }

//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String args[]) {
//        EventQueue.invokeLater(new Runnable() {
//
//            @Override
//            public void run() {
//                RemoteSupportFrame frame = new RemoteSupportFrame();
//                SEEK_PROCESS_EXECUTOR.addPropertyChangeListener(frame);
//                frame.setVisible(true);
//            }
//        });
//    }

    /**
     * handles property change events
     *
     * @param evt the event to handle
     */
    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getPropertyName().equals(ProcessExecutor.LINE)) {
            String line = (String) evt.getNewValue();
            if (failedPattern.matcher(line).matches()) {
                showPanel(seekSupportPanel, "mainPanel");
                JOptionPane.showMessageDialog(this,
                        BUNDLE.getString("Connection_Failed"),
                        BUNDLE.getString("Error"),
                        JOptionPane.ERROR_MESSAGE);
            } else if (okPlainPattern.matcher(line).matches()
                    || okSSLPattern.matcher(line).matches()) {
                String connectedMessage = BUNDLE.getString("Connected_To");
                connectedMessage = MessageFormat.format(
                        connectedMessage, supportAddress.getDescription());
                connectedLabel.setText(connectedMessage);
                setTitle(connectedMessage);
                showPanel(seekSupportPanel, "connectedPanel");
                new SwingWorker() {

                    @Override
                    protected Object doInBackground() throws Exception {
                        Thread.sleep(1000);
                        return null;
                    }

                    @Override
                    protected void done() {
                        setExtendedState(Frame.ICONIFIED);
                    }
                }.execute();
            }
        }
    }

    /**
     * returns the default support address list
     *
     * @return the default support address list
     */
    public static List<SupportAddress> getDefaultList() {
        List<SupportAddress> defaultList = new ArrayList<SupportAddress>();
        FilenameFilter rsccDefaultsFilter = new FilenameFilter() {

            @Override
            public boolean accept(File dir, String name) {
                return name.startsWith("rscc-defaults");
            }
        };
        File usrShareDir = new File("/usr/share/");
        File[] defaultFiles = usrShareDir.listFiles(rsccDefaultsFilter);
        for (File defaultFile : defaultFiles) {
            LOGGER.log(Level.INFO, "parsing {0}", defaultFile);
            FileReader fileReader = null;
            BufferedReader bufferedReader = null;
            try {
                fileReader = new FileReader(defaultFile);
                bufferedReader = new BufferedReader(fileReader);
                int lineCounter = 0;
                String description = null;
                String address = null;
                boolean encrypted;
                for (String line = bufferedReader.readLine(); line != null;
                        line = bufferedReader.readLine()) {
                    switch (lineCounter % 3) {
                        case 0:
                            description = line;
                            break;
                        case 1:
                            address = line;
                            break;
                        case 2:
                            encrypted = line.trim().equals("true");
                            defaultList.add(new SupportAddress(
                                    description, address, encrypted));
                    }
                    lineCounter++;
                }

            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "", ex);
            } finally {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "", ex);
                }
                try {
                    fileReader.close();
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "", ex);
                }
            }
        }
        return defaultList;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        java.awt.GridBagConstraints gridBagConstraints;

        tabbedPane = new javax.swing.JTabbedPane();
        seekSupportPanel = new javax.swing.JPanel();
        mainPanel = new javax.swing.JPanel();
        supportAddressLabel = new javax.swing.JLabel();
        comboBox = new javax.swing.JComboBox();
        editButton = new javax.swing.JButton();
        scaleLabel = new javax.swing.JLabel();
        scaleSpinner = new javax.swing.JSpinner();
        buttonPanel = new javax.swing.JPanel();
        connectButton = new javax.swing.JButton();
        quitButton = new javax.swing.JButton();
        connectingPanel = new javax.swing.JPanel();
        connectingLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();
        connectedPanel = new javax.swing.JPanel();
        connectedLabel = new javax.swing.JLabel();
        disconnectButton = new javax.swing.JButton();
        offerSupportPanel = new javax.swing.JPanel();
        picturePropertiesPanel = new javax.swing.JPanel();
        compressionLabel = new javax.swing.JLabel();
        compressionSpinner = new javax.swing.JSpinner();
        bgr233CheckBox = new javax.swing.JCheckBox();
        qualityLabel = new javax.swing.JLabel();
        qualitySpinner = new javax.swing.JSpinner();
        securePortsLabel = new javax.swing.JLabel();
        securePortsTextField = new javax.swing.JTextField();
        offerSupportButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DO_NOTHING_ON_CLOSE);
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("ch/imedias/rscc/Bundle"); // NOI18N
        setTitle(bundle.getString("RemoteSupportFrame.title")); // NOI18N
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });
        getContentPane().setLayout(new java.awt.GridBagLayout());

        seekSupportPanel.setLayout(new java.awt.CardLayout());

        mainPanel.setLayout(new java.awt.GridBagLayout());

        supportAddressLabel.setText(bundle.getString("RemoteSupportDialog.supportAddressLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        mainPanel.add(supportAddressLabel, gridBagConstraints);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        mainPanel.add(comboBox, gridBagConstraints);

        editButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ch/imedias/rscc/icons/16x16/document-edit.png"))); // NOI18N
        editButton.setToolTipText(bundle.getString("RemoteSupportFrame.editButton.toolTipText")); // NOI18N
        editButton.setMargin(new java.awt.Insets(2, 2, 2, 2));
        editButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        mainPanel.add(editButton, gridBagConstraints);

        scaleLabel.setText(bundle.getString("RemoteSupportFrame.scaleLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        mainPanel.add(scaleLabel, gridBagConstraints);

        scaleSpinner.setModel(scaleSpinnerModel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        mainPanel.add(scaleSpinner, gridBagConstraints);

        buttonPanel.setLayout(new java.awt.GridBagLayout());

        connectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ch/imedias/rscc/icons/16x16/network-connect.png"))); // NOI18N
        connectButton.setText(bundle.getString("RemoteSupportDialog.connectButton.text")); // NOI18N
        connectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                connectButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 0, 0, 5);
        buttonPanel.add(connectButton, gridBagConstraints);

        quitButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ch/imedias/rscc/icons/16x16/application-exit.png"))); // NOI18N
        quitButton.setText(bundle.getString("RemoteSupportDialog.cancelButton.text")); // NOI18N
        quitButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridy = 0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        buttonPanel.add(quitButton, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(20, 10, 10, 10);
        mainPanel.add(buttonPanel, gridBagConstraints);

        seekSupportPanel.add(mainPanel, "mainPanel");

        connectingPanel.setLayout(new java.awt.GridBagLayout());

        connectingLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        connectingLabel.setText(bundle.getString("Connecting_To")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        connectingPanel.add(connectingLabel, gridBagConstraints);

        progressBar.setIndeterminate(true);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 5);
        connectingPanel.add(progressBar, gridBagConstraints);

        seekSupportPanel.add(connectingPanel, "connectingPanel");

        connectedPanel.setLayout(new java.awt.GridBagLayout());

        connectedLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        connectedLabel.setText(bundle.getString("Connected_To")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        connectedPanel.add(connectedLabel, gridBagConstraints);

        disconnectButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ch/imedias/rscc/icons/16x16/network-disconnect.png"))); // NOI18N
        disconnectButton.setText(bundle.getString("RemoteSupportFrame.disconnectButton.text")); // NOI18N
        disconnectButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                disconnectButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.insets = new java.awt.Insets(10, 0, 0, 0);
        connectedPanel.add(disconnectButton, gridBagConstraints);

        seekSupportPanel.add(connectedPanel, "connectedPanel");

        tabbedPane.addTab(bundle.getString("RemoteSupportFrame.seekSupportPanel.TabConstraints.tabTitle"), seekSupportPanel); // NOI18N

        offerSupportPanel.setLayout(new java.awt.GridBagLayout());

        picturePropertiesPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(bundle.getString("RemoteSupportFrame.picturePropertiesPanel.border.title"))); // NOI18N
        picturePropertiesPanel.setLayout(new java.awt.GridBagLayout());

        compressionLabel.setText(bundle.getString("RemoteSupportFrame.compressionLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        picturePropertiesPanel.add(compressionLabel, gridBagConstraints);

        compressionSpinner.setModel(compressionSpinnerModel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 0);
        picturePropertiesPanel.add(compressionSpinner, gridBagConstraints);

        bgr233CheckBox.setText(bundle.getString("RemoteSupportFrame.bgr233CheckBox.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.EAST;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        picturePropertiesPanel.add(bgr233CheckBox, gridBagConstraints);

        qualityLabel.setText(bundle.getString("RemoteSupportFrame.qualityLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        picturePropertiesPanel.add(qualityLabel, gridBagConstraints);

        qualitySpinner.setModel(qualitySpinnerModel);
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.WEST;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 5, 0);
        picturePropertiesPanel.add(qualitySpinner, gridBagConstraints);

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(0, 5, 0, 5);
        offerSupportPanel.add(picturePropertiesPanel, gridBagConstraints);

        securePortsLabel.setText(bundle.getString("RemoteSupportFrame.securePortsLabel.text")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 0);
        offerSupportPanel.add(securePortsLabel, gridBagConstraints);

        securePortsTextField.setToolTipText(bundle.getString("RemoteSupportFrame.securePortsTextField.toolTipText")); // NOI18N
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.insets = new java.awt.Insets(5, 5, 0, 5);
        offerSupportPanel.add(securePortsTextField, gridBagConstraints);

        offerSupportButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/ch/imedias/rscc/icons/16x16/fork.png"))); // NOI18N
        offerSupportButton.setText(bundle.getString("Start_Service")); // NOI18N
        offerSupportButton.setActionCommand("start");
        offerSupportButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                offerSupportButtonActionPerformed(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridwidth = java.awt.GridBagConstraints.REMAINDER;
        gridBagConstraints.insets = new java.awt.Insets(10, 5, 5, 5);
        offerSupportPanel.add(offerSupportButton, gridBagConstraints);

        tabbedPane.addTab(bundle.getString("RemoteSupportFrame.offerSupportPanel.TabConstraints.tabTitle"), offerSupportPanel); // NOI18N

        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
        gridBagConstraints.weightx = 1.0;
        gridBagConstraints.weighty = 1.0;
        getContentPane().add(tabbedPane, gridBagConstraints);
    }// </editor-fold>//GEN-END:initComponents

    private void editButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editButtonActionPerformed
        EditDialog editDialog = new EditDialog(this, supportAddresses);
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
    }//GEN-LAST:event_editButtonActionPerformed

    private void connectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_connectButtonActionPerformed
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
                setTitle(BUNDLE.getString("RemoteSupportFrame.title"));
                showPanel(seekSupportPanel, "mainPanel");
                setExtendedState(Frame.NORMAL);
            }
        };
        swingWorker.execute();
        String connectMessage = BUNDLE.getString("Connecting_To");
        connectMessage = MessageFormat.format(
                connectMessage, supportAddress.getDescription());
        connectingLabel.setText(connectMessage);
        showPanel(seekSupportPanel, "connectingPanel");
    }//GEN-LAST:event_connectButtonActionPerformed

    private void quitButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitButtonActionPerformed
        exit();
    }//GEN-LAST:event_quitButtonActionPerformed

    private void disconnectButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_disconnectButtonActionPerformed
        disconnect();
    }//GEN-LAST:event_disconnectButtonActionPerformed

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        exit();
    }//GEN-LAST:event_formWindowClosing

    private void offerSupportButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_offerSupportButtonActionPerformed
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
                }

                @Override
                protected void done() {
                    compressionSpinner.setEnabled(true);
                    qualitySpinner.setEnabled(true);
                    bgr233CheckBox.setEnabled(true);
                    securePortsTextField.setEnabled(true);
                    offerSupportButton.setActionCommand("start");
                    offerSupportButton.setText(BUNDLE.getString("Start_Service"));
                    offerSupportButton.setIcon(new ImageIcon(getClass().getResource(
                            "/ch/imedias/rscc/icons/16x16/fork.png")));
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

            compressionSpinner.setEnabled(false);
            qualitySpinner.setEnabled(false);
            bgr233CheckBox.setEnabled(false);
            securePortsTextField.setEnabled(false);
            offerSupportButton.setActionCommand("stop");
            offerSupportButton.setText(BUNDLE.getString("Stop_Service"));
            offerSupportButton.setIcon(new ImageIcon(getClass().getResource(
                    "/ch/imedias/rscc/icons/16x16/"
                    + "process-stop.png")));
        } else {
            stopOffer();
        }
    }//GEN-LAST:event_offerSupportButtonActionPerformed

    private void showPortError(String portString) {
        String errorMessage = BUNDLE.getString("Error_No_Port");
        errorMessage = MessageFormat.format(errorMessage, portString);
        showErrorMessage(errorMessage);
    }

    private void showErrorMessage(String errorMessage) {
        JOptionPane.showMessageDialog(this, errorMessage,
                BUNDLE.getString("Error"), JOptionPane.ERROR_MESSAGE);
    }

    private void showPanel(JPanel panel, String cardName) {
        CardLayout cardLayout = (CardLayout) panel.getLayout();
        cardLayout.show(panel, cardName);
    }

    private void disconnect() {
        SEEK_PROCESS_EXECUTOR.destroy();
    }

    private void stopOffer() {
        OFFER_PROCESS_EXECUTOR.destroy();
        for (ProcessExecutor tunnelExecutor : TUNNEL_EXECUTORS) {
            tunnelExecutor.destroy();
        }
        ProcessExecutor processExecutor = new ProcessExecutor();
        processExecutor.executeProcess("killall", "-9", "stunnel4");
    }

    private void exit() {
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

    private class MyComboboxModel extends DefaultComboBoxModel {

        @Override
        public int getSize() {
            return supportAddresses.size();
        }

        @Override
        public Object getElementAt(int index) {
            SupportAddress supportAddress = supportAddresses.get(index);
            if (supportAddress == null) {
                return null;
            } else {
                return supportAddress.getDescription();
            }
        }

        public void fireContentsChanged() {
            fireContentsChanged(this, 0, getSize() - 1);
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox bgr233CheckBox;
    private javax.swing.JPanel buttonPanel;
    private javax.swing.JComboBox comboBox;
    private javax.swing.JLabel compressionLabel;
    private javax.swing.JSpinner compressionSpinner;
    private javax.swing.JButton connectButton;
    private javax.swing.JLabel connectedLabel;
    private javax.swing.JPanel connectedPanel;
    private javax.swing.JLabel connectingLabel;
    private javax.swing.JPanel connectingPanel;
    private javax.swing.JButton disconnectButton;
    private javax.swing.JButton editButton;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton offerSupportButton;
    private javax.swing.JPanel offerSupportPanel;
    private javax.swing.JPanel picturePropertiesPanel;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel qualityLabel;
    private javax.swing.JSpinner qualitySpinner;
    private javax.swing.JButton quitButton;
    private javax.swing.JLabel scaleLabel;
    private javax.swing.JSpinner scaleSpinner;
    private javax.swing.JLabel securePortsLabel;
    private javax.swing.JTextField securePortsTextField;
    private javax.swing.JPanel seekSupportPanel;
    private javax.swing.JLabel supportAddressLabel;
    private javax.swing.JTabbedPane tabbedPane;
    // End of variables declaration//GEN-END:variables
}
