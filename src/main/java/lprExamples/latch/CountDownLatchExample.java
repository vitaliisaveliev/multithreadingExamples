package lprExamples.latch;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lprExamples.executorService.ExecutorServiceWrapper;

public class CountDownLatchExample {
    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(5);
        List<Child> children = Arrays.asList(new Child("Саша", latch), new Child("Настя", latch),
                                             new Child("Серёжа", latch),
                                             new Child("Оля", latch), new Child("Андрей", latch));
        try (ExecutorServiceWrapper<Child> serviceWrapper = new ExecutorServiceWrapper<>(children, children.size())) {
            serviceWrapper.runAllTasks(1000);
        }
    }
}

class Child implements Runnable {

    private String childName;
    private CountDownLatch countDownLatch;

    public Child(String childName, CountDownLatch countDownLatch) {
        this.childName = childName;
        this.countDownLatch = countDownLatch;
    }

    @Override
    public void run() {
        try {
            System.out.println(childName + " вышел на сбор группы...");
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }
        countDownLatch.countDown();
        System.out.println(
                childName + " пришёл на место сбора, осталось ждать " + countDownLatch.getCount() + " детей");
    }
}