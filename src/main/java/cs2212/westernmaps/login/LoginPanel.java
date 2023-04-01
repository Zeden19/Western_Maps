package cs2212.westernmaps.login;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.core.Account;
import java.awt.*;
import java.util.Objects;
import javax.swing.*;

public final class LoginPanel extends JPanel {
    private LinkButton createAccountLink;
    private JButton signInButton;
    private JPasswordField passwordField;
    private JTextField usernameField;
    private JLabel invalidUserError;
    private boolean developerMode;

    public LoginPanel() {
        // When a GridBagLayout has one child, it will center it.
        setLayout(new GridBagLayout());

        // TODO: Put account icon here as in the wireframe.

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
        signInButton = new JButton("Sign In");
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Create account link
        createAccountLink = new LinkButton("Create an Account");
        createAccountLink.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Error message for invalid user
        invalidUserError = new JLabel("Username not found");
        invalidUserError.setAlignmentX(Component.CENTER_ALIGNMENT);
        invalidUserError.setForeground(UIManager.getColor("Actions.Red"));
        invalidUserError.setVisible(false);

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
        panel.add(Box.createVerticalStrut(8));
        panel.add(createAccountLink);

        // Add the panel to the layout
        add(panel);
    }

    public boolean checkValidLogin() {
        // temporary accounts until database gets implemented
        Account testUser = new Account("regular User", new byte[3], false);
        Account testDeveloper = new Account("developer", new byte[3], true);

        invalidUserError.setVisible(false);
        if (!Objects.equals(usernameField.getText(), "regular user")
                && !Objects.equals(usernameField.getText(), "developer")) {
            invalidUserError.setVisible(true);
            return false;
        }

        if (Objects.equals(usernameField.getText(), "regular user")) {
            developerMode = false;
        }
        if (Objects.equals(usernameField.getText(), "developer")) {
            developerMode = true;
        }

        return true;
    }

    public LinkButton getCreateAccountLink() {
        return createAccountLink;
    }

    public JButton getSignInButton() {
        return signInButton;
    }

    public boolean getIsDeveloper() {
        return developerMode;
    }
}
