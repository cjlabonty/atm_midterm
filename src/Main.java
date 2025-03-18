import javax.sound.midi.Soundbank;
import java.sql.*;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
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
                user = findAccount(login, pin);
                if (user == null) {
                    System.out.println("Account not found. Please try again");
                }
            }
        }

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
                System.out.print("\n--------------------------\n\n" +
                        "Hello " + user.name + ", Please Select From the Following:\n" +
                        "1----Withdraw Cash\n" +
                        "2----Deposit Cash\n" +
                        "3----Display Balance\n" +
                        "4----Exit\n\n" +
                        "Enter your choice: ");
                String selection = scanner.nextLine();

                switch (selection) {
                    case "1": user = withdraw(user);
                        break;
                    case "2": user = deposit(user);
                        break;
                    case "3": display(user);
                        break;
                    case "4": return;
                }
            }
        }

    }

    // Create an account
    public static void createAccount() {
        Scanner scanner = new Scanner(System.in);

        // Guide user through account creation
        System.out.print("\nCreate New Account\nLogin: ");
        String login = scanner.nextLine();
        String pin = "";
        // Ensure pin is at least 5 characters
        while(pin.length() < 5) {
            System.out.print("Pin Code: ");
            pin = scanner.nextLine();
            if(pin.length() < 5) {
                System.out.println("Pin Must Be at Least 5 Digits");
            } else if (!pin.matches("\\d+")) {
                System.out.println("Pin Must Contain Only Numbers");
                pin = "";
            }
        }
        System.out.print("Holders Name: ");
        String name = scanner.nextLine();
        double balance = -1;
        while (balance < 0) {
            try {
                System.out.print("Starting Balance: ");
                balance = Double.parseDouble(scanner.nextLine());
                if (balance < 0) {
                    System.out.println("Error: Balance cannot be negative.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Error: Please enter a valid number.");
            }
        }
        System.out.print("Status: ");
        String status = scanner.nextLine();

        // Create the account in SQL
        String sql = "INSERT INTO Accounts (login, pin, name, balance, status) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, login);
            stmt.setString(2, pin);
            stmt.setString(3, name);
            stmt.setDouble(4, balance);
            stmt.setString(5, status);

            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        System.out.println("Account Successfully Created – the account number assigned is: " + generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Creating account failed, no ID obtained.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Delete an account
    public static void deleteAccount() {
        Scanner scanner = new Scanner(System.in);

        // Get account number to delete
        System.out.print("\nDelete Existing Account\nEnter the account number to which you want to delete: ");
        int accountNum = Integer.parseInt(scanner.nextLine());

        // Search for account to delete
        String sql = "SELECT name FROM Accounts WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNum);
            ResultSet rs = stmt.executeQuery();

            // Check if account exists
            if (rs.next()) {
                String name = rs.getString("name");
                System.out.print("You wish to delete the account held by " + name + ". If this information is correct, please re-enter the account number: ");

                // Ask user to confirm
                if (Integer.parseInt(scanner.nextLine()) == accountNum) {
                    String deleteSql = "DELETE FROM Accounts WHERE id = ?";
                    try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                        deleteStmt.setInt(1, accountNum);
                        int rowsDeleted = deleteStmt.executeUpdate();
                        if (rowsDeleted > 0) {
                            System.out.println("\nAccount Deleted Successfully");
                        } else {
                            System.out.println("\nFailed to delete account. Please try again.");
                        }
                    }
                } else {
                    System.out.println("\nAccount Numbers Didn't Match. Try Again");
                }
            } else {
                System.out.println("\nAccount Not Found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Update an account
    public static void updateAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nUpdate Account Information\nEnter the Account Number: ");
        int accountNum = Integer.parseInt(scanner.nextLine());

        // Find account in database
        String sql = "SELECT * FROM Accounts WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNum);
            ResultSet rs = stmt.executeQuery();

            // Check if account exists
            if (rs.next()) {
                double currentBalance = rs.getDouble("balance");

                System.out.println("\nAccount #" + accountNum);
                System.out.println("Balance: " + currentBalance);
                System.out.println("Please Update the Following Information:");
                System.out.print("Holder: ");
                String newName = scanner.nextLine();
                System.out.print("Status: ");
                String newStatus = scanner.nextLine();
                System.out.print("Login: ");
                String newLogin = scanner.nextLine();
                String newPin = "";
                // Ensure pin is at least 5 characters
                while(newPin.length() < 5) {
                    System.out.print("Pin Code: ");
                    newPin = scanner.nextLine();
                    if(newPin.length() < 5) {
                        System.out.println("Pin Must Be at Least 5 Digits");
                    } else if (!newPin.matches("\\d+")) {
                        System.out.println("Pin Must Contain Only Numbers");
                        newPin = "";
                    }
                }

                // Update account
                String updateSql = "UPDATE Accounts SET name = ?, status = ?, login = ?, pin = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, newName);
                    updateStmt.setString(2, newStatus);
                    updateStmt.setString(3, newLogin);
                    updateStmt.setString(4, newPin);
                    updateStmt.setInt(5, accountNum);

                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("\nAccount Information Updated Successfully");
                    } else {
                        System.out.println("\nFailed to update account. Please try again.");
                    }
                }
            } else {
                System.out.println("\nAccount Not Found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Search for an account by ID
    public static void searchAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nSearch for Account\nEnter the Account Number: ");
        int accountNum = Integer.parseInt(scanner.nextLine());

        // Find account in database
        String sql = "SELECT * FROM Accounts WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNum);
            ResultSet rs = stmt.executeQuery();

            // Check if account exists
            if (rs.next()) {
                String currentName = rs.getString("name");
                String currentStatus = rs.getString("status");
                String currentLogin = rs.getString("login");
                String currentPin = rs.getString("pin");
                double currentBalance = rs.getDouble("balance");

                System.out.println("\nThe account information is:");
                System.out.println("Account #" + accountNum);
                System.out.println("Holder: " + currentName);
                System.out.println("Balance: " + currentBalance);
                System.out.println("Status: " + currentStatus);
                System.out.println("Login: " + currentLogin);
                System.out.println("Pin Code: " + currentPin);
            } else {
                System.out.println("\nAccount Not Found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    // Customer withdraws
    public static Account withdraw(Account account) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nWithdraw Cash\nEnter the withdrawal amount: ");
        int withdrawal = Integer.parseInt(scanner.nextLine());

        if(withdrawal > account.balance) {
            System.out.println("\nWithdrawal Amount too Large. Transaction Failed");
        } else if (withdrawal <= 0) {
            System.out.println("\nWithdrawal Amount Must Be Greater Than 0. Transaction Failed");
        } else {
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            //account.setBalance(account.balance - withdrawal);
            Account newAccount = new Account(account.id, account.login, account.pin, account.name, account.balance - withdrawal, account.status);
            System.out.println("\nCash Successfully Withdrawn");
            System.out.println("Account #" + account.id);
            System.out.println("Date: " + currentDate);
            System.out.println("Withdrawn: " + withdrawal);
            System.out.println("Balance: " + account.balance);
            updateDatabase(newAccount);
            return newAccount;
        }

        return account;
    }

    // Customer makes deposit
    public static Account deposit(Account account) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nDeposit Cash\nEnter the cash amount to deposit: ");
        int deposited = Integer.parseInt(scanner.nextLine());

        if(deposited <= 0) {
            System.out.println("\nDeposit Amount Must Be Greater Than 0. Transaction Failed");
        } else {
            String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
            //account.setBalance(account.balance + deposited);
            Account newAccount = new Account(account.id, account.login, account.pin, account.name, account.balance + deposited, account.status);
            System.out.println("\nCash Successfully Deposited");
            System.out.println("Account #" + account.id);
            System.out.println("Date: " + currentDate);
            System.out.println("Deposited: " + deposited);
            System.out.println("Balance: " + account.balance);
            updateDatabase(account);
            return newAccount;
        }

        return account;
    }

    // Displays customer balance
    public static void display(Account account) {
        String currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"));
        System.out.print("\nDisplay Balance\n");
        System.out.println("Account #" + account.id);
        System.out.println("Date: " + currentDate);
        System.out.println("Balance: " + account.balance);
    }

    // Update accounts after customer makes changes
    public static Account findAccount(String login, String pin) {
        // Find account in database
        String sql = "SELECT * FROM Accounts WHERE login = ? AND pin = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, login);
            stmt.setString(2, pin);
            ResultSet rs = stmt.executeQuery();

            // Check if account exists
            if (rs.next()) {
                // Get info and return account object
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String status = rs.getString("status");
                double balance = rs.getDouble("balance");
                return new Account(id, login, pin, name, balance, status);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // Update accounts after customer makes changes
    public static void updateDatabase(Account account) {
        int accountNum = account.id;
        // Find account in database
        String sql = "SELECT * FROM Accounts WHERE id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNum);
            ResultSet rs = stmt.executeQuery();

            // Check if account exists
            if (rs.next()) {
                // Update account
                String updateSql = "UPDATE Accounts SET login = ?, pin = ?, name = ?, balance = ?, status = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setString(1, account.login);
                    updateStmt.setString(2, account.pin);
                    updateStmt.setString(3, account.name);
                    updateStmt.setDouble(4, account.balance);
                    updateStmt.setString(5, account.status);
                    updateStmt.setInt(6, accountNum);

                    int rowsUpdated = updateStmt.executeUpdate();
                    if (rowsUpdated > 0) {
                        System.out.println("\nAccount Information Updated Successfully");
                    } else {
                        System.out.println("\nFailed to update account. Please try again.");
                    }
                }
            } else {
                System.out.println("\nAccount Not Found");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}