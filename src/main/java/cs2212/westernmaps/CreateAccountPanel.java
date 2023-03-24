package cs2212.westernmaps;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.swing.*;

public final class CreateAccountPanel extends JPanel {

    private static final String PASSWORD_REQUIREMENTS_TEXT =
            "<html>At least 8 characters<br/>At least 1 number<br/>At least 1 symbol</html>";

    public CreateAccountPanel() {
        // When a GridBagLayout has one child, it will center it.
        setLayout(new GridBagLayout());

        var backButton = new JButton("Back");
        backButton.setHorizontalAlignment(SwingConstants.LEFT);
        backButton.setVerticalAlignment(SwingConstants.TOP);

        var title = new JLabel("Create Account");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");

        var usernameField = new JTextField();
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setColumns(20);
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");

        var passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setColumns(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");

        var passwordRequirementsLabel = new JLabel(PASSWORD_REQUIREMENTS_TEXT);
        var passwordRequirementsBox = Box.createHorizontalBox();
        passwordRequirementsBox.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordRequirementsBox.add(passwordRequirementsLabel);

        var confirmPasswordField = new JPasswordField();
        confirmPasswordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPasswordField.setColumns(20);
        confirmPasswordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirm Password");

        var createAccountButton = new JButton("Create Account");
        createAccountButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        var passwordMatchError = new JLabel("Passwords do not match");
        passwordMatchError.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordMatchError.setVisible(false);
        passwordMatchError.setForeground(Color.RED);

        var passwordUnviableError = new JLabel("Password not strong enough.");
        passwordUnviableError.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordUnviableError.setVisible(false);
        passwordUnviableError.setForeground(Color.RED);

        var userUnavailableError = new JLabel("Username already taken.");
        userUnavailableError.setAlignmentX(Component.CENTER_ALIGNMENT);
        userUnavailableError.setVisible(false);
        userUnavailableError.setForeground(Color.RED);

        var innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
        innerPanel.add(backButton);
        innerPanel.add(title);
        innerPanel.add(Box.createVerticalStrut(24));
        innerPanel.add(usernameField);
        innerPanel.add(Box.createVerticalStrut(8));
        innerPanel.add(passwordField);
        innerPanel.add(Box.createVerticalStrut(4));
        innerPanel.add(passwordRequirementsBox);
        innerPanel.add(Box.createVerticalStrut(8));
        innerPanel.add(confirmPasswordField);
        innerPanel.add(Box.createVerticalStrut(16));

        innerPanel.add(passwordMatchError);
        innerPanel.add(passwordUnviableError);
        innerPanel.add(userUnavailableError);

        innerPanel.add(createAccountButton);

        var outerPanel = new JPanel();
        outerPanel.setLayout(new GridBagLayout());
        add(innerPanel);

        backButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                remove(innerPanel);
                remove(outerPanel);

                add(new LoginPanel());
                revalidate();
                repaint();
            }
        });

        createAccountButton.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                passwordMatchError.setVisible(false);
                passwordUnviableError.setVisible(false);
                userUnavailableError.setVisible(false);

                if (!Arrays.toString(passwordField.getPassword()) // passwords not matching
                        .equals(Arrays.toString(confirmPasswordField.getPassword())))
                    passwordMatchError.setVisible(true);
                else if (!isPasswordViable(passwordField.getPassword())) // password not viable
                passwordUnviableError.setVisible(true);
            }
        });
    }

    public boolean isPasswordViable(char[] password) {
        Pattern letter = Pattern.compile("[a-zA-Z]");
        Pattern digit = Pattern.compile("[0-9]");
        Pattern special = Pattern.compile("[!@#$%&*()_+=|<>?{}\\[\\]~-]");

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
