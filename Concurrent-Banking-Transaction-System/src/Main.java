import util.LoggerUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * The main class to simulate bank account transactions using the TransactionSystem.
 * Demonstrates concurrent transfers and account balance checks.
 */
public class Main {
    public static void main(String[] args) {
        // Create a list of bank accounts with initial balances
        List<BankAccount> accountList = new ArrayList<>();
        accountList.add(new BankAccount(1, 1000)); // Account 1 with $1000
        accountList.add(new BankAccount(2, 2000)); // Account 2 with $2000
        accountList.add(new BankAccount(3, 3000)); // Account 3 with $3000

        // Initialize the TransactionSystem with the list of accounts
        TransactionSystem transactionSystem = new TransactionSystem(accountList);

        // Simulate concurrent transactions using threads
        Thread t1 = new Thread(() -> transactionSystem.transfer(1, 2, 100)); // Transfer $100 from Account 1 to Account 2
        Thread t2 = new Thread(() -> transactionSystem.transfer(2, 3, 200)); // Transfer $200 from Account 2 to Account 3
        Thread t3 = new Thread(() -> transactionSystem.transfer(3, 1, 50));  // Transfer $50 from Account 3 to Account 1
        Thread t4 = new Thread(() -> transactionSystem.transfer(1, 1, 150)); // Invalid transfer: Same account
        Thread t5 = new Thread(() -> transactionSystem.transfer(3, 1, 50000)); // Invalid transfer: Insufficient balance

        // Thread to check balances of Account 1 and Account 3
        Thread t6 = new Thread(() -> {
            LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), "Balance of account 1: " + accountList.get(0).getBalance());
            LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), "Balance of account 3: " + accountList.get(2).getBalance());
        });

        // Start all transaction threads
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();

        // Wait for the main transaction threads to complete
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            // Handle thread interruption
            Thread.currentThread().interrupt();
        }

        // Start the balance-checking thread
        t6.start();

        // Wait for a few seconds to ensure all operations are complete
        try {
            Thread.sleep(3000); // Sleep for 3 seconds
        } catch (InterruptedException e) {
            // Handle thread interruption
            Thread.currentThread().interrupt();
        }

        // Print the final balances of all accounts
        transactionSystem.printAccountBalances();

        // Retrieve and print the transaction history for all accounts
        transactionSystem.getTransactionHistory();
    }
}