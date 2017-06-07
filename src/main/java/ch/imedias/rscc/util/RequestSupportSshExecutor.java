/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import ch.imedias.rscc.model.SupportAddress;
import java.util.concurrent.ExecutorService;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Util class to request remote support. Provides method to connect to a vnc
 * (xtightvncviewer) listener.
 */
public class RequestSupportSshExecutor implements RequestSupportExecutor {
    private final Pattern successPattern = Pattern.compile(".*reverse_connect: .* OK");
    private final Pattern failedPattern = Pattern.compile(".*reverse_connect: .* failed");
    
    private final ProcessExecutor SEEK_PROCESS_EXECUTOR;

    private final ExecutorService executor;
    private final Runnable success;
    private final Runnable failed;

    /**
     * Construcor, creates the process executor
     *
     * @param factory ProcessExecutorFactory
     * @param executor The executor to be used (normally
     * Executors.newCachedThreadPool())
     * @param success Callback
     * @param failed Callback
     */
    public RequestSupportSshExecutor(ProcessExecutorFactory factory, ExecutorService executor, Runnable success, Runnable failed) {
        this.executor = executor;
        this.success = success;
        this.failed = failed;
        
        SEEK_PROCESS_EXECUTOR = factory.makeProcessExecutor();
        SEEK_PROCESS_EXECUTOR.addPropertyChangeListener(evt -> {
            if(success != null && 
                    successPattern.matcher((String) evt.getNewValue()).matches())
                Platform.runLater(success);
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
     * Starts the ssh connection to the supportAddress with given password.
     * <br>
     * Runs a background thread to check if the connection is still running to 
     * reliably show the connected dialog.
     *
     * @param supportAddress
     * @param scale
     * @param password
     */
    @Override
    public void connect(final SupportAddress supportAddress, final Double scale, final String password) {
        String[] splitted = supportAddress.getAddress().split(":");
        final String address = splitted[0];
        final String scaleString = scale.toString();

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                String port = "22"; // default Port
                if (splitted.length >= 2) {
                    port = splitted[1];
                }
                
                String sshConnection = 
                    "sshpass -p " + password + " ssh -o StrictHostKeyChecking=no" +
                    " -p " + port + " -NfL5500:localhost:5500 rscc_ssh@" + address + 
                    " sleep 2 && x11vnc -connect_or_exit localhost:5500" +
                    " -scale " + scaleString;
                int exitCode = SEEK_PROCESS_EXECUTOR.executeScript(true, true, sshConnection);
                
                // Exit code 143 is killed by another process
                if (exitCode != 0 && exitCode != 143 ){
                    Platform.runLater(failed);
                } else {
                    disconnect();
                }

                return null;
            }
        };

        executor.submit(task);
    }

    /**
     * Stops service.
     */
    @Override
    public void disconnect() {
        SEEK_PROCESS_EXECUTOR.destroy();
        SEEK_PROCESS_EXECUTOR.executeProcess("killall", "-9", "x11vnc");
    }

    /**
     * Stops the service and shuts the executor down.
     */
    @Override
    public void exit() {
        disconnect();
        executor.shutdown();
    }
}
