/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author root
 */


public class PasswordChanger {
    
    private static final int PW_LENGTH = 8;
    private static final String ACC = "rscc_ssh";
        
    public static String setRandomPassword() {
        String pw = generateNumberedPassword(PW_LENGTH);
        
        // Access terminal
            ProcessExecutorFactory factory = new ProcessExecutorFactory();
            ProcessExecutor processExecutor = factory.makeProcessExecutor();
            try {
                String pwChange = String.format("yes %1$s | passwd %2$s", pw, ACC);
                processExecutor.executeScript(true, true, processExecutor.createScript(pwChange).getAbsolutePath());
            } catch (IOException ex) {
                Logger.getLogger(PasswordChanger.class.getName()).log(Level.SEVERE, null, ex);
            }
        return pw;
    }
    
    private static String generateNumberedPassword(int length) {
        StringBuilder sb = new StringBuilder(length);
        try {
            SecureRandom sr = SecureRandom.getInstanceStrong();
            for(int i = 0; i < length; i++) {
                sb.append(sr.nextInt(10));
            }
            
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(PasswordChanger.class.getName()).log(Level.SEVERE, null, ex);
        }
        return sb.toString();
    }
}
