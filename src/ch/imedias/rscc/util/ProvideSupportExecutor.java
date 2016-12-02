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
    public void startOffer(String securePortsText, final Number compression, final Number quality, final boolean isBGR233selected){
        String[] securePortsStrings = securePortsText.split(",");
        List<Integer> securePorts = new ArrayList<Integer>();
        for (String securePortsString : securePortsStrings) {
            String trimmed = securePortsString.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            // throws NumberFormatException
            int securePort = Integer.parseInt(trimmed);
            if ((securePort >= 0) && (securePort <= 65535)) {
                securePorts.add(securePort);
            } else {
                throw new NumberFormatException();
            }
        }

        Task viewerTask = new Task() {
            @Override
            protected Object call() throws Exception {
                return onOfferActionDoInBackground(compression, quality, isBGR233selected);
            }
        };
        executor.submit(viewerTask);

        // check that the pem file for stunnel is there
        final String pemFilePath = System.getProperty("user.home")
                + "/.local/stunnel.pem";
        if (!securePorts.isEmpty()) {
            File pemFile = new File(pemFilePath);
            if (!pemFile.exists()) {
                ProcessExecutor processExecutor = factory.makeProcessExecutor();
                processExecutor.executeProcess("openssl", "req", "-x509",
                        "-nodes", "-days", "36500", "-subj",
                        "/C=/ST=/L=/CN=tmp", "-newkey",
                        "rsa:1024",
                        "-keyout", pemFilePath, "-out", pemFilePath);
            }
        }

        for (final Integer securePort : securePorts) {
            Task tunnelTask = new Task() {
                @Override
                protected Object call() throws Exception {
                    ProcessExecutor tunnelExecutor = factory.makeProcessExecutor();
                    tunnelExecutor.executeProcess(
                            "stunnel", "-f", "-P", "", "-p", pemFilePath,
                            "-d", securePort.toString(), "-r", "5500");
                    TUNNEL_EXECUTORS.add(tunnelExecutor);
                    return null;
                }
            };
            executor.submit(tunnelTask);
        }       
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
    
    public void exit() {
        stopOffer();
        executor.shutdown();
    }
}
