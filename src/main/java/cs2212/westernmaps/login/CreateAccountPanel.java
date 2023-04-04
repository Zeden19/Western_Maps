package cs2212.westernmaps.login;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.core.Account;
import cs2212.westernmaps.core.Database;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

/**
 * This class creates the account panel for the application
 */
public final class CreateAccountPanel extends JPanel {
    private final JPasswordField passwordField;
    private final JPasswordField confirmPassword;
    private final JLabel passwordMatchError;
    private final JLabel passwordInvalidError;
    private final List<Consumer<Account>> accountCreateListeners = new ArrayList<>();
    private final List<Runnable> backButtonListeners = new ArrayList<>();
    private final JLabel userNameTakenError;

    public CreateAccountPanel(Database database) {
        // This determines what MainWindow will use as its title.
        setName("Create Account");

        // Setting layout for the whole panel
        setLayout(new OverlayLayout(this));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // back button
        var backButton = new JButton("Back");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setAlignmentY(Component.TOP_ALIGNMENT);
        backButton.addActionListener(e -> {
            backButtonListeners.forEach(Runnable::run);
        });

        // Title
        var title = new JLabel("Create Account");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");

        // Username field
        var usernameField = new JTextField();
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setColumns(20);
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");

        // Password requirements, like letters, symbol, number
        var passwordSpecifiers = new JLabel("8 or more letters, 1 symbol, 1 number");
        passwordSpecifiers.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordSpecifiers.setFont(new Font("Arial", Font.ITALIC, 10));

        // The password field
        passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setColumns(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");

        // Confirm password field, where user would retype password
        confirmPassword = new JPasswordField();
        confirmPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPassword.setColumns(20);
        confirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirm Password");

        // Create account Button
        var createAccountButton = new JButton("Create Account");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        createAccountButton.addActionListener(e -> {
            // checking if user can create account with given username and password
            if (!checkPasswordFields() || isUsernameTaken(database, usernameField.getText())) {
                return;
            }
            PasswordAuthenticator auth = new PasswordAuthenticator();
            char[] passwordChar = passwordField.getPassword();
            String hash = auth.hash(passwordChar);
            Arrays.fill(passwordChar, ' ');

            var newAccount = new Account(usernameField.getText(), hash, false);
            accountCreateListeners.forEach(listener -> listener.accept(newAccount));
        });

        // error if password doesn't match
        passwordMatchError = new JLabel("Passwords do not match");
        passwordMatchError.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordMatchError.setVisible(false);
        passwordMatchError.setForeground(UIManager.getColor("Actions.Red"));

        // error if password is invalid
        passwordInvalidError = new JLabel("Password not strong enough");
        passwordInvalidError.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordInvalidError.setVisible(false);
        passwordInvalidError.setForeground(UIManager.getColor("Actions.Red"));

        // error if account name is already taken
        userNameTakenError = new JLabel("Username has already been taken");
        userNameTakenError.setAlignmentX(Component.CENTER_ALIGNMENT);
        userNameTakenError.setVisible(false);
        userNameTakenError.setForeground(UIManager.getColor("Actions.Red"));

        // The main panel, a gridbagLayout, containing an inner boxLayout and the back button
        var mainPanel = new JPanel();
        mainPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        mainPanel.setAlignmentY(Component.TOP_ALIGNMENT);
        mainPanel.setLayout(new GridBagLayout());

        // An inner panel, a box layout, containing all the components
        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

        // adding all the main components to the boxLayout
        panel.add(title);
        panel.add(Box.createVerticalStrut(24));

        panel.add(usernameField);
        panel.add(Box.createVerticalStrut(8));

        panel.add(passwordField);
        panel.add(Box.createVerticalStrut(4));

        panel.add(passwordSpecifiers);
        panel.add(Box.createVerticalStrut(8));

        panel.add(confirmPassword);
        panel.add(Box.createVerticalStrut(16));

        panel.add(Box.createVerticalStrut(8));

        panel.add(passwordMatchError);
        panel.add(passwordInvalidError);
        panel.add(userNameTakenError);

        panel.add(Box.createVerticalStrut(8));

        panel.add(createAccountButton);

        // adding boxlayout into the gridbag layout
        mainPanel.add(panel);

        // adding back button and gridbag layout into the overlay layout
        add(backButton);
        add(mainPanel);
    }

    /**
     * This method adds listeners to the accountCreateListeners
     * @param listener is the listener being added
     */
    public void addAccountCreateListener(Consumer<Account> listener) {
        accountCreateListeners.add(listener);
    }
    /**
     * This method adds listeners to the backButtonListeners
     * @param listener is the listener being added
     */
    public void addBackListener(Runnable listener) {
        backButtonListeners.add(listener);
    }

    // checking if passwords match
    private boolean checkPasswordFields() {
        passwordMatchError.setVisible(false);
        passwordInvalidError.setVisible(false);
        boolean result;
        char[] passwordChar = passwordField.getPassword();
        char[] confirmPasswordChar = confirmPassword.getPassword();

        if (!Arrays.equals(passwordChar, confirmPasswordChar)) {
            passwordMatchError.setVisible(true); // passwords not matching
            result = false;
        } else if (isPasswordInvalid(passwordChar)) {
            passwordInvalidError.setVisible(true); // password not valid
            result = false;
        } else {
            result = true;
        }

        Arrays.fill(confirmPasswordChar, ' ');
        Arrays.fill(passwordChar, ' ');
        return result;
    }

    // checking if the username was taken
    private boolean isUsernameTaken(Database database, String username) {
        userNameTakenError.setVisible(false);
        Account accountFound = database.getCurrentState().accounts().stream()
                .filter(account -> account.username().equals(username))
                .findFirst()
                .orElse(null);
        if (accountFound != null) {
            userNameTakenError.setVisible(true); // username already taken
            return true;
        }
        return false;
    }

    // checking if the password was valid
    private boolean isPasswordInvalid(char[] password) {
        Pattern letter = Pattern.compile("[a-zA-Z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        // creating new string builder to compare with regex
        StringBuilder stringPassword = new StringBuilder();
        for (char c : password) {
            stringPassword.append(c);
        }
        return (password.length < 8
                || // checking for length
                !letter.matcher(stringPassword).find()
                || // checking for letters
                !digit.matcher(stringPassword).find()
                || // checking for numbers
                !special.matcher(stringPassword).find()); // checking for special characters
    }
}
