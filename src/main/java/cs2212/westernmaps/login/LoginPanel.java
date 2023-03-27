package cs2212.westernmaps.login;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.GridBagLayout;
import java.awt.event.ActionListener;
import javax.swing.*;

public final class LoginPanel extends JPanel {
    public LoginPanel() {
        // When a GridBagLayout has one child, it will center it.
        setLayout(new GridBagLayout());

        // TODO: Put account icon here as in the wireframe.

        var title = new JLabel("Sign In");
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

        var signInButton = new JButton("Sign In");
        signInButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        var createAccountLink = new LinkButton("Create an Account");
        createAccountLink.setAlignmentX(Component.CENTER_ALIGNMENT);

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
        panel.add(createAccountLink);

        add(panel);

        createAccountLink.addActionListener(e -> {
            {
                remove(panel);
                add(new CreateAccountPanel());
                revalidate();
                repaint();
            }
        });
    }

    public void addCreateAccountLinkActionListener(ActionListener listener) {}
}
