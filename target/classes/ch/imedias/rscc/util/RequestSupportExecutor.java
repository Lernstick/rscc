/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import ch.imedias.rscc.model.SupportAddress;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javafx.application.Platform;
import javafx.concurrent.Task;

/**
 * Util class to request remote support. Provides method to connect to a vnc
 * (xtightvncviewer) listener.
 */
public class RequestSupportExecutor {

    private Pattern okPlainPattern;
    private Pattern okSSLPattern;
    private Pattern failedPattern;

    private final ProcessExecutor SEEK_PROCESS_EXECUTOR;
    private ScheduledExecutorService ses;

    private final ExecutorService executor;
    private boolean connected = false;
    private final Runnable success;
    private final Runnable failed;

    /**
     * Construcor, adds changelistener to ProcessExecutor. If the succeeds,
     * "success" will be called, "failed" otherwise. Callbacks can be null.
     *
     * @param factory ProcessExecutorFactory
     * @param executor The executor to be used (normally
     * Executors.newCachedThreadPool())
     * @param success Callback
     * @param failed Callback
     */
    public RequestSupportExecutor(ProcessExecutorFactory factory, ExecutorService executor, Runnable success, Runnable failed) {
        this.executor = executor;
        SEEK_PROCESS_EXECUTOR = factory.makeProcessExecutor();
        this.success = success;
        this.failed = failed;
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
     * Starts x11vnc and connects to supportAddress with given scale. vnc
     *
     * @param supportAddress
     * @param scale
     * @param password
     */
    public void connect(final SupportAddress supportAddress, final Double scale, final String password) {
        String[] splitted = supportAddress.getAddress().split(":");
        final String address = splitted[0];
        final String scaleString = scale.toString();

        okPlainPattern = Pattern.compile(".*reverse_connect: " + address + "/.* OK");
        okSSLPattern = Pattern.compile(".*created selwin:.*");
        failedPattern = Pattern.compile(".*reverse_connect: " + address + " failed");

        Task task = new Task() {
            @Override
            protected Object call() throws Exception {
                if (supportAddress.isEncrypted()) {
                    String port = "22"; // default Port
                    if (splitted.length >= 2) {
                        port = splitted[1];
                    }

                    // TODO: throw exception if port wrong, etc
                    String sshConnection = String.format("%1$s %2$s %3$s %4$s %5$s %6$s %7$s",
                            "sshpass -p " + password,
                            "ssh -o StrictHostKeyChecking=no",
                            "-p" + port,
                            "-NfL5500:localhost:5500 rscc_ssh@" + address,
                            "sleep 2 && x11vnc",
                            "-connect_or_exit localhost:5500",
                            "-scale " + scaleString
                    );

                    SEEK_PROCESS_EXECUTOR.executeScript(SEEK_PROCESS_EXECUTOR.createScript(sshConnection).getAbsolutePath());

                } else {
                    String sshConnection = "x11vnc -connect_or_exit -scale" + scaleString;
                    SEEK_PROCESS_EXECUTOR.executeScript(SEEK_PROCESS_EXECUTOR.createScript(sshConnection).getAbsolutePath());
                }
                return null;
            }
        };

        executor.submit(task);

        // Start new Thread to observe SSH-connection
        try {
            final long start = System.currentTimeMillis();
            final String path = SEEK_PROCESS_EXECUTOR.createScript("netstat -anp | grep x11vnc").getAbsolutePath();
            ses = Executors.newSingleThreadScheduledExecutor();
            ses.scheduleAtFixedRate(() -> {
                try {
                    SEEK_PROCESS_EXECUTOR.executeScript(true, false, path);
                    String s = SEEK_PROCESS_EXECUTOR.getOutput();
                    if (!connected && s != null && !s.isEmpty()) {
                        connected = true;
                        Platform.runLater(success);
                    } else if (!connected) {
                        // Needs to connect within 12s
                        if (System.currentTimeMillis() - start > 12000) {
                            Platform.runLater(failed);
                            throw new RuntimeException("Task finished");
                        }
                    } else if (connected && (s == null || s.isEmpty())) {
                        disconnect();
                        throw new RuntimeException("Task finished");
                    }
                } catch (IOException e) {
                    Logger.getLogger(PasswordChanger.class.getName()).log(Level.SEVERE, null, e);
                }
            }, 0, 1, TimeUnit.SECONDS);
        } catch (IOException ex) {
            Logger.getLogger(PasswordChanger.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Stops service.
     */
    public void disconnect() {
        ses.shutdownNow();
        SEEK_PROCESS_EXECUTOR.destroy();
        SEEK_PROCESS_EXECUTOR.executeProcess("killall", "-9", "x11vnc");
    }

    /**
     * Stops the service and shuts the executor down.
     */
    public void exit() {
        disconnect();
        executor.shutdown();
    }
}
