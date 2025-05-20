public class CoffeeShopExample {
    public static void main(String[] args) {
        final int Queue_cap = 4;
        CoffeeShop coffeeShop = new CoffeeShop(Queue_cap);

        // Create and start customer threads
        Thread[] customers = new Thread[5];
        for (int i = 0; i < customers.length; i++) {
            customers[i] = new Thread(new Customer(coffeeShop), "Customer " + (i + 1));
            customers[i].start();
        }

        // Create and start barista threads
        Thread[] baristas = new Thread[5];
        for (int i = 0; i < baristas.length; i++) {
            baristas[i] = new Thread(new Barista(coffeeShop), "Barista " + (i + 1));
            baristas[i].start();
        }

        // Wait for all customer threads to finish
        for (Thread customer : customers) {
            try {
                customer.join();
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted while waiting for customers to finish.");
                Thread.currentThread().interrupt();
            }
        }

        // Close the coffee shop after all customers are served
        coffeeShop.close();

        // Wait for all barista threads to finish
        for (Thread barista : baristas) {
            try {
                barista.join();
            } catch (InterruptedException e) {
                System.out.println("Main thread interrupted while waiting for baristas to finish.");
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("All customers have been served. Exiting...");
    }
}
