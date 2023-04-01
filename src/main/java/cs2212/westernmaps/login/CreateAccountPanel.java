package cs2212.westernmaps.login;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.core.Account;
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

public final class CreateAccountPanel extends JPanel {
    private final JPasswordField passwordField;
    private final JPasswordField confirmPassword;
    private final JLabel passwordMatchError;
    private final JLabel passwordInvalidError;

    private final List<Consumer<Account>> accountCreateListeners = new ArrayList<>();

    public CreateAccountPanel() {
        // This determines what MainWindow will use as its title.
        setName("Create Account");

        // Setting layout for the whole panel
        setLayout(new OverlayLayout(this));
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // back button
        var backButton = new JButton("Back");
        backButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        backButton.setAlignmentY(Component.TOP_ALIGNMENT);

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
            if (!checkPasswordFields()) {
                return;
            }
            // TODO: Hash the provided password and use it for the account.
            var account = new Account(usernameField.getText(), new byte[0], false);
            accountCreateListeners.forEach(listener -> listener.accept(account));
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
        panel.add(Box.createVerticalStrut(8));

        panel.add(createAccountButton);

        // adding boxlayout into the gridbag layout
        mainPanel.add(panel);

        // adding back button and gridbag layout into the overlay layout
        add(backButton);
        add(mainPanel);
    }

    public void addAccountCreateListener(Consumer<Account> listener) {
        accountCreateListeners.add(listener);
    }

    private boolean checkPasswordFields() {
        passwordMatchError.setVisible(false);
        passwordInvalidError.setVisible(false);
        // checking if the password was valid, if not displaying error
        if (!Arrays.equals(passwordField.getPassword(), confirmPassword.getPassword())) {
            passwordMatchError.setVisible(true); // passwords not matching
            return false;
        } else if (!isPasswordValid(passwordField.getPassword())) {
            passwordInvalidError.setVisible(true); // password not valid
            return false;
        } else {
            return true;
        }
    }

    private boolean isPasswordValid(char[] password) {
        Pattern letter = Pattern.compile("[a-zA-Z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

        // creating new string builder to compare with regex
        StringBuilder stringPassword = new StringBuilder();
        for (char c : password) {
            stringPassword.append(c);
        }
        return (password.length >= 8
                && // checking for length
                letter.matcher(stringPassword).find()
                && // checking for letters
                digit.matcher(stringPassword).find()
                && // checking for numbers
                special.matcher(stringPassword).find()); // checking for special characters
    }
}
