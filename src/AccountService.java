import com.google.inject.Inject;

import java.sql.*;
import java.util.Scanner;

public class AccountService {

    private final IDatabase databaseService;

    @Inject
    public AccountService(IDatabase databaseService) {
        this.databaseService = databaseService;
    }

    // Create an account
    public void createAccount() {
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
        try (Connection conn = databaseService.getConnection();
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
    public void deleteAccount() {
        Scanner scanner = new Scanner(System.in);

        // Get account number to delete
        System.out.print("\nDelete Existing Account\nEnter the account number to which you want to delete: ");
        int accountNum;
        try {
            accountNum = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid account number. Please enter a valid integer.");
            return;
        }

        // Search for account to delete
        String sql = "SELECT name FROM Accounts WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNum);
            ResultSet rs = stmt.executeQuery();

            // Check if account exists
            if (rs.next()) {
                String name = rs.getString("name");
                System.out.print("You wish to delete the account held by " + name + ". If this information is correct, please re-enter the account number: ");

                // Ask user to confirm
                int confirmNum;
                try {
                    confirmNum = Integer.parseInt(scanner.nextLine());
                } catch (NumberFormatException e) {
                    System.out.println("Invalid account number. Please enter a valid integer.");
                    return;
                }
                if (confirmNum == accountNum) {
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
    public void updateAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nUpdate Account Information\nEnter the Account Number: ");
        int accountNum;
        try {
            accountNum = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid account number. Please enter a valid integer.");
            return;
        }

        // Find account in database
        String sql = "SELECT * FROM Accounts WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
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
    public void searchAccount() {
        Scanner scanner = new Scanner(System.in);

        System.out.print("\nSearch for Account\nEnter the Account Number: ");
        int accountNum;
        try {
            accountNum = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid account number. Please enter a valid integer.");
            return;
        }

        // Find account in database
        String sql = "SELECT * FROM Accounts WHERE id = ?";
        try (Connection conn = databaseService.getConnection();
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

    // Update accounts after customer makes changes
    public Account findAccount(String login, String pin) {
        // Find account in database
        String sql = "SELECT * FROM Accounts WHERE login = ? AND pin = ?";
        try (Connection conn = databaseService.getConnection();
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
}
