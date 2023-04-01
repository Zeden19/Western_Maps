package cs2212.westernmaps.login;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.core.Account;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;

public final class LoginPanel extends JPanel {
    private final JTextField usernameField;
    private final JLabel invalidUserError;

    private final List<Consumer<Account>> loginListeners = new ArrayList<>();
    private final List<Runnable> createAccountClickListeners = new ArrayList<>();

    public LoginPanel() {
        // This determines what MainWindow will use as its title.
        setName("Sign In");

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
        var passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passwordField.setColumns(20);
        passwordField.putClientProperty(FlatClientProperties.PLACEHOLDER_TEXT, "Password");

        // Sign in button
        var signInButton = new JButton("Sign In");
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        signInButton.addActionListener(e -> validateAndSubmit());

        // Create account link
        var createAccountLink = new LinkButton("Create an Account");
        createAccountLink.setAlignmentX(Component.CENTER_ALIGNMENT);
        createAccountLink.addActionListener(e -> createAccountClickListeners.forEach(Runnable::run));

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

    public void addLoginListener(Consumer<Account> listener) {
        loginListeners.add(listener);
    }

    public void addCreateAccountClickListener(Runnable listener) {
        createAccountClickListeners.add(listener);
    }

    private void validateAndSubmit() {
        // temporary accounts until database gets implemented
        var accounts =
                List.of(new Account("regular user", new byte[3], false), new Account("developer", new byte[3], true));

        var username = usernameField.getText();
        var account =
                accounts.stream().filter(a -> a.username().equals(username)).findFirst();

        if (account.isPresent()) {
            invalidUserError.setVisible(false);
            loginListeners.forEach(listener -> listener.accept(account.get()));
        } else {
            invalidUserError.setVisible(true);
        }
    }
}
