import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Implementation of the IDatabase interface that manages database connections.
 * This class handles the connection to a MySQL database using JDBC.
 */
@Singleton
public class Database implements IDatabase {
    private static final String URL = "jdbc:mysql://localhost:3306/atm";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    /**
     * Establishes and returns a connection to the database using the specified URL, user, and password.
     *
     * @return a {@link Connection} object that represents the connection to the database.
     * @throws SQLException if a database access error occurs or the URL, user, or password is invalid.
     */
    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
