package lprExamples.semaphore;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Semaphore;
import lprExamples.executorService.ExecutorServiceWrapper;

public class SemaphoreExample {
    public static void main(String[] args) throws Exception {
        Semaphore semaphore = new Semaphore(2);
        List<Philosopher> philosophers = Arrays.asList(new Philosopher(semaphore, "Сократ"),
                                                       new Philosopher(semaphore, "Платон"),
                                                       new Philosopher(semaphore, "Аристотель"),
                                                       new Philosopher(semaphore, "Фалес"),
                                                       new Philosopher(semaphore, "Пифагор"));
        try (ExecutorServiceWrapper<Philosopher> serviceWrapper = new ExecutorServiceWrapper<>(philosophers,
                                                                                               philosophers.size())) {
            serviceWrapper.runAllTasks(0);
        }
    }
}

class Philosopher implements Runnable {

    private Semaphore semaphore;
    private String name;
    private boolean full = false;

    public Philosopher(Semaphore semaphore, String name) {
        this.semaphore = semaphore;
        this.name = name;
    }

    @Override
    public void run() {
        try {
            if (!full) {
                semaphore.acquire();
                System.out.println(name + " садится за стол");
                eat();
                System.out.println(name + " поел. Он выходит из-за стола");
            }
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        } finally {
            semaphore.release();
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                System.out.println("Something went wrong");
            }
        }
    }

    private void eat() throws InterruptedException {
        Thread.sleep(300);
        full = true;
    }
}