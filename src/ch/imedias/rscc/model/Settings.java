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
    
    private final static Preferences preferences;
    
    // getters & setters
    public static String getSecurePorts() {
        return securePorts;
    }
    public static void setSecurePorts(String securePorts) {
        Settings.securePorts = securePorts;
    }
    public static int getQuality() {
        return quality;
    }
    public static void setQuality(int quality) {
        Settings.quality = quality;
    }
    public static int getCompressionLevel() {
        return compressionLevel;
    }
    public static void setCompressionLevel(int compressionLevel) {
        Settings.compressionLevel = compressionLevel;
    }
    public static boolean getBgr233() {
        return bgr233;
    }
    public static void setBgr233(boolean bgr233) {
        Settings.bgr233 = bgr233;
    }
    
    static {
        preferences = Preferences.userNodeForPackage(Settings.class);
        securePorts = preferences.get("securePorts", null);
        quality = preferences.getInt("quality", 6);
        compressionLevel = preferences.getInt("compressionLevel", 6);
        bgr233 = preferences.getBoolean("bgr233", false);
    }
    
    public static void save() {
        preferences.put("securePorts", securePorts);
        preferences.putInt("quality", quality);
        preferences.putInt("compressionLevel", compressionLevel);
        preferences.putBoolean("bgr233", bgr233);
    }
}
