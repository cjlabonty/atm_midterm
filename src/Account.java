public class Account {
    public int id;
    public String login;
    public String pin;
    public String name;
    public double balance;
    public String status;

    public Account(int id, String login, String pin, String name, double balance, String status) {
        this.id = id;
        this.login = login;
        this.pin = pin;
        this.name = name;
        this.balance = balance;
        this.status = status;
    }
}
