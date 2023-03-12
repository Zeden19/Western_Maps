package cs2212.westernmaps;

import cs2212.westernmaps.help.HelpWindow;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class MainWindow extends JFrame {
    public MainWindow() {
        super(Main.APPLICATION_NAME);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        setJMenuBar(createMenuBar());

        setPreferredSize(new Dimension(1280, 720));
        pack();
    }

    private JMenuBar createMenuBar() {
        System.out.println("hi");
        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(new QuitAction());

        var helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(new HelpWindow.ShowAction());
        helpMenu.add(new AboutAction());

        var menuBar = new JMenuBar();
        menuBar.add(fileMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    private class AboutAction extends AbstractAction {
        public AboutAction() {
            super("About " + Main.APPLICATION_NAME);
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JOptionPane.showMessageDialog(
                    MainWindow.this,
                    "Information about the application should go here.",
                    getName(),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private class QuitAction extends AbstractAction {
        public QuitAction() {
            super("Quit");
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_Q);
            putValue(Action.SHORT_DESCRIPTION, "Quit the application.");
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            MainWindow.this.dispose();
        }
    }
}
