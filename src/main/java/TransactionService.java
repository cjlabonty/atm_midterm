import com.google.inject.Inject;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

/**
 * Service class responsible for handling financial transactions, such as withdrawals and deposits.
 * Also provides the functionality to display the current balance of a customer's account.
 */
public class TransactionService {
    private final DatabaseService databaseService;

    /**
     * Constructs a {@link TransactionService} with the given {@link DatabaseService}.
     *
     * @param databaseService the {@link DatabaseService} used for updating account data in the database.
     */
    @Inject
    public TransactionService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    /**
     * Withdraws a specified amount from the customer's account.
     * Verifies that the withdrawal is valid (greater than zero and less than or equal to the account balance).
     * If valid, the balance is updated and the transaction is logged.
     *
     * @param account the {@link Account} from which funds are being withdrawn.
     * @return the updated {@link Account} with the new balance.
     */
    public Account withdraw(Account account) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nWithdraw Cash\nEnter the withdrawal amount: ");
        int withdrawal;
        try {
            withdrawal = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return account;
        }

        if (withdrawal > account.getBalance()) {
            System.out.println("\nWithdrawal Amount too Large. Transaction Failed");
        } else if (withdrawal <= 0) {
            System.out.println("\nWithdrawal Amount Must Be Greater Than 0. Transaction Failed");
        } else {
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            //account.setBalance(account.balance - withdrawal);
            Account newAccount = account.withUpdatedBalance(account.getBalance() - withdrawal);
            System.out.println("\nCash Successfully Withdrawn");
            System.out.println("Account #" + newAccount.getID());
            System.out.println("Date: " + currentDate);
            System.out.println("Withdrawn: " + withdrawal);
            System.out.println("Balance: " + newAccount.getBalance());
            databaseService.updateDatabase(newAccount);
            return newAccount;
        }

        return account;
    }

    /**
     * Deposits a specified amount into the customer's account.
     * Verifies that the deposit is valid (greater than zero).
     * If valid, the balance is updated and the transaction is logged.
     *
     * @param account the {@link Account} into which funds are being deposited.
     * @return the updated {@link Account} with the new balance.
     */
    public Account deposit(Account account) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nDeposit Cash\nEnter the cash amount to deposit: ");
        int deposited;
        try {
            deposited = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            return account;
        }

        if (deposited <= 0) {
            System.out.println("\nDeposit Amount Must Be Greater Than 0. Transaction Failed");
        } else {
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            //account.setBalance(account.balance + deposited);
            Account newAccount = account.withUpdatedBalance(account.getBalance() + deposited);
            System.out.println("\nCash Successfully Deposited");
            System.out.println("Account #" + newAccount.getID());
            System.out.println("Date: " + currentDate);
            System.out.println("Deposited: " + deposited);
            System.out.println("Balance: " + newAccount.getBalance());
            databaseService.updateDatabase(newAccount);
            return newAccount;
        }

        return account;
    }

    /**
     * Displays the current balance of the specified customer's account.
     * The balance is shown along with the account number and the current date.
     *
     * @param account the {@link Account} whose balance is to be displayed.
     */
    public void display(Account account) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        System.out.print("\nDisplay Balance\n");
        System.out.println("Account #" + account.getID());
        System.out.println("Date: " + currentDate);
        System.out.println("Balance: " + account.getBalance());
    }
}
