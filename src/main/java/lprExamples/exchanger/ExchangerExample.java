package lprExamples.exchanger;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Exchanger;
import lprExamples.executorService.ExecutorServiceWrapper;

public class ExchangerExample {
    public static void main(String[] args) {
        String[] p1 = new String[]{"{посылка A->D}", "{посылка A->C}"};
        String[] p2 = new String[]{"{посылка B->C}", "{посылка B->D}"};
        Exchanger<String> exchanger = new Exchanger<>();
        List<Truck> trucks = Arrays.asList(new Truck(1, "A", "D", p1, exchanger),
                                           new Truck(2, "B", "C", p2, exchanger));
        try (ExecutorServiceWrapper<Truck> serviceWrapper = new ExecutorServiceWrapper<>(trucks, trucks.size())) {
            serviceWrapper.runAllTasks(100);
        }
    }
}

class Truck implements Runnable {
    private int number;
    private String dep;
    private String dest;
    private String[] parcels;
    private Exchanger<String> exchanger;

    public Truck(int number, String departure, String destination, String[] parcels, Exchanger<String> exchanger) {
        this.number = number;
        this.dep = departure;
        this.dest = destination;
        this.parcels = parcels;
        this.exchanger = exchanger;
    }

    @Override
    public void run() {
        try {
            System.out.println("В грузовик " + number + " погрузили: " + parcels[0] + ", " + parcels[1]);
            System.out.println("Грузовик " + number + " выехал из пункта " + dep + " в пункт " + dest);
            Thread.sleep(1000 + (long) (Math.random() * 5000));
            System.out.println("Грузовик " + number + " приехал в пункт Е");
            parcels[1] = exchanger.exchange(parcels[1]);
            System.out.println("В грузовик " + number + " переместили посылку для пункта " + dest);
            Thread.sleep(1000 + (long) (Math.random() * 5000));
            System.out.println(
                    "Грузовик " + number + " приехал в " + dest + " и доставил: " + parcels[0] + ", " + parcels[1]);
        } catch (InterruptedException e) {
            System.out.println("Something went wrong");
        }
    }
}