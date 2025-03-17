public class Account {
    private int id;
    private String login;
    private int pin;
    private String name;
    private double balance;
    private String status;

    public Account(int id, String login, int pin, String name, double balance, String status) {
        this.login = login;
        this.pin = pin;
        this.name = name;
        this.balance = balance;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public String getLogin() {
        return login;
    }

    public void setLogin(String login) {
        this.login = login;
    }

    public int getPin() {
        return pin;
    }

    public void setPin(int pin) {
        this.pin = pin;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
