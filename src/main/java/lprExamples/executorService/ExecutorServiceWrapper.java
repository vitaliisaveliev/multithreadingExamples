package lprExamples.executorService;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ExecutorServiceWrapper<T extends Runnable> implements AutoCloseable {

    private ExecutorService executorService;
    private List<T> elements;

    public ExecutorServiceWrapper(List<T> elements, int threadsCount) {
        this.elements = elements;
        executorService = Executors.newFixedThreadPool(threadsCount);
    }

    public void runAllTasks(long timeToSleep) {
        elements.forEach(e -> {
            executorService.submit(e);
            if (timeToSleep > 0) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    throw new RuntimeException("Something went wrong");
                }
            }
        });
    }

    @Override
    public void close() {
        executorService.shutdown();
    }
}
