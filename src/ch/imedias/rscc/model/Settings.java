package ch.imedias.rscc.model;
import java.util.prefs.Preferences;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * rscc settings
 * @author patric steiner
 */
public class Settings {
    // private fields
    private String securePorts;
    private int quality;
    private int compressionLevel;
    private boolean bgr233;
    
    private final Preferences preferences;
    
    // getters & setters
    public String getSecurePorts() {
        return securePorts;
    }
    public void setSecurePorts(String securePorts) {
        this.securePorts = securePorts;
    }
    public int getQuality() {
        return quality;
    }
    public void setQuality(int quality) {
        this.quality = quality;
    }
    public int getCompressionLevel() {
        return compressionLevel;
    }
    public void setCompressionLevel(int compressionLevel) {
        this.compressionLevel = compressionLevel;
    }
    public boolean getBgr233() {
        return bgr233;
    }
    public void setBgr233(boolean bgr233) {
        this.bgr233 = bgr233;
    }
    
    public Settings() {
        preferences = Preferences.userNodeForPackage(Settings.class);
        securePorts = preferences.get("securePorts", null);
        quality = preferences.getInt("quality", 6);
        compressionLevel = preferences.getInt("compressionLevel", 6);
        bgr233 = preferences.getBoolean("bgr233", false);
    }
    
    public void save() {
        preferences.put("securePorts", securePorts);
        preferences.putInt("quality", quality);
        preferences.putInt("compressionLevel", compressionLevel);
        preferences.putBoolean("bgr233", bgr233);
    }
}
