import util.LoggerUtil;

import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class FloorBathroom {
    // Total number of bathroom stalls
    public static final int NUM_OF_STALLS = 6;

    // Total number of employees
    public static final int NUM_OF_EMPLOYEES = 100;

    // Queue to simulate available bathroom stalls
    public static final Queue<Integer> bathroomStalls = new LinkedList<>();

    // Semaphore to control access to the stalls, initialized with the number of stalls
    public static final Semaphore stall = new Semaphore(NUM_OF_STALLS);

    // Runnable class representing a bathroom user
    private static class BathroomUser implements Runnable {
        @Override
        public void run() {
            try {
                // Acquire a permit from the semaphore to enter a stall
                stall.acquire();

                int stallNo;
                synchronized (bathroomStalls) {
                    // Get the next available stall number from the queue
                    stallNo = bathroomStalls.poll();
                }

                // Log that the user has entered the stall
                LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), " has entered stall " + stallNo, LoggerUtil.GREEN);

                // Simulate the user using the stall for a random time between 3 and 5 seconds
                Thread.sleep(ThreadLocalRandom.current().nextInt(3000, 5000));

                synchronized (bathroomStalls) {
                    // Return the stall number to the queue after use
                    bathroomStalls.add(stallNo);
                }

                // Log that the user has left the stall
                LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), " has left stall " + stallNo,LoggerUtil.YELLOW);

            } catch (InterruptedException e) {
                // Log if the thread is interrupted
                LoggerUtil.log(Level.SEVERE, Thread.currentThread().getName(), " was interrupted.",LoggerUtil.RED);
            } finally {
                // Release the permit back to the semaphore
                stall.release();
            }
        }
    }

    public static void main(String[] args) {
        // Initialize the queue with stall numbers
        for (int i = 1; i <= NUM_OF_STALLS; i++) {
            bathroomStalls.add(i);
        }

        // Create and start threads for each employee/student
        for (int i = 1; i <= NUM_OF_EMPLOYEES; i++) {
            // Alternate between "Student" and "Employee" names
            String name = (i % 2 == 0) ? "Student" : "Employee";
            Thread user = new Thread(new BathroomUser(), name + " " + i);
            user.start();
        }

        // Allow some time for the threads to run
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            // Handle interruption if the main thread is interrupted
            Thread.currentThread().interrupt();
        }
    }
}