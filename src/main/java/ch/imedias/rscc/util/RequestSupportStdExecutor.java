/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import ch.imedias.rscc.model.SupportAddress;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Util class to request remote support. Provides method to connect to a
 * vnc (xtightvncviewer) listener.
 */
public class RequestSupportStdExecutor implements RequestSupportExecutor {
    private Pattern okPlainPattern;
    private Pattern okSSLPattern;
    private Pattern failedPattern;
    
    private ProcessExecutorFactory factory;
    private final ProcessExecutor SEEK_PROCESS_EXECUTOR;
    
    private ExecutorService executor;
    
    /**
     * Construcor, adds changelistener to ProcessExecutor. If the succeeds,
     * "success" will be called, "failed" otherwise. Callbacks can be null.
     * @param factory ProcessExecutorFactory
     * @param executor The executor to be used (normally Executors.newCachedThreadPool())
     * @param success Callback
     * @param failed Callback
     */
    public RequestSupportStdExecutor(ProcessExecutorFactory factory, ExecutorService executor, Runnable success, Runnable failed) {
        this.factory = factory;
        this.executor = executor;
        SEEK_PROCESS_EXECUTOR = factory.makeProcessExecutor();
        SEEK_PROCESS_EXECUTOR.addPropertyChangeListener(evt -> {
            if(success != null && 
                    (okPlainPattern.matcher((String) evt.getNewValue()).matches() || 
                    okSSLPattern.matcher((String) evt.getNewValue()).matches()))
                Platform.runLater(success);
            else if(failed != null && 
                    failedPattern.matcher((String) evt.getNewValue()).matches())
                Platform.runLater(failed);
        });
    }
    
    /**
     * This method is unused at the time, but might be useful some time in the 
     * future when you want to set different executors for different tasks or 
     * also if you want to set a new executor after shutting down the old one.
     */
    /*public void setExecutorService(ExecutorService executor) {
        this.executor = executor;
    }*/
    
    /**
     * Starts x11vnc and connects to supportAddress with given scale.
     * @param supportAddress
     * @param scale
     * @param password
     */
    public void connect(final SupportAddress supportAddress, final Double scale, final String password){
        final String address = supportAddress.getAddress();

        okPlainPattern = Pattern.compile(".*reverse_connect: " + address + "/.* OK");
        okSSLPattern = Pattern.compile(".*created selwin:.*");
        failedPattern = Pattern.compile(".*reverse_connect: " + address + " failed");

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
        };
        executor.submit(task);
    }
    /**
     * Stops service.
     */
    public void disconnect() {
        SEEK_PROCESS_EXECUTOR.destroy();
    }
    
    /**
     * Stops the service and shuts the executor down.
     */
    public void exit() {
        disconnect();
        executor.shutdown();
    }
}