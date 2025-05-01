import com.google.inject.*;
import com.google.inject.Injector;
import jakarta.inject.Inject;
import java.util.Scanner;

/**
 * Main application class that facilitates the user interaction for account management.
 * The class provides login functionality, selection menus, and invokes corresponding services
 * for both administrators and customers.
 */
public class Main {
    private final AccountService accountService;
    private final TransactionService transactionService;
    private final Scanner scanner;

    /**
     * Constructs a {@link Main} application with the necessary dependencies.
     *
     * @param accountService the {@link AccountService} to handle account-related operations.
     * @param transactionService the {@link TransactionService} to handle transaction operations.
     */
    @Inject
    public Main(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
        this.scanner = new Scanner(System.in);
    }

    /**
     * The entry point of the application, where dependency injection is used to create an instance
     * of {@link Main} and invoke the login menu.
     *
     * @param args the command-line arguments.
     */
    public static void main(String[] args) {
        Injector injector = Guice.createInjector(new AppModule());
        Main mainApp = injector.getInstance(Main.class);
        mainApp.loginMenu();
    }

    /**
     * Displays the login menu, where the user can enter their login credentials.
     * The method handles both admin and user login functionality.
     * Admin login requires a fixed login and pin ("admin" and "12345").
     */
    public void loginMenu() {
        boolean admin = false;
        Account user = null;

        // TO LOG IN AS ADMIN, USE login = admin, pin = 12345

        while (user == null && !admin) {
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

    /**
     * Displays the selection menu for the user after they have logged in.
     * If the user is an admin, the menu will offer options to manage accounts (create, delete, update, search).
     * If the user is a customer, the menu will offer options for managing their own account (withdraw, deposit, display balance).
     *
     * @param user the {@link Account} of the logged-in user (if not an admin).
     * @param admin a boolean indicating if the user is an admin or not.
     */
    public void selectionMenu(Account user, boolean admin) {
        while (true) {
            if (admin) { // admin
                System.out.print("\n--------------------------\n\n"
                        + "Hello Admin, Please Select From the Following:\n"
                        + "1----Create New Account\n"
                        + "2----Delete Existing Account\n"
                        + "3----Update Account Information\n"
                        + "4----Search for Account\n"
                        + "5----Exit\n\n"
                        + "Enter your choice: ");
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
                System.out.print("\n--------------------------\n\n"
                        + "Hello " + user.getName() + ", Please Select From the Following:\n"
                        + "1----Withdraw Cash\n"
                        + "2----Deposit Cash\n"
                        + "3----Display Balance\n"
                        + "4----Exit\n\n"
                        + "Enter your choice: ");
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
