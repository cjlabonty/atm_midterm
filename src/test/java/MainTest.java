import org.junit.*;
import java.io.*;
import static org.mockito.Mockito.*;

public class MainTest {
    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;

    private AccountService mockAccountService;
    private TransactionService mockTransactionService;
    private Main mainApp;

    Account fakeAccount;

    @Before
    public void setUp() {
        System.setOut(new PrintStream(outContent));
        mockAccountService = mock(AccountService.class);
        mockTransactionService = mock(TransactionService.class);
        fakeAccount = new Account(1, "testLogin", "12345", "Test Name", 100.0, "Active");
    }

    @After
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    @Test
    public void testSuccessfulCustomerLoginAndExit() {
        String input = "testLogin\n12345\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(mockAccountService.findAccount("testLogin", "12345")).thenReturn(fakeAccount);

        mainApp = new Main(mockAccountService, mockTransactionService);
        mainApp.loginMenu();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Hello Test Name"));
        Assert.assertTrue(output.contains("Exit"));
    }

    @Test
    public void testSuccessfulAdminLoginAndExit() {
        String input = "admin\n12345\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        mainApp = new Main(mockAccountService, mockTransactionService);
        mainApp.loginMenu();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Hello Admin"));
        Assert.assertTrue(output.contains("Exit"));
    }

    @Test
    public void testLoginAccountNotFound() {
        String input = "testLogin\n12344\ntestLogin\n12345\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(mockAccountService.findAccount("testLogin", "12344")).thenReturn(null);
        when(mockAccountService.findAccount("testLogin", "12345")).thenReturn(fakeAccount);

        mainApp = new Main(mockAccountService, mockTransactionService);
        mainApp.loginMenu();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Account not found. Please try again"));
        Assert.assertTrue(output.contains("Hello Test Name"));
        Assert.assertTrue(output.contains("Exit"));
    }

    @Test
    public void testAdminInvalidSelection() {
        String input = "admin\n12345\n-1\n6\n0\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        mainApp = new Main(mockAccountService, mockTransactionService);
        mainApp.loginMenu();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Hello Admin"));
        Assert.assertTrue(output.contains("Invalid selection. Try again"));
    }

    @Test
    public void testCustomerInvalidSelection() {
        String input = "testLogin\n12345\n-1\n5\n0\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(mockAccountService.findAccount("testLogin", "12345")).thenReturn(fakeAccount);

        mainApp = new Main(mockAccountService, mockTransactionService);
        mainApp.loginMenu();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Hello Test Name"));
        Assert.assertTrue(output.contains("Invalid selection. Try again"));
    }

    @Test
    public void testValidateAdminSwitchStatement() {
        String input = "admin\n12345\n1\n2\n3\n4\n5\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        doNothing().when(mockAccountService).createAccount();
        doNothing().when(mockAccountService).deleteAccount();
        doNothing().when(mockAccountService).updateAccount();
        doNothing().when(mockAccountService).searchAccount();

        mainApp = new Main(mockAccountService, mockTransactionService);
        mainApp.loginMenu();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Hello Admin"));
        Assert.assertTrue(output.contains("Exit"));
    }

    @Test
    public void testValidateCustomerSwitchStatement() {
        String input = "testLogin\n12345\n1\n2\n3\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        when(mockAccountService.findAccount("testLogin", "12345")).thenReturn(fakeAccount);

        when(mockTransactionService.withdraw(fakeAccount)).thenReturn(fakeAccount);
        when(mockTransactionService.deposit(fakeAccount)).thenReturn(fakeAccount);
        doNothing().when(mockTransactionService).display(fakeAccount);

        mainApp = new Main(mockAccountService, mockTransactionService);
        mainApp.loginMenu();

        String output = outContent.toString();
        Assert.assertTrue(output.contains("Hello Test Name"));
        Assert.assertTrue(output.contains("Exit"));
    }
}