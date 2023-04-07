package cs2212.westernmaps.core;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators.IntSequenceGenerator;
import cs2212.westernmaps.login.PasswordAuthenticator;
import java.util.Arrays;

/**
 * An account that can be used to log in to the application.
 *
 * <p>Note that, although in general the content of a {@code byte[]} is mutable,
 * you should <i>not</i> mutate the content of {@link #passwordHash()}, as other
 * parts of the application may expect that it never changes.</p>
 *
 * @param username     The username used to log in to this account.
 * @param passwordHash The hash of this account's password. For security
 *                     reasons, the actual password is never saved.
 * @param developer    Whether this account is a developer account.
 */
@JsonIdentityInfo(generator = IntSequenceGenerator.class, scope = Account.class)
public record Account(String username, String passwordHash, boolean developer) {

    /**
     * Checks if the given password matches this account's password.
     *
     * <p>It is recommended to clear the {@code password} array using
     * {@link Arrays#fill} once it is no longer needed to prevent it from
     * staying in memory longer than necessary.</p>
     *
     * @param password The password to check.
     * @return         Whether the password was correct.
     */
    public boolean isPasswordCorrect(char[] password) {
        PasswordAuthenticator passwordAuthenticator = new PasswordAuthenticator();
        return passwordAuthenticator.authenticate(password, passwordHash);
    }
}
