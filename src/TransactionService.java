import com.google.inject.Inject;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class TransactionService {
    private final DatabaseService databaseService;

    @Inject
    public TransactionService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    // Customer withdraws
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

        if(withdrawal > account.getBalance()) {
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

    // Customer makes deposit
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

        if(deposited <= 0) {
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

    // Displays customer balance
    public void display(Account account) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        System.out.print("\nDisplay Balance\n");
        System.out.println("Account #" + account.getID());
        System.out.println("Date: " + currentDate);
        System.out.println("Balance: " + account.getBalance());
    }
}
