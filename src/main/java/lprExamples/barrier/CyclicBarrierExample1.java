package lprExamples.barrier;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.List;
import java.util.Random;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lprExamples.executorService.ExecutorServiceWrapper;

public class CyclicBarrierExample1 {
    public static void main(String[] args) {
        StringBuffer stringBuffer = new StringBuffer();
        CyclicBarrier barrier = new CyclicBarrier(3,
                                                  () -> System.out.println("Строки были сгенерированы, добавляем..."));
        List<WriteStringWorker> writeStringWorkers = Stream.generate(() -> new WriteStringWorker(barrier, stringBuffer))
                                                           .limit(9).collect(Collectors.toList());
        try (ExecutorServiceWrapper<WriteStringWorker> serviceWrapper = new ExecutorServiceWrapper<>(writeStringWorkers,
                                                                                                     writeStringWorkers.size())) {
            serviceWrapper.runAllTasks(500);
        }
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("result.txt"))) {
            Thread.sleep(1000);
            System.out.println("Результирующая строка готова: " + stringBuffer.toString() + ", пишем её в файл...");
            writer.write(stringBuffer.toString());
        } catch (IOException | InterruptedException e) {
            System.out.println("Something went wrong");
        }
    }
}

class WriteStringWorker implements Runnable {

    private CyclicBarrier barrier;
    private StringBuffer stringBuffer;

    public WriteStringWorker(CyclicBarrier barrier, StringBuffer stringBuffer) {
        this.barrier = barrier;
        this.stringBuffer = stringBuffer;
    }

    @Override
    public void run() {
        try {
            Thread.sleep(500);
            Random random = new Random();
            String generatedString = random.ints(97, 122 + 1)
                                           .limit(10)
                                           .collect(StringBuilder::new, StringBuilder::appendCodePoint,
                                                    StringBuilder::append)
                                           .toString();
            String name = Thread.currentThread().getName();
            System.out.println(name + " сгенерировал строку: " + generatedString);
            barrier.await();
            stringBuffer.append(generatedString);
            System.out.println(name + " добавил строку к общей");
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Something went wrong");
        }
    }
}