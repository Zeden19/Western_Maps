package cs2212.westernmaps;

import java.awt.Dimension;
import javax.swing.JFrame;

public final class LoginWindow extends JFrame {
    public LoginWindow() {
        super("Sign in");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        add(new LoginPanel());
        // add(new CreateAccountPanel());

        setPreferredSize(new Dimension(640, 480));
        pack();
    }
}
