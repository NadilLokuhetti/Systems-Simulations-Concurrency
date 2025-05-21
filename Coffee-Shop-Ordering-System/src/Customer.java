import util.LoggerUtil;

import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Customer implements Runnable {
    // The coffee shop where the customer places orders
    private final CoffeeShop coffeeShop;

    /**
     * Constructs a Customer that places orders in the specified coffee shop.
     *
     * @param coffeeShop the coffee shop where the customer places orders
     */
    public Customer(CoffeeShop coffeeShop) {
        this.coffeeShop = coffeeShop;
    }

    /**
     * The main logic for the customer thread. Places multiple orders with random
     * delays between them. If interrupted, the thread exits gracefully.
     */
    @Override
    public void run() {
        try {
            // Each customer places 5 orders
            for (int i = 1; i <= 5; i++) {
                // Create a unique order string for each iteration
                String order = Thread.currentThread().getName() + "'s order " + i;

                // Place the order in the coffee shop's order queue
                coffeeShop.placeOrder(order);

                // Log the order being placed
                LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), order);

                // Simulate a random delay between placing orders (1 to 2 seconds)
                Thread.sleep(ThreadLocalRandom.current().nextInt(1000, 2000));
            }
        } catch (InterruptedException e) {
            // Handle thread interruption gracefully
            Thread.currentThread().interrupt();

            // Notify that the thread was interrupted
            System.out.println(Thread.currentThread().getName() + " interrupted.");
        }
    }
}