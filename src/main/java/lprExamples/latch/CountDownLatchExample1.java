package lprExamples.latch;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import lprExamples.executorService.ExecutorServiceWrapper;

public class CountDownLatchExample1 {
    public static void main(String[] args) throws InterruptedException {
        CountDownLatch latch = new CountDownLatch(8);
        int trackLength = 500000;
        List<SportCar> sportCars = Arrays.asList(new SportCar("BMW", 240, trackLength, latch),
                                                 new SportCar("Audi", 230, trackLength, latch),
                                                 new SportCar("Porsche", 250, trackLength, latch),
                                                 new SportCar("Lamborghini", 300, trackLength, latch),
                                                 new SportCar("Mercedes-Benz", 220, trackLength, latch));
        try (ExecutorServiceWrapper<SportCar> serviceWrapper = new ExecutorServiceWrapper<>(sportCars,
                                                                                            sportCars.size())) {
            serviceWrapper.runAllTasks(1000);
        }
        while (latch.getCount() > 3) {
            Thread.sleep(100);
        }
        System.out.println("Все автомобили собрались на прямой");
        Thread.sleep(1000);
        System.out.println("На старт!");
        latch.countDown();
        Thread.sleep(1000);
        System.out.println("Внимание!");
        latch.countDown();
        Thread.sleep(1000);
        System.out.println("Марш!");
        latch.countDown();
    }
}

class SportCar implements Runnable {

    private String carModel;
    private int carSpeed;
    private int trackLength;
    private CountDownLatch latch;

    public SportCar(String carModel, int carSpeed, int trackLength, CountDownLatch latch) {
        this.carModel = carModel;
        this.carSpeed = carSpeed;
        this.trackLength = trackLength;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            System.out.println("Автомобиль " + carModel + " подъехал к стартовой прямой.");
            latch.countDown();
            latch.await();
            Thread.sleep(trackLength / carSpeed);
            System.out.println("Автомобиль " + carModel + " финишировал.");
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }
    }
}
