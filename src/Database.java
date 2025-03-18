import com.google.inject.Singleton;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Singleton
public class Database implements IDatabase{
    private static final String URL = "jdbc:mysql://localhost:3306/atm";
    private static final String USER = "root";
    private static final String PASSWORD = "root";

    @Override
    public Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}
