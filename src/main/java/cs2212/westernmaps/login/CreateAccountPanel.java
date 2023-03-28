package cs2212.westernmaps.login;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import java.util.regex.Pattern;
import javax.swing.*;
import javax.swing.border.EmptyBorder;

public final class CreateAccountPanel extends JPanel {

    private JButton back;
    private JButton createAccountButton;
    private JPasswordField passwordField;
    private JPasswordField confirmPassword;
    private JLabel passwordMatchError;
    private JLabel passwordInvalidError;

    public CreateAccountPanel() {
        // Setting layout for the whole panel
        setLayout(new OverlayLayout(this));
        setBorder(new EmptyBorder(10, 10, 10, 10));

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
        createAccountButton = new JButton("Create Account");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        add(back);
        add(mainPanel);
    }

    // checking if password is valid, checking length, symbols, number and character
    boolean isPasswordValid(char[] password) {
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

    public JButton getBackButton() {
        return back;
    }

    public JButton getCreateAccountButton() {
        return createAccountButton;
    }

    public JPasswordField getPassword() {
        return passwordField;
    }

    public JPasswordField getConfirmPassword() {
        return confirmPassword;
    }

    public JLabel getPasswordMatchError() {
        return passwordMatchError;
    }

    public JLabel getPasswordInvalidError() {
        return passwordInvalidError;
    }
}
