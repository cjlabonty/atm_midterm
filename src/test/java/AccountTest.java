import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AccountTest {

    @Test
    public void testAccountCreation() {
        Account account = new Account(1, "user1", "12345", "Alice", 500.0, "Active");

        assertEquals(1, account.getID());
        assertEquals("Alice", account.getName());
        assertEquals(500.0, account.getBalance(), 0.001);
    }

    @Test
    public void testWithUpdatedBalanceReturnsNewAccount() {
        Account original = new Account(1, "user1", "12345", "Alice", 500.0, "Active");
        Account updated = original.withUpdatedBalance(750.0);

        assertEquals(500.0, original.getBalance(), 0.001);
        assertEquals(750.0, updated.getBalance(), 0.001);

        assertEquals(original.getID(), updated.getID());
        assertEquals(original.getName(), updated.getName());
    }
}