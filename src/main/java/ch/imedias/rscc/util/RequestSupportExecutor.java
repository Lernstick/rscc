/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc.util;

import ch.imedias.rscc.model.SupportAddress;

/**
 * Interface for different request support actions eg. ssh or unencrypted vnc
 * 
 * @author sschw
 */
public interface RequestSupportExecutor {
    public void connect(final SupportAddress supportAddress, final Double scale, final String password);
    public void disconnect();
    public void exit();
}
