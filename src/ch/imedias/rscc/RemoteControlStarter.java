/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc;

import ch.imedias.rscc.utils.ProcessExecutor;
import java.awt.EventQueue;

public class RemoteControlStarter {
    
    private final static ProcessExecutor SEEK_PROCESS_EXECUTOR =
            new ProcessExecutor();

    
    public static void main(String[] args){
        EventQueue.invokeLater(new Runnable() {

            @Override
            public void run() {
                RemoteSupportFrame frame = new RemoteSupportFrame();
                RemoteSupportController controller = new RemoteSupportController(frame);
                SEEK_PROCESS_EXECUTOR.addPropertyChangeListener(frame);
                controller.initialize(null, null);
                frame.setVisible(true);
            }
        });
    }
}
