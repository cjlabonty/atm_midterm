import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TransactionServiceTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;

    private final InputStream originalIn = System.in;
    private Database mockDatabase;
    private Connection mockConnection;
    private PreparedStatement mockStatement;
    private ResultSet mockGeneratedKeys;

    private Account account;
    private DatabaseService databaseService;
    private TransactionService transactionService;

    @Before // set up test environment
    public void setUp() throws Exception {
        System.setOut(new PrintStream(outContent));

        mockDatabase = mock(Database.class);
        mockConnection = mock(Connection.class);

        PreparedStatement mockSelectStmt = mock(PreparedStatement.class);
        PreparedStatement mockUpdateStmt = mock(PreparedStatement.class);
        ResultSet mockSelectResultSet = mock(ResultSet.class);

        when(mockConnection.prepareStatement(anyString()))
                .thenReturn(mockSelectStmt)
                .thenReturn(mockUpdateStmt);

        when(mockSelectStmt.executeQuery()).thenReturn(mockSelectResultSet);
        when(mockSelectResultSet.next()).thenReturn(true);

        when(mockUpdateStmt.executeUpdate()).thenReturn(1);

        mockStatement = mock(PreparedStatement.class);
        mockGeneratedKeys = mock(ResultSet.class);
        when(mockConnection.prepareStatement(anyString(), eq(Statement.RETURN_GENERATED_KEYS)))
                .thenReturn(mockStatement);
        when(mockStatement.executeUpdate()).thenReturn(1);
        when(mockStatement.getGeneratedKeys()).thenReturn(mockGeneratedKeys);
        when(mockGeneratedKeys.next()).thenReturn(true);
        when(mockGeneratedKeys.getInt(1)).thenReturn(12345);

        when(mockDatabase.getConnection()).thenReturn(mockConnection);

        account = new Account(1, "testLogin", "12345", "Test Name", 100.0, "Active");
        databaseService = new DatabaseService(mockDatabase);
        transactionService = new TransactionService(databaseService);
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void testSuccessfulWithdraw() {
        double initialBalance = account.getBalance();
        String userInput = "1\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        Account updatedAccount = transactionService.withdraw(account);

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Cash Successfully Withdrawn"));

        assertEquals(initialBalance - 1, updatedAccount.getBalance(), 0.001);
    }

    @Test
    public void testInvalidAmountWithdraw() {
        double initialBalance = account.getBalance();
        String userInput = "1.\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        Account updatedAccount = transactionService.withdraw(account);

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Invalid input. Please enter a valid number."));

        assertEquals(initialBalance, updatedAccount.getBalance(), 0.001);
    }

    @Test
    public void testWithdrawTooLarge() {
        double initialBalance = account.getBalance();
        String userInput = "101\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        Account updatedAccount = transactionService.withdraw(account);

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Withdrawal Amount too Large. Transaction Failed"));
        assertEquals(initialBalance, updatedAccount.getBalance(), 0.001);
    }

    @Test
    public void testWithdrawNegative() {
        double initialBalance = account.getBalance();
        String userInput = "-1\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        Account updatedAccount = transactionService.withdraw(account);

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Withdrawal Amount Must Be Greater Than 0. Transaction Failed"));
        assertEquals(initialBalance, updatedAccount.getBalance(), 0.001);
    }

    @Test
    public void testSuccessfulDeposit() {
        double initialBalance = account.getBalance();
        String userInput = "92\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        Account updatedAccount = transactionService.deposit(account);

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Cash Successfully Deposited"));

        assertEquals(initialBalance + 92, updatedAccount.getBalance(), 0.001);
    }

    @Test
    public void testInvalidAmountDeposit() {
        double initialBalance = account.getBalance();
        String userInput = "~29\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        Account updatedAccount = transactionService.deposit(account);

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Invalid input. Please enter a valid number."));

        assertEquals(initialBalance, updatedAccount.getBalance(), 0.001);
    }

    @Test
    public void testDepositNegative() {
        double initialBalance = account.getBalance();
        String userInput = "-100\n";
        System.setIn(new ByteArrayInputStream(userInput.getBytes()));

        Account updatedAccount = transactionService.deposit(account);

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Deposit Amount Must Be Greater Than 0. Transaction Failed"));
        assertEquals(initialBalance, updatedAccount.getBalance(), 0.001);
    }

    @Test
    public void testDisplay() {
        transactionService.display(account);

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Display Balance"));
        Assert.assertTrue(output.contains("Account #" + account.getID()));
        Assert.assertTrue(output.contains("Date: " + LocalDate.now().format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))));
        Assert.assertTrue(output.contains("Balance: " + account.getBalance()));
    }
}
