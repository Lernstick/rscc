/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import java.io.IOException;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;

/**
 *
 * @author user
 */
public final class FXMLGuiLoader {
    private static FXMLGuiLoader instance = new FXMLGuiLoader();
    
    private Scene remoteSupportStart;
    private Scene provideSupport;
    private Scene requestSupport;
    private Scene editDialog;
    
    private FXMLGuiLoader() {}
    
    public static FXMLGuiLoader getInstance() {
        return instance;
    }
    
    public Scene getRemoteSupportStart() {
        if (remoteSupportStart == null) {
            // Create new Instance
            try {
            remoteSupportStart = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/RemoteSupportStart.fxml")));
            } catch(IOException ex) {
               ex.printStackTrace();
            }
        }
        return remoteSupportStart;
    }
    
    public Scene getProvideSupport() {
        if (provideSupport == null) {
            // Create new Instance
            try {
            provideSupport = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/ProvideSupport.fxml")));
            } catch(IOException ex) {
               ex.printStackTrace();
            }
        }
        return provideSupport;
    }
    
    public Scene getRequestSupport() {
        if (requestSupport == null) {
            // Create new Instance
            try {
            requestSupport = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/RequestSupport.fxml")));
            } catch(IOException ex) {
               ex.printStackTrace();
            }
        }
        return requestSupport;
    }
    
    public Scene getEditDialog() {
        if (editDialog == null) {
            // Create new Instance
            try {
            editDialog = new Scene((Parent)FXMLLoader.load(getClass().getResource("../view/EditDialog.fxml")));
            } catch(IOException ex) {
               ex.printStackTrace();
            }
        }
        return editDialog;
    }
}
