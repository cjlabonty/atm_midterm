import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

@Singleton
public class DatabaseService {
    private final Database database;

    @Inject
    public DatabaseService(Database database) {
        this.database = database;
    }
    // Update accounts after customer makes changes
    public void updateDatabase(Account account) {
        int accountNum = account.getID();
        // Find account in database
        String sql = "SELECT * FROM Accounts WHERE id = ?";
        try (Connection conn = database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, accountNum);
            ResultSet rs = stmt.executeQuery();

            // Check if account exists
            if (rs.next()) {
                // Update account
                String updateSql = "UPDATE Accounts SET balance = ? WHERE id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setDouble(1, account.getBalance());
                    updateStmt.setInt(2, accountNum);

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
