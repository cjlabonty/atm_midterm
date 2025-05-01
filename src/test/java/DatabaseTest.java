import org.junit.Test;

import java.sql.Connection;
import java.sql.SQLException;

import static org.junit.Assert.*;

public class DatabaseTest {

    @Test
    public void testGetConnection_success() {
        Database database = new Database();
        try (Connection connection = database.getConnection()) {
            assertNotNull(connection);
            assertFalse(connection.isClosed());
        } catch (SQLException e) {
            fail("Connection failure: " + e.getMessage());
        }
    }
}