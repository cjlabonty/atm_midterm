import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean admin = true;

        System.out.print("Enter login: ");
        String login = scanner.nextLine();
        System.out.print("Enter Pin code: ");
        String pin = scanner.nextLine();

        Account user = new Account(1, "test", 1234, "john", 1000, "Active");

        while (true) {
            if(admin) { // admin
                System.out.print("\n1----Create New Account\n" +
                        "2----Delete Existing Account\n" +
                        "3----Update Account Information\n" +
                        "4----Search for Account\n" +
                        "5----Exit\n\n" +
                        "Enter your choice: ");
                String selection = scanner.nextLine();

                switch (selection) {
                    case "1": createAccount();
                        break;
                    case "2": deleteAccount();
                        break;
                    case "3": updateAccount();
                        break;
                    case "4": searchAccount();
                        break;
                    case "5": return;
                }
            } else { // customer
                System.out.print("\n1----Withdraw Cash\n" +
                        "2----Deposit Cash\n" +
                        "3----Display Balance\n" +
                        "4----Exit\n\n" +
                        "Enter your choice: ");
                String selection = scanner.nextLine();

                switch (selection) {
                    case "1": withdraw();
                        break;
                    case "2": deposit();
                        break;
                    case "3": display();
                        break;
                    case "4": return;
                }
            }
        }

    }

    public static void createAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nCreate New Account\nLogin: ");
        String login = scanner.nextLine();
        System.out.print("Pin Code: ");
        int pin = Integer.parseInt(scanner.nextLine());
        System.out.print("Holders Name: ");
        String name = scanner.nextLine();
        System.out.print("Starting Balance: ");
        double balance = Double.parseDouble(scanner.nextLine());
        System.out.print("Status: ");
        String status = scanner.nextLine();

        // TODO add acount to SQL and get assigned number

        int assigned = 1;
        Account newAccount = new Account(assigned, login, pin, name, balance, status);
        System.out.println("Account Successfully Created – the account number assigned is: " + assigned);

    }

    public static void deleteAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nDelete Existing Account\nEnter the account number to which you want to delete: ");
        int accountNum = Integer.parseInt(scanner.nextLine());

        // TODO get account
        String name = "test1";

        System.out.print("You wish to delete the account held by " + name + ". If this information is correct, please re-enter the account number: ");
        if(Integer.parseInt(scanner.nextLine()) == accountNum) {
            // TODO delete account
            System.out.println("\nAccount Deleted Successfully");
        } else {
            System.out.println("\nAccount Numbers Didn't Match. Try Again");
        }
    }

    public static void updateAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nUpdate Account Information\nEnter the Account Number: ");
        int accountNum = Integer.parseInt(scanner.nextLine());

        // TODO get account

        // temporary
        Account user = new Account(1, "test", 1234, "john", 1000, "Active");

        System.out.println("\nAccount #" + accountNum);
        System.out.println("Balance: " + user.getBalance());
        System.out.println("Please Update the Following Information:");

        System.out.print("Holder: ");
        String newName = scanner.nextLine();
        System.out.print("Status: ");
        String newStatus = scanner.nextLine();
        System.out.print("Login: ");
        String newLogin = scanner.nextLine();
        System.out.print("Pin Code: ");
        int newPin = Integer.parseInt(scanner.nextLine());

        user.setName(newName);
        user.setStatus(newStatus);
        user.setLogin(newLogin);
        user.setPin(newPin);

        System.out.println("\nAccount Information Updated Successfully");
    }

    public static void searchAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nSearch for Account\nEnter the Account Number: ");
        int accountNum = Integer.parseInt(scanner.nextLine());

        // TODO get account
        // temporary
        Account user = new Account(1, "test", 1234, "john", 1000, "Active");

        System.out.println("\nThe account information is:");
        System.out.println("Account #" + accountNum);
        System.out.println("Holder: " + user.getName());
        System.out.println("Balance: " + user.getBalance());
        System.out.println("Status: " + user.getStatus());
        System.out.println("Login: " + user.getLogin());
        System.out.println("Pin Code: " + user.getPin());
    }

    public static void withdraw() {}

    public static void deposit() {}

    public static void display() {}
}