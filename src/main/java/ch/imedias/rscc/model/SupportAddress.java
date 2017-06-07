package ch.imedias.rscc.model;

import java.beans.DefaultPersistenceDelegate;
import java.beans.PersistenceDelegate;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.Preferences;

/**
 * a VNC support address. Also provides methods to retrieve, save and reset a 
 * complete list of addresses.
 * @author Ronny Standtke <ronny.standtke@fhnw.ch>, updated and expanded patric steiner
 */
public class SupportAddress implements Serializable {

    private static final long serialVersionUID = 0L;

    private String description;
    private String address;
    private boolean encrypted;
    private static List<SupportAddress> supportAddresses;
    
    private final static Preferences PREFERENCES = Preferences.userNodeForPackage(SupportAddress.class);;
    
    private static final Logger LOGGER = Logger.getLogger(SupportAddress.class.getName());
    
    /**
     * returns list of supportAddresses from static variable or from file
     * 
     * @return list of supportAddresses
     */
    public static List<SupportAddress> getAll() {
        if (supportAddresses != null) return supportAddresses; // if it has already been set: just return it
        // else read from storage
        String supportAddressesXML = PREFERENCES.get("supportAddresses", null);
        if (supportAddressesXML == null) {
            // use some hardcoded defaults
            supportAddresses = getDefaultList();
        } else {
            byte[] array = supportAddressesXML.getBytes();
            ByteArrayInputStream inputStream = new ByteArrayInputStream(array);
            XMLDecoder decoder = new XMLDecoder(inputStream);
            supportAddresses = (List<SupportAddress>) decoder.readObject();
        }
        return supportAddresses;
    }
    
    /**
     * Replaces the static list of supportAddresses.
     * 
     * @param supportAddresses List of supportAddresses
     */
    public static void setAll(List<SupportAddress> supportAddresses) {
        SupportAddress.supportAddresses = supportAddresses;
    }
    
    /**
     * Saves data to XML-file.
     */
    public static void saveAll() {
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        try (XMLEncoder encoder = new XMLEncoder(byteArrayOutputStream)) {
            encoder.setPersistenceDelegate(SupportAddress.class,
                    SupportAddress.getPersistenceDelegate());
            encoder.writeObject(supportAddresses);
        }
        String supportAddressesXML = byteArrayOutputStream.toString();
        PREFERENCES.put("supportAddresses", supportAddressesXML);
    }
    
    /**
     * Resets attributes of this with data from local file.
     */
    public static void resetAllToDefault() {
        supportAddresses = getDefaultList();
    }
    
    /**
     * Returns the default support address list. Reads local file.
     *
     * @return the default support address list
     */
    private static List<SupportAddress> getDefaultList() {
        List<SupportAddress> defaultList = new ArrayList<>();
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
     * Creates a new SupportAddress.
     *
     * @param description the description
     * @param address the address
     * @param encrypted if the connection is encrypted
     */
    public SupportAddress(
            String description, String address, boolean encrypted) {
        this.description = description;
        this.address = address;
        this.encrypted = encrypted;
    }

    /**
     * Returns the description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets the description.
     *
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Returns the address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets the address.
     *
     * @param address the address to set
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Returns
     * <code>true</code>, if the connection is encrypted,
     * <code>false</code> otherwise.
     *
     * @return
     * <code>true</code>, if the connection is encrypted,
     * <code>false</code> otherwise
     */
    public boolean isEncrypted() {
        return encrypted;
    }

    /**
     * Sets the encrypted property of the SupportAddress.
     *
     * @param encrypted if the SupportAddress is used for encrypted connections
     */
    public void setEncrypted(boolean encrypted) {
        this.encrypted = encrypted;
    }

    /**
     * Returns the PersistenceDelegate.
     *
     * @return the PersistenceDelegate
     */
    public static PersistenceDelegate getPersistenceDelegate() {
        return new DefaultPersistenceDelegate(
                new String[]{"description", "address", "encrypted"});
    }

    /**
     * Override to String cause main info is supporter name.
     * 
     * @return the string
     */
    @Override
    public String toString() {
        return getDescription();
    }
    
    
}
