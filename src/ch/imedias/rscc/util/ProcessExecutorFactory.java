package ch.imedias.rscc.util;

/**
 * Simple Factory to create new Processexecutors. Mainly used for isolated 
 * testing, so that the factory can be mocked and return mocked ProcessExecutors.
 * @author patric steiner
 */
public class ProcessExecutorFactory {
    
    /**
     * Creates a new ProcessExecutor.
     * @return ProcessExecutor
     */
    public ProcessExecutor makeProcessExecutor() {
        return new ProcessExecutor();
    }
}
