import util.LoggerUtil;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Barista implements Runnable {
    // The coffee shop where the barista works
    private final CoffeeShop coffeeShop;

    /**
     * Constructs a Barista that works in the specified coffee shop.
     *
     * @param coffeeShop the coffee shop where the barista prepares orders
     */
    public Barista(CoffeeShop coffeeShop) {
        this.coffeeShop = coffeeShop;
    }

    /**
     * The main logic for the barista thread. Continuously prepares orders
     * until the coffee shop is closed and no more orders are available,
     * or the thread is interrupted.
     */
    @Override
    public void run() {
        // Keep running while the coffee shop is open or the thread is not interrupted
        while (coffeeShop.isOpen() || !Thread.currentThread().isInterrupted()) {
            try {
                // Retrieve the next order from the coffee shop
                String order = coffeeShop.prepareOrder();
                if (order == null) {
                    // Exit the loop if there are no more orders and the shop is closed
                    break;
                }

                // Log the order being prepared
                LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), " Preparing order for " + order);

                // Simulate the time taken to prepare the order (random delay between 1 and 2 seconds)
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
            } catch (InterruptedException e) {
                // Handle thread interruption gracefully
                Thread.currentThread().interrupt();

                // Log the interruption and exit the loop
                System.out.printf("%s - [INFO] %s interrupted. Exiting...%n",
                        LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")),
                        Thread.currentThread().getName());
                break;
            }
        }
    }
}