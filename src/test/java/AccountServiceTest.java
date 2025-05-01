import org.junit.Test;
import static org.mockito.Mockito.*;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.*;

public class AccountServiceTest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private final InputStream originalIn = System.in;
    private IDatabase mockDatabase;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockGeneratedKeys;

    private AccountService accountService;

    @Before // set up test environment
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));

        mockDatabase = mock(IDatabase.class);
        mockConnection = mock(Connection.class);
        mockStatement = mock(PreparedStatement.class);
        mockGeneratedKeys = mock(ResultSet.class);

        when(mockDatabase.getConnection()).thenReturn(mockConnection);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS))).thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(12345);

        accountService = new AccountService(mockDatabase);
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void testSuccessfulAccount() {
        String userInput = "testuser\n12345\nTest User\n500.0\nactive\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        accountService.createAccount();
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Account Successfully Created"));
        Assert.assertTrue(output.contains("12345"));
    }

    @Test
    public void testTooFewDigits() {
        String userInput = "testuser2\n1234\n12345\nTest User\n500.0\nactive\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        accountService.createAccount();
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Pin Must Be at Least 5 Digits"));
    }

    @Test
    public void testNonNumbersInPIN() {
        String userInput = "1\n12345A\n12345\nTest User\n500.0\nactive\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        accountService.createAccount();
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Pin Must Contain Only Numbers"));
    }

    @Test
    public void testNegativeBalance() {
        String userInput = "testuser\n12345\nTest User\n-1\n0\nactive\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        accountService.createAccount();
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Error: Balance cannot be negative."));
    }

    @Test
    public void testLettersInBalance() {
        String userInput = "testuser\n12345\nTest User\n11aaaa\n0\nactive\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        accountService.createAccount();
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Error: Please enter a valid number."));
    }

    @Test
    public void testDeleteSuccessful() throws SQLException {
        String userInput = "12345\n12345\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        PreparedStatement mockDeleteStmt = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("SELECT name FROM Accounts"))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("name")).thenReturn("Test User");

        when(mockConnection.prepareStatement(contains("DELETE FROM Accounts"))).thenReturn(mockDeleteStmt);
        when(mockDeleteStmt.executeUpdate()).thenReturn(1);

        accountService.deleteAccount();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Account Deleted Successfully"));
    }

    @Test
    public void testDeleteInvalidAccountNum1() {
        String userInput = "-111a\n12345\n12345\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        accountService.deleteAccount();
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Invalid account number. Please enter a valid integer."));
    }

    @Test
    public void testDeleteInvalidAccountNum2() throws SQLException {
        String userInput = "12345\naa111\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("SELECT name FROM Accounts"))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("name")).thenReturn("Test User");

        accountService.deleteAccount();
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Invalid account number. Please enter a valid integer."));
    }

    @Test
    public void testDeleteNumbersDontMatch() throws SQLException {
        String userInput = "12345\n12346\n12345\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        PreparedStatement mockDeleteStmt = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("SELECT name FROM Accounts"))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("name")).thenReturn("Test User");

        when(mockConnection.prepareStatement(contains("DELETE FROM Accounts"))).thenReturn(mockDeleteStmt);
        when(mockDeleteStmt.executeUpdate()).thenReturn(1);

        accountService.deleteAccount();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Account Numbers Didn't Match. Try Again"));
    }

    @Test
    public void testDeleteAccountDoesntExist() throws SQLException {
        String userInput = "12345\n12345\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(contains("SELECT name FROM Accounts"))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(false);

        accountService.deleteAccount();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Account Not Found"));
    }

    @Test
    public void testUpdateAccountSuccessful() throws Exception {
        String selectSql = "SELECT * FROM Accounts WHERE id = ?";
        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        ResultSet mockSelectResult = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq(selectSql))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockSelectResult);
        when(mockSelectResult.next()).thenReturn(true);
        when(mockSelectResult.getDouble("balance")).thenReturn(1000.0);

        String updateSql = "UPDATE Accounts SET name = ?, status = ?, login = ?, pin = ? WHERE id = ?";
        PreparedStatement mockUpdateStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(eq(updateSql))).thenReturn(mockUpdateStmt);
        when(mockUpdateStmt.executeUpdate()).thenReturn(1);

        String userInput = "12345\nNew Name\nactive\nnewlogin\n54321\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        accountService.updateAccount();
        String output = outContent.toString();

        Assert.assertTrue(output.contains("Account Information Updated Successfully"));
    }

    @Test
    public void testUpdateInvalidAccountNum() {
        String userInput = "-a\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        accountService.updateAccount();
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Invalid account number. Please enter a valid integer."));
    }

    @Test
    public void testUpdateAccountInvalidPIN() throws Exception {
        String selectSql = "SELECT * FROM Accounts WHERE id = ?";
        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        ResultSet mockSelectResult = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq(selectSql))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockSelectResult);
        when(mockSelectResult.next()).thenReturn(true);
        when(mockSelectResult.getDouble("balance")).thenReturn(1000.0);

        String updateSql = "UPDATE Accounts SET name = ?, status = ?, login = ?, pin = ? WHERE id = ?";
        PreparedStatement mockUpdateStmt = mock(PreparedStatement.class);
        when(mockConnection.prepareStatement(eq(updateSql))).thenReturn(mockUpdateStmt);
        when(mockUpdateStmt.executeUpdate()).thenReturn(1);

        String userInput = "12345\nNew Name\nactive\nnewlogin\n5\nfdsjhj\n00000";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        accountService.updateAccount();
        String output = outContent.toString();

        Assert.assertTrue(output.contains("Pin Must Be at Least 5 Digits"));
        Assert.assertTrue(output.contains("Pin Must Contain Only Numbers"));
        Assert.assertTrue(output.contains("Account Information Updated Successfully"));
    }

    @Test
    public void testUpdateAccountNotFound() throws Exception {
        String selectSql = "SELECT * FROM Accounts WHERE id = ?";
        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        ResultSet mockSelectResult = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq(selectSql))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockSelectResult);
        when(mockSelectResult.next()).thenReturn(false);
        when(mockSelectResult.getDouble("balance")).thenReturn(1000.0);

        String userInput = "12345\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        accountService.updateAccount();
        String output = outContent.toString();

        Assert.assertTrue(output.contains("Account Not Found"));
    }

    @Test
    public void testSearchAccountSuccessful() throws Exception {
        String selectSql = "SELECT * FROM Accounts WHERE id = ?";
        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq(selectSql))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getString("name")).thenReturn("Alice");
        when(mockResultSet.getString("status")).thenReturn("active");
        when(mockResultSet.getString("login")).thenReturn("alice123");
        when(mockResultSet.getString("pin")).thenReturn("12345");
        when(mockResultSet.getDouble("balance")).thenReturn(1500.0);

        String userInput = "42\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        accountService.searchAccount();
        String output = outContent.toString();

        Assert.assertTrue(output.contains("The account information is:"));
        Assert.assertTrue(output.contains("Account #42"));
        Assert.assertTrue(output.contains("Holder: Alice"));
        Assert.assertTrue(output.contains("Balance: 1500.0"));
        Assert.assertTrue(output.contains("Status: active"));
        Assert.assertTrue(output.contains("Login: alice123"));
        Assert.assertTrue(output.contains("Pin Code: 12345"));
    }

    @Test
    public void testSearchInvalidAccountNum() {
        String userInput = "~^%&^^*\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));
        accountService.searchAccount();
        String output = outContent.toString();
        Assert.assertTrue(output.contains("Invalid account number. Please enter a valid integer."));
    }

    @Test
    public void testSearchAccountNotFound() throws Exception {
        String selectSql = "SELECT * FROM Accounts WHERE id = ?";
        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        ResultSet mockSelectResult = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq(selectSql))).thenReturn(mockSelectStmt);
        when(mockSelectStmt.executeQuery()).thenReturn(mockSelectResult);
        when(mockSelectResult.next()).thenReturn(false);

        String userInput = "12345\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        accountService.searchAccount();
        String output = outContent.toString();

        Assert.assertTrue(output.contains("Account Not Found"));
    }

    @Test
    public void testFindAccountSuccessful() throws Exception {
        String sql = "SELECT * FROM Accounts WHERE login = ? AND pin = ?";
        PreparedStatement mockStmt = mock(PreparedStatement.class);
        ResultSet mockResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(eq(sql))).thenReturn(mockStmt);
        when(mockStmt.executeQuery()).thenReturn(mockResultSet);
        when(mockResultSet.next()).thenReturn(true);
        when(mockResultSet.getInt("id")).thenReturn(5);
        when(mockResultSet.getString("name")).thenReturn("Jane Doe");
        when(mockResultSet.getString("status")).thenReturn("active");
        when(mockResultSet.getDouble("balance")).thenReturn(2500.0);

        Account result = accountService.findAccount("janeLogin", "54321");

        Assert.assertNotNull(result);
        Assert.assertEquals(5, result.getID());
        Assert.assertEquals("Jane Doe", result.getName());
        Assert.assertEquals(2500.0, result.getBalance(), 0.001);
    }
}
