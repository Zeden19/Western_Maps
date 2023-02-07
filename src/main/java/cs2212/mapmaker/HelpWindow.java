package cs2212.mapmaker;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.annotation.Nullable;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JFrame;

public class HelpWindow extends JFrame {
    private static @Nullable HelpWindow openedWindow = null;

    public HelpWindow() {
        super(Main.APPLICATION_NAME + " Help");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setPreferredSize(new Dimension(800, 600));
        pack();
    }

    public static class ShowAction extends AbstractAction {
        public ShowAction() {
            super("Help");
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_H);
            putValue(Action.SHORT_DESCRIPTION, "Show the help browser.");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            if (openedWindow == null) {
                // This is the first time the help window has been opened.
                openedWindow = new HelpWindow();
                openedWindow.setVisible(true);
            } else if (!openedWindow.isVisible()) {
                // The help window has been opened and then closed before.
                // pack() must be called here because the window is disposed.
                openedWindow.pack();
                openedWindow.setVisible(true);
            } else {
                // The help window is already open.
                openedWindow.requestFocus();
            }
        }
    }
}
