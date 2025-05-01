/**
 * Represents a user account with associated information such as login, PIN, name, balance, and status.
 * Provides methods to retrieve account details, update the balance, and manage account properties.
 */
public class Account {
    private final int id;
    private final String login;
    private final String pin;
    private final String name;
    private double balance;
    private final String status;

    /**
     * Constructs an Account object with the specified details.
     *
     * @param id the account ID
     * @param login the login ID for the account
     * @param pin the PIN code for the account
     * @param name the name of the account holder
     * @param balance the account balance
     * @param status the account status (e.g., "Active", "Suspended")
     */
    public Account(int id, String login, String pin, String name, double balance, String status) {
        this.id = id;
        this.login = login;
        this.pin = pin;
        this.name = name;
        this.balance = balance;
        this.status = status;
    }

    /**
     * Retrieves the name of the account or user.
     *
     * @return the name as a {@code String}.
     */
    public String getName() {
        return name;
    }

    /**
     * Retrieves the id of the account or user.
     *
     * @return the id as a {@code int}
     */
    public int getID() {
        return id;
    }

    /**
     * Retrieves the balance of the account or user.
     *
     * @return the balance as a {@code double}
     */
    public double getBalance() {
        return balance;
    }

    /**
     * Creates and returns a new Account object with an updated balance.
     * This method does not modify the existing Account.
     *
     * @param newBalance the new balance to set
     * @return the updated Account as a {@code Account}
     */
    public Account withUpdatedBalance(double newBalance) {
        return new Account(this.id, this.login, this.pin, this.name, newBalance, this.status);
    }
}
