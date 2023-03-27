package cs2212.westernmaps;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import java.util.Arrays;
import java.util.regex.Pattern;
import javax.swing.*;

public final class CreateAccountPanel extends JPanel {
    public CreateAccountPanel() {
        // When a GridBagLayout has one child, it will center it.
        setLayout(new OverlayLayout(this));

        var back = new JButton("Back");
        back.setAlignmentX(Component.LEFT_ALIGNMENT);
        back.setAlignmentY(Component.TOP_ALIGNMENT);

        var title = new JLabel("Create Account");
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.putClientProperty(FlatClientProperties.STYLE_CLASS, "h1");

        var usernameField = new JTextField();
        usernameField.setAlignmentX(Component.CENTER_ALIGNMENT);
        usernameField.setColumns(20);
        usernameField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Username");

        var passwordSpecifiers = new JLabel("8 or more letters, 1 symbol, 1 number");
        passwordSpecifiers.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordSpecifiers.setFont(new Font("Arial", Font.ITALIC, 10));

        var passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setColumns(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");

        var confirmPassword = new JPasswordField();
        confirmPassword.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmPassword.setColumns(20);
        confirmPassword.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Confirm Password");

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

        var mainPanel = new JPanel();
        mainPanel.setLayout(new GridBagLayout());

        var panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.PAGE_AXIS));

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
        panel.add(passwordUnviableError);

        panel.add(createAccountButton);

        mainPanel.add(panel);

        add(back);
        add(mainPanel);

        back.addActionListener(e -> {
            remove(mainPanel);
            add(new LoginPanel());
            revalidate();
            repaint();
        });

        createAccountButton.addActionListener(e -> {
            passwordMatchError.setVisible(false);
            passwordUnviableError.setVisible(false);

            if (!Arrays.toString(passwordField.getPassword()).equals(Arrays.toString(confirmPassword.getPassword())))
                passwordMatchError.setVisible(true); // passwords not matching
            else if (!isPasswordViable(passwordField.getPassword())) // password not viable
            passwordUnviableError.setVisible(true);
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
