package cs2212.westernmaps;

import com.kitfox.svg.SVGDiagram;
import com.kitfox.svg.SVGUniverse;
import cs2212.westernmaps.help.HelpWindow;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.net.MalformedURLException;
import javax.swing.*;

public class MapWindow extends JFrame {

    public MapWindow() throws MalformedURLException {
        super(Main.APPLICATION_NAME);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setJMenuBar(createMenuBar());
        setTitle("Map screen");

        // todo make this get the building we are currently on
        JLabel building = new JLabel("MiddleSex College"); // creating label
        building.setBounds(530, 60, 400, 40);
        building.setFont(new Font("Arial", Font.PLAIN, 25)); // setting size of label
        add(building); // adding label on frame

        // probably want to make this a layout instead of doing it like this

        // map --> finsih
        File map = new File("Middlesex-2.svg");
        SVGUniverse universe = new SVGUniverse();
        SVGDiagram diagram = universe.getDiagram(universe.loadSVG(map.toURL()));
        JPanel map_placeHolder = new JPanel();
        add(map_placeHolder);

        // back button
        JButton back = new JButton("Back");
        back.setBounds(8, 15, 85, 30);
        add(back);

        // window size
        setPreferredSize(new Dimension(1280, 720));

        // test of a poi that gets pressed
        ImageIcon icon = new ImageIcon("locator.png");
        JLabel label = new JLabel(icon);
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                System.out.println("CLICKED");
            }
        });
        label.setBounds(550, 500, 28, 60);
        add(label);

        // making map display with all elemnt
        setLayout(null);
        setVisible(true);
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
