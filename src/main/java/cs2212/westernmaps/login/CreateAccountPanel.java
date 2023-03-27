package cs2212.westernmaps.login;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.swing.*;

public final class CreateAccountPanel extends JPanel {

    JButton back;

    public CreateAccountPanel() {
        // Setting layout for the whole panel
        setLayout(new OverlayLayout(this));

        // back button
        back = new JButton("Back");
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.setAlignmentY(Component.TOP_ALIGNMENT);

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
        var passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setColumns(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");

        // Confirm password field, where user would retype password
        var confirmPassword = new JPasswordField();
        confirmPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPassword.setColumns(20);
        confirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirm Password");

        // Create account Button
        var createAccountButton = new JButton("Create Account");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // error if password doesn't match
        var passwordMatchError = new JLabel("Passwords do not match");
        passwordMatchError.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordMatchError.setVisible(false);
        passwordMatchError.setForeground(Color.RED);

        // error if password is invalid
        var passwordInvalidError = new JLabel("Password not strong enough.");
        passwordInvalidError.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordInvalidError.setVisible(false);
        passwordInvalidError.setForeground(Color.RED);

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

        panel.add(passwordMatchError);
        panel.add(passwordInvalidError);

        panel.add(createAccountButton);

        mainPanel.add(panel);

        add(back);
        add(mainPanel);

        // checking if password is valid
        createAccountButton.addActionListener(e -> {
            passwordMatchError.setVisible(false);
            passwordInvalidError.setVisible(false);

            if (!Arrays.toString(passwordField.getPassword()).equals(Arrays.toString(confirmPassword.getPassword())))
                passwordMatchError.setVisible(true); // passwords not matching
            else if (!isPasswordViable(passwordField.getPassword())) // password not valid
            passwordInvalidError.setVisible(true);
        });
    }

    // checking if password is valid, checking length, symbols, number and character
    private boolean isPasswordViable(char[] password) {
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
