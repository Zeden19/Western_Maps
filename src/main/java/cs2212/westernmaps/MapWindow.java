package cs2212.westernmaps;

import cs2212.westernmaps.core.Floor;
import cs2212.westernmaps.core.Layer;
import cs2212.westernmaps.core.POI;
import cs2212.westernmaps.help.HelpWindow;
import java.awt.*;
import java.awt.event.*;
import java.net.MalformedURLException;
import java.nio.file.Path;
import javax.swing.*;

public class MapWindow {
    JFrame frame = new JFrame("Map screen");

    public MapWindow() throws MalformedURLException {

        JPanel panel = new JPanel(new GridBagLayout());

        // Add the back button to the top left corner
        JButton backButton = new JButton("Back");
        GridBagConstraints backButtonConstraints = new GridBagConstraints();
        backButtonConstraints.gridx = 0;
        backButtonConstraints.gridy = 0;
        backButtonConstraints.anchor = GridBagConstraints.NORTHWEST;
        backButtonConstraints.weightx = 0.1;
        backButtonConstraints.weighty = 0.1;
        backButtonConstraints.insets = new Insets(10, 10, 10, 10);
        panel.add(backButton, backButtonConstraints);

        // Add the title label near the top
        JLabel titleLabel = new JLabel("Middlesex College");
        GridBagConstraints titleLabelConstraints = new GridBagConstraints();
        titleLabelConstraints.gridx = 1;
        titleLabelConstraints.gridy = 1;
        titleLabelConstraints.weighty = 1.0;
        titleLabelConstraints.weightx = 1.0;
        titleLabelConstraints.anchor = GridBagConstraints.PAGE_START;
        titleLabelConstraints.insets = new Insets(10, 0, 10, 0);
        panel.add(titleLabel, titleLabelConstraints);

        // Add padding on the left with placeholder text that says "LAYERS"
        JLabel leftPaddingLabel = new JLabel("LAYERS");
        GridBagConstraints leftPaddingLabelConstraints = new GridBagConstraints();
        leftPaddingLabelConstraints.gridx = 0;
        leftPaddingLabelConstraints.gridy = 1;
        leftPaddingLabelConstraints.weightx = 0.1;
        leftPaddingLabelConstraints.weighty = 1;
        leftPaddingLabelConstraints.anchor = GridBagConstraints.WEST;
        leftPaddingLabelConstraints.insets = new Insets(100, 100, 100, 50);
        panel.add(leftPaddingLabel, leftPaddingLabelConstraints);

        // Add padding on the right that is bigger than the left and says "LISTS"
        JLabel rightPaddingLabel = new JLabel("LISTS");
        GridBagConstraints rightPaddingLabelConstraints = new GridBagConstraints();
        rightPaddingLabelConstraints.gridx = 2;
        rightPaddingLabelConstraints.gridy = 1;
        rightPaddingLabelConstraints.weightx = 0.1;
        rightPaddingLabelConstraints.weighty = 1;
        rightPaddingLabelConstraints.anchor = GridBagConstraints.EAST;
        rightPaddingLabelConstraints.insets = new Insets(100, 50, 100, 100);
        panel.add(rightPaddingLabel, rightPaddingLabelConstraints);

        // search bar
        JTextField search = new JTextField("Click here to search");
        GridBagConstraints searchConstraints = new GridBagConstraints();
        searchConstraints.gridx = 0;
        searchConstraints.gridy = 1;
        searchConstraints.weighty = 1.0;
        searchConstraints.weightx = 1.0;
        searchConstraints.anchor = GridBagConstraints.PAGE_START;
        searchConstraints.insets = new Insets(10, 0, 10, 0);
        search.addFocusListener(new FocusListener() {
            @Override
            public void focusGained(FocusEvent e) {
                search.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                search.setText("Click here to search");
            }
        });
        panel.add(search, searchConstraints);

        // create POI Button
        JButton createPOI = new JButton("Create POI");
        GridBagConstraints createPOIConstraints = new GridBagConstraints();
        createPOIConstraints.gridx = 2;
        createPOIConstraints.gridy = 1;
        createPOIConstraints.weighty = 1.0;
        createPOIConstraints.weightx = 1.0;
        createPOIConstraints.anchor = GridBagConstraints.PAGE_START;
        createPOIConstraints.insets = new Insets(10, 0, 10, 0);
        panel.add(createPOI, createPOIConstraints);

        // JPanel placeholder
        JPanel mapPanel = new JPanel(null);
        GridBagConstraints mapConstraints = new GridBagConstraints();
        mapConstraints.gridy = 1;
        mapConstraints.gridx = 1;
        mapConstraints.fill = GridBagConstraints.BOTH;
        mapConstraints.weightx = 1.0;
        mapConstraints.weighty = 1.0;
        panel.add(mapPanel, mapConstraints);

        JLabel text = new JLabel("MAP HERE");
        text.setBounds(50, 50, 100, 10);
        mapPanel.add(text);

        // test of a poi that gets pressed
        ImageIcon icon = new ImageIcon("locator.png");
        JLabel label = new JLabel(icon);
        POI testPoi = new POI(
                "GradClub",
                "The grad club stuff",
                200,
                200,
                false,
                new Floor("test", "test", Path.of("test")),
                Layer.UTILITIES);
        label.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent me) {
                displayInfoPOI(testPoi);
            }
        });
        label.setBounds(testPoi.x(), testPoi.y(), 28, 60);
        mapPanel.add(label);

        // filler
        JLabel filler = new JLabel();
        GridBagConstraints fillerConstraints = new GridBagConstraints();
        fillerConstraints.weighty = 1;
        fillerConstraints.weightx = 1;
        fillerConstraints.gridx = 1;
        fillerConstraints.gridy = 3;
        panel.add(filler, fillerConstraints);

        //
        //
        //        // map --> finsih
        //        File map = new File("Middlesex-2.svg");
        //        SVGUniverse universe = new SVGUniverse();
        //        SVGDiagram diagram = universe.getDiagram(universe.loadSVG(map.toURL()));
        //
        //
        //
        //
        //
        //        // test of a poi that gets pressed
        //        ImageIcon icon = new ImageIcon("locator.png");
        //        JLabel label = new JLabel(icon);
        //        label.addMouseListener(new MouseAdapter() {
        //            public void mouseClicked(MouseEvent me) {
        //                System.out.println("CLICKED");
        //            }
        //        });
        //        label.setBounds(550, 500, 28, 60);
        //        add(label, constraints);
        //

        frame.setContentPane(panel);
        frame.setPreferredSize(new Dimension(1280, 720));
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
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
                    frame,
                    "Information about the application should go here.",
                    frame.getName(),
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
            frame.dispose();
        }
    }

    private void displayInfoPOI(POI poi) {
        JFrame testFrame = new JFrame();
        JPanel infoDisplay = new JPanel();
        infoDisplay.setLayout(new BoxLayout(infoDisplay, BoxLayout.PAGE_AXIS));

        JLabel name = new JLabel("Name: " + poi.name());
        name.setFont(new Font("Arial", Font.PLAIN, 30));
        name.setAlignmentX(JLabel.CENTER_ALIGNMENT);
        name.setAlignmentY(JLabel.CENTER_ALIGNMENT);

        infoDisplay.add(name);
        infoDisplay.add(Box.createHorizontalGlue());
        infoDisplay.add(new JLabel("Room: Not set yet"));
        infoDisplay.add(new JLabel("Layer: " + poi.layer().name()));
        infoDisplay.add(new JLabel("Favourite? " + poi.favorite()));
        infoDisplay.add(new JLabel("Coordinates: " + poi.x() + ", " + poi.y()));
        infoDisplay.add(new JLabel("Description: " + poi.description()));

        testFrame.setContentPane(infoDisplay);
        testFrame.setPreferredSize(new Dimension(600, 400));
        testFrame.setLocationRelativeTo(null);
        testFrame.pack();
        testFrame.setVisible(true);
    }
}
