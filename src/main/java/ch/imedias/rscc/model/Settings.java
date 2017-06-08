package ch.imedias.rscc.model;
import java.util.prefs.Preferences;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Static class for rscc settings.
 * @author patric steiner
 */
public class Settings {
    // private fields
    private static String securePorts;
    private static int quality;
    private static int compressionLevel;
    private static boolean bgr233;
    private static boolean useSSHPort;
    
    private final static Preferences PREFERENCES;
    
    // getters & setters
    /**
     * @return the secure ports
     */
    public static String getSecurePorts() {
        return securePorts;
    }
    
    /**
     * @param securePorts the secure ports 
     */
    public static void setSecurePorts(String securePorts) {
        Settings.securePorts = securePorts;
    }
    
    /**
     * @return the quality
     */
    public static int getQuality() {
        return quality;
    }
    
    /**
     * @param quality the quality
     */
    public static void setQuality(int quality) {
        Settings.quality = quality;
    }
    
    /**
     * @return the compression level
     */
    public static int getCompressionLevel() {
        return compressionLevel;
    }
    
    /**
     * @param compressionLevel the compression level
     */
    public static void setCompressionLevel(int compressionLevel) {
        Settings.compressionLevel = compressionLevel;
    }
    
    /**
     * @return shows if bgr233 supported
     */
    public static boolean getBgr233() {
        return bgr233;
    }
    
    /**
     * @param bgr233 set bgr233
     */
    public static void setBgr233(boolean bgr233) {
        Settings.bgr233 = bgr233;
    }
    
    /**
     * @return shows if https is used
     */
    public static boolean getUseHttpsPort() {
        return useSSHPort;
    }
    
    /**
     * @param useSSHPort shows if SSH port is used
     */
    public static void setUseSSHPort(boolean useSSHPort) {
        Settings.useSSHPort = useSSHPort;
    }
    
    static {
        PREFERENCES = Preferences.userNodeForPackage(Settings.class);
        securePorts = PREFERENCES.get("securePorts", "");
        quality = PREFERENCES.getInt("quality", 6);
        compressionLevel = PREFERENCES.getInt("compressionLevel", 6);
        bgr233 = PREFERENCES.getBoolean("bgr233", false);
        useSSHPort = PREFERENCES.getBoolean("useSSHPort", false);
    }
    
    /**
     * Saves the values into preferences
     */
    public static void save() {
        PREFERENCES.put("securePorts", securePorts);
        PREFERENCES.putInt("quality", quality);
        PREFERENCES.putInt("compressionLevel", compressionLevel);
        PREFERENCES.putBoolean("bgr233", bgr233);
        PREFERENCES.putBoolean("useHttpsPort", useSSHPort);
    }
}