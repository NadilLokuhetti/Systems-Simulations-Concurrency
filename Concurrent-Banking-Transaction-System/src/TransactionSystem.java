import util.LoggerUtil;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

/**
 * Manages a system of bank accounts and facilitates transactions between them.
 * Provides thread-safe transfer operations and transaction history management.
 */
public class TransactionSystem {
    // Map to store all bank accounts, keyed by their unique ID
    private final Map<Integer, BankAccount> accounts = new HashMap<>();

    /**
     * Initializes the TransactionSystem with a list of bank accounts.
     *
     * @param accountList the list of bank accounts to add to the system
     */
    public TransactionSystem(List<BankAccount> accountList) {
        for (BankAccount account : accountList) {
            accounts.put(account.getId(), account); // Add each account to the map
        }
    }

    /**
     * Transfers a specified amount from one account to another.
     * Ensures thread safety by locking accounts in a consistent order to avoid deadlocks.
     *
     * @param fromAccountId the ID of the account to transfer from
     * @param toAccountId   the ID of the account to transfer to
     * @param amount        the amount to transfer
     * @return true if the transfer is successful, false otherwise
     */
    public boolean transfer(int fromAccountId, int toAccountId, double amount) {
        // Validate the transfer amount and account IDs
        if (amount <= 0 || fromAccountId == toAccountId) {
            LoggerUtil.log(Level.WARNING, Thread.currentThread().getName(), "Transfer failed: Invalid transfer amount or identical account IDs.");
            return false;
        }

        // Lock accounts in a consistent order (smaller ID first) to avoid deadlocks
        BankAccount firstAccount = accounts.get(Math.min(fromAccountId, toAccountId));
        BankAccount secondAccount = accounts.get(Math.max(fromAccountId, toAccountId));

        // Check if both accounts exist
        if (firstAccount == null || secondAccount == null) {
            LoggerUtil.log(Level.WARNING, Thread.currentThread().getName(), "Transfer failed: One or both accounts do not exist.");
            return false;
        }

        LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), "Initiating transfer of " + amount + " from Account " + fromAccountId + " to Account " + toAccountId);

        // Lock the first account
        firstAccount.lock();
        try {
            // Lock the second account
            secondAccount.lock();
            try {
                // Execute the transfer and return the result
                if (executeTransfer(fromAccountId, toAccountId, amount)) {
                    return true;
                } else {
                    LoggerUtil.log(Level.WARNING, Thread.currentThread().getName(), "Transfer failed: Insufficient balance in Account " + fromAccountId);
                    return false;
                }
            } catch (Exception e) {
                // Log errors and attempt to reverse the transaction
                LoggerUtil.log(Level.SEVERE, Thread.currentThread().getName(), "Error during transfer: " + amount + " from Account " + fromAccountId + " to Account " + toAccountId + ". Attempting reversal.");
                reverseTransaction(fromAccountId, toAccountId, amount);
                return false;
            } finally {
                // Unlock the second account
                secondAccount.unlock();
            }
        } finally {
            // Unlock the first account
            firstAccount.unlock();
        }
    }

    /**
     * Executes the transfer between two accounts.
     *
     * @param fromAccountId the ID of the account to transfer from
     * @param toAccountId   the ID of the account to transfer to
     * @param amount        the amount to transfer
     * @return true if the transfer is successful, false if the source account has insufficient balance
     */
    private boolean executeTransfer(int fromAccountId, int toAccountId, double amount) {
        BankAccount fromAccount = accounts.get(fromAccountId);
        BankAccount toAccount = accounts.get(toAccountId);

        // Check if the source account has sufficient balance
        if (fromAccount.getBalance() < amount) {
            return false;
        }

        // Create a transaction record
        Transaction transaction = new Transaction(fromAccountId, toAccountId, amount);

        // Perform the transfer
        fromAccount.withdraw(amount);
        toAccount.deposit(amount);

        // Add the transaction to both accounts' histories
        fromAccount.addTransaction(transaction);
        toAccount.addTransaction(transaction);

        return true;
    }

    /**
     * Reverses a transaction by transferring the amount back from the destination account to the source account.
     *
     * @param fromAccountId the ID of the original source account
     * @param toAccountId   the ID of the original destination account
     * @param amount        the amount to reverse
     */
    public void reverseTransaction(int fromAccountId, int toAccountId, double amount) {
        // Validate the reversal request
        if (amount <= 0 || fromAccountId == toAccountId) {
            LoggerUtil.log(Level.WARNING, Thread.currentThread().getName(), "Invalid transaction reversal request.");
            return;
        }

        BankAccount fromAccount = accounts.get(fromAccountId);
        BankAccount toAccount = accounts.get(toAccountId);

        // Check if both accounts exist
        if (fromAccount == null || toAccount == null) {
            LoggerUtil.log(Level.WARNING, Thread.currentThread().getName(), "Invalid account details for reversal.");
            return;
        }

        // Lock accounts in a consistent order (smaller ID first) to avoid deadlocks
        BankAccount firstLock = fromAccountId < toAccountId ? fromAccount : toAccount;
        BankAccount secondLock = fromAccountId < toAccountId ? toAccount : fromAccount;

        // Lock the first account
        firstLock.lock();
        try {
            // Lock the second account
            secondLock.lock();
            try {
                // Check if the destination account has sufficient balance for reversal
                if (toAccount.getBalance() >= amount) {
                    toAccount.withdraw(amount);
                    fromAccount.deposit(amount);
                    LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), "Reversed " + amount + " from Account " + fromAccountId + " to Account " + toAccountId);
                } else {
                    LoggerUtil.log(Level.WARNING, Thread.currentThread().getName(), "Insufficient balance for reversal of " + amount + " from Account " + toAccountId + " to Account " + fromAccountId);
                }
            } finally {
                // Unlock the second account
                secondLock.unlock();
            }
        } finally {
            // Unlock the first account
            firstLock.unlock();
        }
    }

    /**
     * Prints the balances of all accounts in the system.
     */
    public void printAccountBalances() {
        for (BankAccount account : accounts.values()) {
            LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), "Account Balance" + account.getId() + ": " + account.getBalance());
        }
    }

    /**
     * Retrieves and logs the transaction history for all accounts in the system.
     */
    public void getTransactionHistory() {
        // Iterate over all accounts in the system
        for (BankAccount account : accounts.values()) {
            if (account == null) {
                LoggerUtil.log(Level.WARNING, Thread.currentThread().getName(), "Account does not exist.");
                continue; // Skips to the next account if this one is null
            }

            // Gets the transaction history of the current account
            List<Transaction> transactions = account.getTransactionHistory();

            if (transactions.isEmpty()) {
                LoggerUtil.log(Level.INFO, Thread.currentThread().getName(),
                        "No transactions found for Account " + account.getId() + ".");
            } else {
                LoggerUtil.log(Level.INFO, Thread.currentThread().getName(),
                        "Transaction history for Account " + account.getId() + ":");
                for (Transaction transaction : transactions) {
                    LoggerUtil.log(Level.INFO, Thread.currentThread().getName(), transaction.toString());
                }
            }
        }
    }
}