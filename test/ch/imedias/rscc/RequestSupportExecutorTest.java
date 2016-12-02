package ch.imedias.rscc;

import static org.mockito.Mockito.*;

import java.util.concurrent.Semaphore;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import ch.imedias.rscc.util.ProcessExecutor;
import ch.imedias.rscc.util.ProcessExecutorFactory;
import javafx.application.Application;
import javafx.application.Platform;
import ch.imedias.rscc.model.SupportAddress;
import ch.imedias.rscc.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
public class RequestSupportExecutorTest {

//    class ProcessExecutorFactoryMock extends ProcessExecutorFactory {
//        @Override
//        public ProcessExecutor makeProcessExecutor() {
//            return mock(ProcessExecutor.class);
//        }
//    }
    ProcessExecutorFactory factory;
    ProcessExecutor pe;
    RequestSupportExecutor rse;
    String testValue;
    
    @BeforeClass
    public static void initJFX() throws InterruptedException {
        Thread t = new Thread("JavaFX Init Thread") {
            public void run() {
                Application.launch(RemoteSupportApplication.class, new String[0]);
            }
        };
        t.setDaemon(true);
        t.start();
    }
    
    @Before
    public void setUp() throws Exception {
        factory = mock(ProcessExecutorFactory.class);
        pe = mock(ProcessExecutor.class);
        when(factory.makeProcessExecutor()).thenReturn(pe);
        
        ExecutorService executor= Executors.newCachedThreadPool();
        
        rse = new RequestSupportExecutor(factory, executor, () -> testValue = "success", () -> testValue = "fail");
        testValue = "default";
    }

    @Test
    public void testAddPropertyChangeListener() {
        rse.connect(new SupportAddress("foo", "bar", false), 6.0);
        verify(pe).addPropertyChangeListener(any());
    }
    
    @Test
    public void testConnect() throws InterruptedException {
        rse.connect(new SupportAddress("foo", "bar", false), 6.0);
        waitForRunLater();
        verify(pe).executeProcess(anyBoolean(), anyBoolean(), anyVararg());
        System.out.println(testValue);
    }
    
    public static void waitForRunLater() throws InterruptedException {
        Semaphore semaphore = new Semaphore(0);
        Platform.runLater(() -> semaphore.release());
        semaphore.acquire();
    }

}
