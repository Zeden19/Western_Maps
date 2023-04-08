package cs2212.westernmaps.core;

import static org.junit.jupiter.api.Assertions.*;

import cs2212.westernmaps.login.PasswordAuthenticator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AccountTest {

    private Account account;

    @BeforeEach
    void setUp() {
        PasswordAuthenticator authenticator = new PasswordAuthenticator();
        String passwordHash = "password";
        String password = authenticator.hash(passwordHash.toCharArray());
        account = new Account("username", password, false);
    }

    @Test
    void isPasswordCorrect() {
        String correctPassword = "password";
        PasswordAuthenticator authenticator = new PasswordAuthenticator();
        assertTrue(authenticator.authenticate(correctPassword.toCharArray(), account.passwordHash()));
    }

    @Test
    void isPasswordNotCorrect() {
        String correctPassword = "Bad Password";
        PasswordAuthenticator authenticator = new PasswordAuthenticator();
        assertFalse(authenticator.authenticate(correctPassword.toCharArray(), account.passwordHash()));
    }

    @Test
    void username() {
        assertEquals("username", account.username());
    }

    @Test
    void developer() {
        assertFalse(account.developer());
    }
}
