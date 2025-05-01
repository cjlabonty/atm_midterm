import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.mockito.Mockito.*;

public class DatabaseServiceTest {

    @Mock
    private Database mockDatabase;

    @Mock
    private Connection mockConnection;

    @Mock
    private PreparedStatement mockSelectStmt;

    @Mock
    private PreparedStatement mockUpdateStmt;

    @Mock
    private ResultSet mockResultSet;

    @InjectMocks
    private DatabaseService databaseService;

    private Account account;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        // Test account
        account = new Account(1, "testLogin", "12345", "Test Name", 100.0, "Active");
    }

    @Test
    public void testUpdateDatabase_accountExists() throws SQLException {
        when(mockDatabase.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockSelectStmt)
                .thenReturn(mockUpdateStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockUpdateStmt.executeUpdate()).thenReturn(1);

        databaseService.updateDatabase(account);

        verify(mockDatabase).getConnection();

        verify(mockSelectStmt).setInt(1, account.getID());
        verify(mockSelectStmt).executeQuery();

        verify(mockUpdateStmt).setDouble(1, account.getBalance());
        verify(mockUpdateStmt).setInt(2, account.getID());
        verify(mockUpdateStmt).executeUpdate();
    }

    @Test
    public void testUpdateDatabase_accountDoesNotExist() throws SQLException {
        when(mockDatabase.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString())).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        databaseService.updateDatabase(account);

        verify(mockDatabase).getConnection();
        verify(mockSelectStmt).setInt(1, account.getID());
        verify(mockSelectStmt).executeQuery();
        verify(mockUpdateStmt, never()).executeUpdate();
    }

    @Test
    public void testUpdateDatabase_sqlException() throws SQLException {
        when(mockDatabase.getConnection()).thenThrow(new SQLException("Database error"));
        databaseService.updateDatabase(account);
        verify(mockDatabase).getConnection();
    }
}
