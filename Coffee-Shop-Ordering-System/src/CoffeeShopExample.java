public class CoffeeShopExample {
    public static void main(String[] args) {
        // Define the maximum capacity of the order queue in the coffee shop
        final int Queue_cap = 4;

        // Create a CoffeeShop instance with the specified queue capacity
        CoffeeShop coffeeShop = new CoffeeShop(Queue_cap);

        // Define the number of customers and baristas
        final int No_customer = 4;
        final int No_barista = 3;

        // Create and start customer threads
        Thread[] customers = new Thread[No_customer];
        for (int i = 0; i < customers.length; i++) {
            // Create a new thread for each customer, passing the coffee shop instance
            customers[i] = new Thread(new Customer(coffeeShop), "Customer " + (i + 1));
        }

        // Create and start barista threads
        Thread[] baristas = new Thread[No_barista];
        for (int i = 0; i < baristas.length; i++) {
            // Create a new thread for each barista, passing the coffee shop instance
            baristas[i] = new Thread(new Barista(coffeeShop), "Barista " + (i + 1));
        }

        // Start all customer threads
        for (Thread customer : customers) {
            customer.start();
        }

        // Start all barista threads
        for (Thread barista : baristas) {
            barista.start();
        }

        // Wait for all customer threads to finish
        for (Thread customer : customers) {
            try {
                customer.join(); // Wait for the customer thread to complete
            } catch (InterruptedException e) {
                // Handle interruption while waiting for customers to finish
                System.out.println("Main thread interrupted while waiting for customers to finish.");
                Thread.currentThread().interrupt(); // Restore the interrupted status
            }
        }

        // Close the coffee shop after all customers have placed their orders
        coffeeShop.close();

        // Wait for all barista threads to finish
        for (Thread barista : baristas) {
            try {
                barista.join(); // Wait for the barista thread to complete
            } catch (InterruptedException e) {
                // Handle interruption while waiting for baristas to finish
                System.out.println("Main thread interrupted while waiting for baristas to finish.");
                Thread.currentThread().interrupt(); // Restore the interrupted status
            }
        }

        // Add a short delay to ensure all threads have completed their tasks
        try {
            Thread.sleep(1000); // Sleep for 1 second
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt(); // Handle interruption during sleep
        }

        // Print a message indicating that all customers have been served
        System.out.println("All customers have been served. Exiting...");
    }
}