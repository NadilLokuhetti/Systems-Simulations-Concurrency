import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a bank account with an ID, balance, and transaction history.
 * Provides thread-safe operations for depositing, withdrawing, and accessing account details.
 */
public class BankAccount {
    private final int id; // Unique identifier for the bank account
    private double balance; // Current balance of the account
    private final List<Transaction> transactionHistory = new ArrayList<>(); // List of transactions for the account
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock(true); // ReadWrite lock for thread safety

    /**
     * Constructs a new BankAccount with the specified ID and initial balance.
     *
     * @param id      the unique identifier for the account
     * @param balance the initial balance of the account
     */
    public BankAccount(int id, double balance) {
        this.id = id;
        this.balance = balance;
    }

    /**
     * Returns the unique ID of the account.
     *
     * @return the account ID
     */
    public int getId() {
        return id;
    }

    /**
     * Returns the current balance of the account.
     * This method is thread-safe and uses a read lock.
     *
     * @return the current balance
     */
    public double getBalance() {
        lock.readLock().lock();
        try {
            return balance;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Deposits the specified amount into the account.
     * This method is thread-safe and uses a write lock.
     *
     * @param amount the amount to deposit
     */
    public void deposit(double amount) {
        lock.writeLock().lock();
        try {
            this.balance += amount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Withdraws the specified amount from the account.
     * This method is thread-safe and uses a write lock.
     *
     * @param amount the amount to withdraw
     */
    public void withdraw(double amount) {
        lock.writeLock().lock();
        try {
            this.balance -= amount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Returns a copy of the transaction history for the account.
     * This method is thread-safe and uses a read lock.
     *
     * @return a list of transactions
     */
    public List<Transaction> getTransactionHistory() {
        lock.readLock().lock();
        try {
            return new ArrayList<>(transactionHistory);
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Adds a transaction to the account's transaction history.
     * This method is thread-safe and uses a write lock.
     *
     * @param transaction the transaction to add
     */
    protected void addTransaction(Transaction transaction) {
        lock.writeLock().lock();
        try {
            transactionHistory.add(transaction);
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Locks the account for exclusive write access.
     * This method is used for external synchronization.
     */
    public void lock() {
        lock.writeLock().lock();
    }

    /**
     * Unlocks the account after exclusive write access.
     * This method is used for external synchronization.
     */
    public void unlock() {
        lock.writeLock().unlock();
    }
}