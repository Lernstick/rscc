/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import javafx.concurrent.Task;

/**
 * Util class to offer remote support. Basically just provides methods to run 
 * xtightvncviewer listener and an stunnel.
 */
public class ProvideSupportExecutor {
    private ProcessExecutorFactory factory;
    private final ProcessExecutor OFFER_PROCESS_EXECUTOR;
    private final List<ProcessExecutor> TUNNEL_EXECUTORS = new ArrayList<ProcessExecutor>();
    private ExecutorService executor = Executors.newCachedThreadPool();
    
    public ProvideSupportExecutor(ProcessExecutorFactory factory) {
        this.factory = factory;
        OFFER_PROCESS_EXECUTOR = factory.makeProcessExecutor();
    }
    
    /**
     * Starts xtightvncviewer listener + stunnel.
     * @param securePortsText
     * @param compression
     * @param quality
     * @param isBGR233selected 
     */
    public void startOffer(final Number compression, final Number quality, final boolean isBGR233selected, boolean isSSHConnection){
         Task viewerTask = new Task() {
            @Override
            protected Object call() throws Exception {
                return onOfferActionDoInBackground(compression, quality, isBGR233selected);
            }
        };
        executor.submit(viewerTask);    
    }   
    
    /**
     * Starts the actual xtightvncviewer listener (invoked by startOffer).
     * @param compression taken from compressionSpinnerModel
     * @param quality taken from qualitySpinnerModel
     * @return 
     */
    private Object onOfferActionDoInBackground(Number compression, 
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
        OFFER_PROCESS_EXECUTOR.executeProcess(commandList.toArray(new String[commandList.size()]));
        return null;
    }
    
    /**
     * Kills all previously opened processes by this class.
     */
    public void stopOffer() {
        OFFER_PROCESS_EXECUTOR.destroy();
        for(ProcessExecutor tunnelExecutor : TUNNEL_EXECUTORS) {
            tunnelExecutor.destroy();
        }
        ProcessExecutor processExecutor = factory.makeProcessExecutor();
        processExecutor.executeProcess("killall", "-9", "stunnel4");
    }
    
    /**
     * Kills all processes and shuts the executor down.
     */
    public void exit() {
        stopOffer();
        executor.shutdown();
    }
}
