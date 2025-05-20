import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Barista implements Runnable {
    private final CoffeeShop coffeeShop;

    public Barista(CoffeeShop coffeeShop) {
        this.coffeeShop = coffeeShop;
    }

    @Override
    public void run() {
//        while (coffeeShop.isOpen() || !Thread.currentThread().isInterrupted()) {
        while (coffeeShop.isOpen()) {
            try {
                String order = coffeeShop.prepareOrder();
                if (order == null) {
                    break; // Exit if no more orders and shop is closed
                }
//                System.out.printf("%s - [INFO] %s is preparing: %s%n", LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")), Thread.currentThread().getName(), order);
                LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), " is preparing order: " + order);
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 3000)); // Simulate order preparation time
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.printf("%s - [INFO] %s interrupted. Exiting...%n",
                        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        Thread.currentThread().getName());
                break;
            }
        }
    }
}
