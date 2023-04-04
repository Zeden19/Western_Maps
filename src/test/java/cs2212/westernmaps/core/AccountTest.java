package cs2212.westernmaps.core;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountTest {

    private Account account;
    @BeforeEach
    void setUp() {
        byte[] passwordHash = "password".getBytes();
        account = new Account("username", passwordHash, false);
    }

    @AfterEach
    void tearDown() {}

    @Test
    void withPassword() {
        char[] newPassword = "newPassword".toCharArray();
        Account newAccount = account.withPassword(newPassword);

        assertNotSame(account, newAccount);
        assertTrue(newAccount.isPasswordCorrect(newPassword));
    }

    @Test
    void isPasswordCorrect() {
        char[] correctPassword = "password".toCharArray();
        char[] incorrectPassword = "wrong".toCharArray();

        assertTrue(account.isPasswordCorrect(correctPassword));
        assertFalse(account.isPasswordCorrect(incorrectPassword));
    }

    @Test
    void testEquals() {
        byte[] passwordHash = "password".getBytes();
        Account equalAccount = new Account("username", passwordHash, false);
        Account differentAccount = new Account("differentUsername", passwordHash, false);

        assertTrue(account.equals(equalAccount));
        assertFalse(account.equals(differentAccount));
    }

    @Test
    void testHashCode() {
        byte[] passwordHash = "password".getBytes();
        Account equalAccount = new Account("username", passwordHash, false);

        assertEquals(account.hashCode(), equalAccount.hashCode());
    }

    @Test
    void username() {
        assertEquals("username", account.username());
    }

    @Test
    void passwordHash() {
        assertArrayEquals("password".getBytes(), account.passwordHash());
    }

    @Test
    void developer() {
        assertFalse(account.developer());
    }
}
