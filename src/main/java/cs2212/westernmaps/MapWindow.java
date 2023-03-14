package cs2212.westernmaps;

import cs2212.westernmaps.help.HelpWindow;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import javax.swing.*;

public class MapWindow extends JFrame {

    public MapWindow() {
        super(Main.APPLICATION_NAME);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setJMenuBar(createMenuBar());
        setTitle("Map screen");

        // todo make this get the building we are currently on
        JLabel building = new JLabel("MiddleSex College"); // creating label
        building.setBounds(530, 60, 400, 40);
        building.setFont(new Font("Arial", Font.PLAIN, 25)); // settng size of label
        add(building); // adding label on frame

        setLayout(null);
        setVisible(true);

        JButton back = new JButton("Back");
        back.setBounds(8, 15, 85, 30);
        add(back);

        setPreferredSize(new Dimension(1280, 720));
        pack();
    }

    private JMenuBar createMenuBar() {
        var fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);
        fileMenu.add(new MapWindow.QuitAction());

        var helpMenu = new JMenu("Help");
        helpMenu.setMnemonic(KeyEvent.VK_H);
        helpMenu.add(new HelpWindow.ShowAction());
        helpMenu.add(new MapWindow.AboutAction());

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
                    MapWindow.this,
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
            MapWindow.this.dispose();
        }
    }
}
