package cs2212.westernmaps.login;

import java.awt.*;
import javax.swing.*;

public final class LoginWindow extends JFrame {

    private LoginPanel loginPanel;
    private CreateAccountPanel createAccountPanel;
    private JPanel cardPanel;

    public LoginWindow() {
        super("Sign in");

        loginPanel = new LoginPanel();
        createAccountPanel = new CreateAccountPanel();
        cardPanel = new JPanel(new CardLayout());

        cardPanel.add(loginPanel, "login");
        cardPanel.add(createAccountPanel, "create");

        loginPanel.createAccountLink.addActionListener(e -> changeToCreateAccount());
        createAccountPanel.back.addActionListener(e -> changeToLogin());

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        add(cardPanel);
        setPreferredSize(new Dimension(640, 480));
        pack();
    }

    public void changeToCreateAccount() {
        setTitle("Create Account");
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "create");
    }

    public void changeToLogin() {
        setTitle("Sign in");
        ((CardLayout) cardPanel.getLayout()).show(cardPanel, "login");
    }
}
