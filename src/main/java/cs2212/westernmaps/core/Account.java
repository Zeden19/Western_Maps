package cs2212.westernmaps.core;

import cs2212.westernmaps.login.PasswordAuthenticator;
import java.util.Arrays;
import java.util.Objects;

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
public record Account(String username, String passwordHash, boolean developer) {
    /**
     * Changes the password this account uses to log in.
     *
     * <p>It is recommended to clear the {@code password} array using
     * {@link Arrays#fill} once it is no longer needed to prevent it from
     * staying in memory longer than necessary.</p>
     *
     * @param password The new password.
     * @return         A copy of this account with the {@link #passwordHash()}
     *                 updated.
     */
    public Account withPassword(char[] password) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

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
        boolean isEqual = passwordAuthenticator.authenticate(password, passwordHash);
        Arrays.fill(password, ' ');
        return isEqual;
    }

    /**
     * Checks if this account is equal to another account.
     *
     * <p>All fields are considered, including the password hash and whether the
     * account is a developer. This means that two accounts with the same
     * username can be considered unequal.</p>
     *
     * <p>Normally, Java automatically generates {@code equals} and
     * {@link #hashCode} implementations, but the automatic implementations do
     * not correctly handle the password hash.</p>
     *
     * @param o The account to check equality against.
     * @return  Whether the two accounts are equal.
     */
    // Generated by IntelliJ.
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Account account = (Account) o;
        return developer == account.developer
                && username.equals(account.username)
                && Arrays.equals(passwordHash.toCharArray(), account.passwordHash.toCharArray());
    }

    /**
     * Generates an integer hash code for this account.
     *
     * <p>All fields are considered, including the password hash and whether the
     * account is a developer. This means that two accounts with the same
     * username can have different hashes.</p>
     *
     * <p>Normally, Java automatically generates {@link #equals} and
     * {@code hashCode} implementations, but the automatic implementations do
     * not correctly handle the password hash.</p>
     *
     * @return  An integer hash code for this account.
     */
    // Generated by IntelliJ.
    @Override
    public int hashCode() {
        int result = Objects.hash(username, developer);
        result = 31 * result + Arrays.hashCode(passwordHash.toCharArray());
        return result;
    }
}
