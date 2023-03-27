package cs2212.westernmaps;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.help.HelpWindow;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;

/*
 * TODO: - Implement the Panel for displaying the current weather.
 *       - Add Javadoc comments.
 */

public class BuildingSelect extends JFrame implements ActionListener {
    final String[] BUILDING_LIST = {"Middlesex College", "Talbot College", "Recreation Centre"};
    final String PATH_TO_IMAGE = "/cs2212.westernmaps.building-select/mc.png";

    JList<String> list;

    public BuildingSelect() {
        super(Main.APPLICATION_NAME + ": Select a building");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create selection pane components
        JLabel heading = new JLabel("Select a building:");
        heading.putClientProperty(FlatClientProperties.STYLE_CLASS, "h0");

        list = new JList<>(BUILDING_LIST);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        list.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));

        JButton selectButton = new JButton("Select Building");
        selectButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        selectButton.addActionListener(this);

        JPanel weather = new JPanel();
        weather.add(new JLabel("weather"));

        // Create informative buttons
        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new HelpWindow.ShowAction());

        JButton aboutButton = new JButton("About");
        aboutButton.addActionListener(new AboutAction());

        JPanel helpBox = new JPanel();
        helpBox.setLayout(new BoxLayout(helpBox, BoxLayout.PAGE_AXIS));
        helpBox.add(helpButton);
        helpBox.add(Box.createRigidArea(new Dimension(0, 5)));
        helpBox.add(aboutButton);

        // Stack and center components in a grid bag layout
        JPanel selectPane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        c.insets = new Insets(10, 0, 0, 0);
        selectPane.add(heading, c);

        c.gridy = 2;
        c.insets = new Insets(20, 0, 20, 0);
        selectPane.add(selectButton, c);

        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 10);
        selectPane.add(weather, c);

        c.gridy = 0;
        selectPane.add(helpBox, c);

        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(30, 30, 30, 30);
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        selectPane.add(list, c);

        // Add layout to final content pane
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(selectPane, BorderLayout.CENTER);

        // Add image to final content pane
        try {
            @Nullable InputStream imageFile = BuildingSelect.class.getResourceAsStream(PATH_TO_IMAGE);
            BufferedImage imageBuffer = ImageIO.read(imageFile);
            ImageIcon imageIcon = new ImageIcon(
                    new ImageIcon(imageBuffer).getImage().getScaledInstance(540, 720, Image.SCALE_DEFAULT));
            JLabel image = new JLabel(imageIcon);
            image.setBounds(640, 0, 640, 720);
            contentPane.add(image, BorderLayout.LINE_END);
        } catch (IOException e) {
            System.out.println("Error opening file: " + PATH_TO_IMAGE);
        }
        setContentPane(contentPane);
        setPreferredSize(new Dimension(1280, 720));
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (list.getSelectedIndex()) {
            case 0 ->
            // Go to Middlesex College
            System.out.println("Middlesex College selected.");
            case 1 ->
            // Go to Talbot College
            System.out.println("Talbot College selected.");
            case 2 ->
            // Go to Recreation Centre
            System.out.println("Recreation Centre selected.");
        }
    }

    private class AboutAction extends AbstractAction {
        public AboutAction() {
            super("About " + Main.APPLICATION_NAME);
            putValue(Action.MNEMONIC_KEY, KeyEvent.VK_A);
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            JOptionPane.showMessageDialog(
                    BuildingSelect.this,
                    "Information about the application should go here.",
                    getName(),
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
