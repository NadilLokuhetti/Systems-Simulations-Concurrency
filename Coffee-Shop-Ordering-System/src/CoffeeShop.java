import util.LoggerUtil;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

public class CoffeeShop {
    // A thread-safe queue to hold customer orders with a maximum capacity
    private final LinkedBlockingQueue<String> orderList;

    // A flag to indicate whether the coffee shop is open or closed
    private volatile boolean isOpen = true;

    /**
     * Constructs a CoffeeShop with a specified maximum capacity for orders.
     *
     * @param maxCapacity the maximum number of orders the coffee shop can hold
     */
    public CoffeeShop(int maxCapacity) {
        this.orderList = new LinkedBlockingQueue<>(maxCapacity);
    }

    /**
     * Places an order into the queue. If the queue is full, this method blocks
     * until space becomes available.
     *
     * @param order the order to be placed
     */
    public void placeOrder(String order) {
        try {
            orderList.put(order); // Blocks if the queue is full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LoggerUtil.log(Level.WARNING, Thread.currentThread().getName(),
                    "Thread interrupted while placing order: " + e.getMessage());
        }
    }

    /**
     * Prepares an order by retrieving it from the queue. If the queue is empty,
     * this method waits until an order is available or the shop is closed and
     * no more orders are expected.
     *
     * @return the next order to be prepared, or null if the shop is closed and
     *         no more orders are available
     */
    public String prepareOrder() {
        while (true) {
            // Wait for an order or return null if the shop is closed and the queue is empty
            String order = orderList.poll();
            if (order != null) {
                return order;
            }
            if (!isOpen && orderList.isEmpty()) {
                return null; // No more orders to process
            }
        }
    }

    /**
     * Closes the coffee shop, indicating that no new orders will be accepted.
     */
    public void close() {
        isOpen = false;
    }

    /**
     * Checks if the coffee shop is open.
     *
     * @return true if the coffee shop is open, false otherwise
     */
    public boolean isOpen() {
        return isOpen;
    }
}