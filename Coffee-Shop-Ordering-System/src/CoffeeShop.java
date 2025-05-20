import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Level;

public class CoffeeShop {
    private final LinkedBlockingQueue<String> orderList;
    private volatile boolean isOpen = true;

    public CoffeeShop(int maxCapacity) {
        this.orderList = new LinkedBlockingQueue<>(maxCapacity);
    }

    public void placeOrder(String order) {
        try {
            orderList.put(order); // Blocks if the queue is full
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            LoggerUtil.log(Level.WARNING, Thread.currentThread().getName(),
                    "Thread interrupted while placing order: " + e.getMessage());
        }

    }

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

    public void close() {
        isOpen = false;
    }

    public boolean isOpen() {
        return isOpen;
    }
}
