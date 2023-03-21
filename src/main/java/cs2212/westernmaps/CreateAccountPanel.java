package cs2212.westernmaps;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.Component;
import java.awt.GridBagLayout;
import javax.swing.*;

public final class CreateAccountPanel extends JPanel {
    private static final String PASSWORD_REQUIREMENTS_TEXT =
            "<html>At least 8 characters<br/>At least 1 number<br/>At least 1 symbol</html>";

    public CreateAccountPanel() {
        setLayout(new GridBagLayout());

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

        var innerPanel = new JPanel();
        innerPanel.setLayout(new BoxLayout(innerPanel, BoxLayout.PAGE_AXIS));
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
        innerPanel.add(createAccountButton);

        var outerPanel = new JPanel();
        outerPanel.setLayout(new GridBagLayout());

        add(innerPanel);
    }
}
