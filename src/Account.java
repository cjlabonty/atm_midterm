public class Account {
    private final int id;
    private final String login;
    private final String pin;
    private final String name;
    private double balance;
    private final String status;

    public Account(int id, String login, String pin, String name, double balance, String status) {
        this.id = id;
        this.login = login;
        this.pin = pin;
        this.name = name;
        this.balance = balance;
        this.status = status;
    }

    public String getName() { return name; }
    public int getID() { return id; }
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }

    public Account withUpdatedBalance(double newBalance) {
        return new Account(this.id, this.login, this.pin, this.name, newBalance, this.status);
    }
}
