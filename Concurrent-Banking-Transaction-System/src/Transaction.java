/**
 * Represents a financial transaction between two bank accounts.
 * Stores the source account ID, destination account ID, and the transaction amount.
 */
public class Transaction {
    private final int fromAccountId; // ID of the account from which the amount is transferred
    private final int toAccountId;   // ID of the account to which the amount is transferred
    private final double amount;     // The amount of money transferred

    /**
     * Constructs a new Transaction with the specified details.
     *
     * @param fromAccountId the ID of the source account
     * @param toAccountId   the ID of the destination account
     * @param amount        the amount of money to transfer
     */
    public Transaction(int fromAccountId, int toAccountId, double amount) {
        this.fromAccountId = fromAccountId;
        this.toAccountId = toAccountId;
        this.amount = amount;
    }

    /**
     * Returns a string representation of the transaction in a human-readable format.
     *
     * @return a formatted string containing the transaction details
     */
    @Override
    public String toString() {
        return String.format(
                "Transaction \n" +
                        "  From Account: %d\n" + // Source account ID
                        "  To Account: %d\n" +   // Destination account ID
                        "  Amount: $%.2f\n" +// Transaction amount formatted to 2 decimal places
                "-----------------------------------"
                , fromAccountId, toAccountId, amount);
    }
}