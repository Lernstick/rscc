/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ch.imedias.rscc;

import ch.imedias.rscc.util.ProcessExecutor;
import ch.imedias.rscc.util.ProcessExecutorFactory;
import ch.imedias.rscc.util.ProvideSupportExecutor;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;
/**
 *
 * @author user
 */
public class ProvideSupportExecutorTest {
    
    ProvideSupportExecutor pse;
    ProcessExecutor executor;
    ProcessExecutorFactory factory;
    
    @Before
    public void setUp(){
   
        factory = mock(ProcessExecutorFactory.class);
        executor = mock(ProcessExecutor.class);
        
        when(factory.makeProcessExecutor()).thenReturn(executor);
        when(executor.executeProcess()).thenReturn(1);
        
        pse = new ProvideSupportExecutor(factory);
    }
    
    @Test
    public void testStartOfferWithSSH(){
        pse.startOffer(1.0, 1.0, true, true);
        verify(executor, atLeast(0)).executeProcess(any());
    }
    
    @Test
    public void testStartOfferWithOutSSH(){
        pse.startOffer(1.0, 1.0, true, false);
        verify(executor, atLeast(0)).executeProcess(any());
    }   
    
    @Test
    public void testStopOffer(){
        pse.stopOffer();
        verify(executor, atLeast(1)).destroy();
        verify(executor).executeProcess("killall", "-9", "stunnel4");
    }
}
