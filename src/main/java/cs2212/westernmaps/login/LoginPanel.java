package cs2212.westernmaps.login;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.core.Account;
import cs2212.westernmaps.core.Database;
import java.awt.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * This class creates the login panel for the application. It does all the necessary checks to make sure the account
 * exists and the password is valid.
 *
 * @author Arjun Sharma
 * @author Connor Cummings
 */
public final class LoginPanel extends JPanel {

    // the fields for the username and password
    private final JTextField usernameField;
    private final JPasswordField passwordField;

    // the errors that are set to invisible by default
    private final JLabel invalidUserError;
    private final JLabel invalidPasswordError;

    // listeners for logging in and creating an account
    private final List<Consumer<Account>> loginListeners = new ArrayList<>();
    private final List<Runnable> createAccountClickListeners = new ArrayList<>();

    /**
     * Creates the login panel
     * @param database the database that is used to check the current account
     */
    public LoginPanel(Database database) {
        // This determines what MainWindow will use as its title.
        setName("Sign In");

        // When a GridBagLayout has one child, it will center it.
        setLayout(new GridBagLayout());

        // Title
        var title = new JLabel("Sign In");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");

        // Username field
        usernameField = new JTextField();
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setColumns(20);
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");

        // Password field
        passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setColumns(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");

        // Sign in button
        var signInButton = new JButton("Sign In");
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInButton.addActionListener(e -> validateAndSubmit(database));

        // Create account link
        var createAccountLink = new LinkButton("Create an Account");
        createAccountLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountLink.addActionListener(e -> createAccountClickListeners.forEach(Runnable::run));

        // Error message for invalid user
        invalidUserError = new JLabel("Username not found");
        invalidUserError.setAlignmentX(Component.CENTER_ALIGNMENT);
        invalidUserError.setForeground(UIManager.getColor("Actions.Red"));
        invalidUserError.setVisible(false);

        // Error message for invalid password for account
        invalidPasswordError = new JLabel("Password is incorrect");
        invalidPasswordError.setAlignmentX(Component.CENTER_ALIGNMENT);
        invalidPasswordError.setForeground(UIManager.getColor("Actions.Red"));
        invalidPasswordError.setVisible(false);

        // Panel to hold all the components
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));
        panel.add(title);
        panel.add(Box.createVerticalStrut(24));
        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(8));
        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(16));
        panel.add(signInButton);
        panel.add(Box.createVerticalStrut(8));
        panel.add(invalidUserError);
        panel.add(invalidPasswordError);
        panel.add(Box.createVerticalStrut(8));
        panel.add(createAccountLink);

        // Add the panel to the layout
        add(panel);
    }

    /**
     * The listener for when the user logs in
     * @param listener the listener for when the user logs in
     */
    public void addLoginListener(Consumer<Account> listener) {
        loginListeners.add(listener);
    }

    /**
     * The listener for when the user creates an account
     *
     *  @param listener the listener for when the user creates an accounnt
     */
    public void addCreateAccountClickListener(Runnable listener) {
        createAccountClickListeners.add(listener);
    }

    // validating if the user exists and if the password is correct
    private void validateAndSubmit(Database database) {
        invalidUserError.setVisible(false);
        invalidPasswordError.setVisible(false);
        List<Account> accounts = database.getCurrentState().accounts();

        var username = usernameField.getText();
        var account =
                accounts.stream().filter(a -> a.username().equals(username)).findFirst();

        if (account.isPresent()) {
            invalidUserError.setVisible(false);

            char[] passwordChar = passwordField.getPassword();
            if (account.get().isPasswordCorrect(passwordChar)) {
                invalidPasswordError.setVisible(false);
                loginListeners.forEach(listener -> listener.accept(account.get()));

            } else invalidPasswordError.setVisible(true);
            Arrays.fill(passwordChar, ' ');

        } else invalidUserError.setVisible(true);
    }
}
