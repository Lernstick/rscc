package ch.imedias.rscc.util;

public class ProcessExecutorFactory {
    public ProcessExecutor makeProcessExecutor() {
        System.out.println("hallo");
        return new ProcessExecutor();
    }
}
