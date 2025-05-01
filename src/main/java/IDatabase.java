import java.sql.Connection;
import java.sql.SQLException;

/**
 * Interface for database connection management.
 * Provides a method for obtaining a connection to the database.
 */
public interface IDatabase {
    /**
     * Establishes and returns a connection to the database.
     *
     * @return a {@link Connection} object representing the connection to the database.
     * @throws SQLException if a database access error occurs or the connection cannot be established.
     */
    Connection getConnection() throws SQLException;
}
