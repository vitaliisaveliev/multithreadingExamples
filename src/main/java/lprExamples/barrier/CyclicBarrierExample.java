package lprExamples.barrier;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CyclicBarrier;
import lprExamples.executorService.ExecutorServiceWrapper;

public class CyclicBarrierExample {
    public static void main(String[] args) {
        CyclicBarrier barrier = new CyclicBarrier(3, new FerryBoat());
        List<Car> cars = Arrays.asList(new Car("BMW", barrier),
                                       new Car("Audi", barrier),
                                       new Car("Porsche", barrier),
                                       new Car("Lamborghini", barrier),
                                       new Car("Mercedes-Benz", barrier),
                                       new Car("Suzuki", barrier),
                                       new Car("Nissan", barrier),
                                       new Car("Toyota", barrier),
                                       new Car("Kia", barrier));
        try (ExecutorServiceWrapper<Car> serviceWrapper = new ExecutorServiceWrapper<>(cars, cars.size())) {
            serviceWrapper.runAllTasks(1000);
        }
    }
}

class FerryBoat implements Runnable {

    @Override
    public void run() {
        try {
            Thread.sleep(500);
            System.out.println("Паром переправил автомобили!");
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }
    }
}

class Car implements Runnable {

    private String carModel;
    private CyclicBarrier barrier;

    public Car(String carModel, CyclicBarrier barrier) {
        this.carModel = carModel;
        this.barrier = barrier;
    }

    @Override
    public void run() {
        System.out.println("Автомобиль " + carModel + " подъехал к паромной переправе.");
        try {
            barrier.await();
            System.out.println("Автомобиль " + carModel + " продолжил движение.");
        } catch (InterruptedException | BrokenBarrierException e) {
            System.out.println("Something went wrong");
        }
    }
}