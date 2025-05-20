import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class Customer implements Runnable {
    private final CoffeeShop coffeeShop;

    public Customer(CoffeeShop coffeeShop) {
        this.coffeeShop = coffeeShop;
    }

    @Override
    public void run() {
        try {
            for (int i = 1; i <= 6; i++) {
                String order = Thread.currentThread().getName() + "'s order " + i;
                System.out.println("--------------------------------"+ order +"----------------------------------");
//                System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) + " - Order placed by: " + order);
                LoggerUtil.log(Level.INFO,Thread.currentThread().getName(), "- Order placed "+ order);
                coffeeShop.placeOrder(order);
                Thread.sleep(ThreadLocalRandom.current().nextInt(2000, 4000)); // Simulate time between orders
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println(Thread.currentThread().getName() + " interrupted.");
        }
    }
}
