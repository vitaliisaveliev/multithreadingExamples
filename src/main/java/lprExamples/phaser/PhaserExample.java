package lprExamples.phaser;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Phaser;

public class PhaserExample {
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executorService = Executors.newFixedThreadPool(7);
        Phaser phaser = new Phaser(1);
        List<Passenger> passengers = Arrays.asList(new Passenger(1, 2, phaser),
                                                   new Passenger(1, 5, phaser),
                                                   new Passenger(2, 3, phaser),
                                                   new Passenger(2, 5, phaser),
                                                   new Passenger(3, 4, phaser),
                                                   new Passenger(4, 5, phaser),
                                                   new Passenger(4, 5, phaser));
        for (int i = 0; i < 7; i++) {
            switch (i) {
                case 0:
                    System.out.println("Автобус выехал из парка");
                    Thread.sleep(1000);
                    phaser.arrive();
                    break;
                case 6:
                    System.out.println("Автобус уехал в парк");
                    phaser.arriveAndDeregister();
                    break;
                default:
                    int currentBusStop = phaser.getPhase();
                    System.out.println("Остановка №" + currentBusStop);
                    passengers.forEach(passenger -> {
                        if (passenger.getDeparture() == currentBusStop) {
                            phaser.register();
                            executorService.submit(passenger);
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    phaser.arriveAndAwaitAdvance();
            }
        }
        executorService.shutdown();
    }
}

class Passenger implements Runnable {

    private int departure;
    private int destination;
    private Phaser phaser;

    public Passenger(int departure, int destination, Phaser phaser) {
        this.departure = departure;
        this.destination = destination;
        this.phaser = phaser;
        System.out.println(this + " ждёт на остановке № " + this.departure);
    }

    public int getDeparture() {
        return departure;
    }

    @Override
    public void run() {
        try {
            System.out.println(this + " сел в автобус");
            while (phaser.getPhase() < destination) {
                phaser.arriveAndAwaitAdvance();
            }
            Thread.sleep(100);
            System.out.println(this + " покинул автобус");
            phaser.arriveAndDeregister();
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }
    }

    @Override
    public String toString() {
        return "Пассажир{" + departure + " -> " + destination + '}';
    }
}