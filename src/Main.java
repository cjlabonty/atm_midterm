import com.google.inject.*;
import com.google.inject.Injector;
import jakarta.inject.Inject;
import java.util.Scanner;

public class Main {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final Scanner scanner;

    // Uses DI
    @Inject
    public Main(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.scanner = new Scanner(System.in);
    }

    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        Main mainApp = injector.getInstance(Main.class);
        mainApp.loginMenu();
    }

    public void loginMenu() {
        boolean admin = false;
        Account user = null;

        // TO LOG IN AS ADMIN, USE login = admin, pin = 12345

        while(user == null && !admin) {
            System.out.print("Enter login: ");
            String login = scanner.nextLine();
            System.out.print("Enter Pin code: ");
            String pin = scanner.nextLine();

            if (login.equals("admin") && pin.equals("12345")) {
                admin = true;
            } else {
                user = accountService.findAccount(login, pin);
                if (user == null) {
                    System.out.println("Account not found. Please try again");
                }
            }
        }

        selectionMenu(user, admin);
    }

    public void selectionMenu(Account user, boolean admin) {
        while (true) {
            if(admin) { // admin
                System.out.print("\n--------------------------\n\n" +
                        "Hello Admin, Please Select From the Following:\n" +
                        "1----Create New Account\n" +
                        "2----Delete Existing Account\n" +
                        "3----Update Account Information\n" +
                        "4----Search for Account\n" +
                        "5----Exit\n\n" +
                        "Enter your choice: ");
                String selection = scanner.nextLine();

                switch (selection) {
                    case "1": accountService.createAccount();
                        break;
                    case "2": accountService.deleteAccount();
                        break;
                    case "3": accountService.updateAccount();
                        break;
                    case "4": accountService.searchAccount();
                        break;
                    case "5": return;
                    default: System.out.println("Invalid selection. Try again");
                }
            } else { // customer
                System.out.print("\n--------------------------\n\n" +
                        "Hello " + user.getName() + ", Please Select From the Following:\n" +
                        "1----Withdraw Cash\n" +
                        "2----Deposit Cash\n" +
                        "3----Display Balance\n" +
                        "4----Exit\n\n" +
                        "Enter your choice: ");
                String selection = scanner.nextLine();

                switch (selection) {
                    case "1": user = transactionService.withdraw(user);
                        break;
                    case "2": user = transactionService.deposit(user);
                        break;
                    case "3": transactionService.display(user);
                        break;
                    case "4": return;
                    default: System.out.println("Invalid selection. Try again");
                }
            }
        }
    }
}