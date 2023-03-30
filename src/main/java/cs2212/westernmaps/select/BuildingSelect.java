package cs2212.westernmaps.select;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.Main;
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

public class BuildingSelect extends JPanel implements ActionListener {
    final String[] BUILDING_LIST = {"Middlesex College", "Talbot College", "Recreation Centre"};
    final String PATH_TO_IMAGE = "/cs2212/westernmaps/building-select/mc.png";

    private JList<String> list;

    private JButton backButton;

    private JButton selectButton;

    private JLabel noBuildingSelectedError;

    public BuildingSelect() {

        setLayout(new BorderLayout());

        // Create selection pane components
        JLabel heading = new JLabel("Select a Building:");
        heading.putClientProperty(FlatClientProperties.STYLE_CLASS, "h0");

        list = new JList<>(BUILDING_LIST);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        list.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));

        selectButton = new JButton("Select Building");
        selectButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        selectButton.addActionListener(this);

        JPanel weather = new JPanel();
        // Temporary
        weather.add(new JLabel("weather"));

        // Create informative buttons
        JButton helpButton = new JButton("Help");
        helpButton.addActionListener(new HelpWindow.ShowAction());

        JButton aboutButton = new JButton("About");
        aboutButton.addActionListener(new AboutAction());

        JPanel helpBox = new JPanel();
        helpBox.setLayout(new BoxLayout(helpBox, BoxLayout.PAGE_AXIS));
        helpBox.add(Box.createRigidArea(new Dimension(0, 10)));
        helpBox.add(helpButton);
        helpBox.add(Box.createRigidArea(new Dimension(0, 5)));
        helpBox.add(aboutButton);

        // Create back button
        backButton = new JButton("Back");

        // Create Error label
        noBuildingSelectedError = new JLabel("Please select a building");
        noBuildingSelectedError.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        noBuildingSelectedError.setForeground(UIManager.getColor("Actions.Red"));
        noBuildingSelectedError.setVisible(false);

        // Stack and center components in a grid bag layout
        JPanel selectPane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        // Add main components
        c.gridx = 0;
        c.gridy = 0;
        c.insets = new Insets(5, 5, 0, 0);
        selectPane.add(backButton, c);

        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 1;
        selectPane.add(heading, c);

        c.gridy = 2;
        c.insets = new Insets(20, 0, 20, 0);
        selectPane.add(selectButton, c);

        // Add right-most components
        c.gridx = 2;
        c.insets = new Insets(0, 0, 0, 10);
        selectPane.add(weather, c);

        c.gridy = 0;
        selectPane.add(helpBox, c);

        // Add building list
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(30, 30, 30, 30);
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        selectPane.add(list, c);

        // Add error when nothing selected
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 290, 100, 0);
        selectPane.add(noBuildingSelectedError, c);

        // Add layout to final content pane
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(selectPane, BorderLayout.CENTER);

        // Add image to final content pane
        try {
            @Nullable InputStream imageFile = BuildingSelect.class.getResourceAsStream(PATH_TO_IMAGE);
            if (imageFile == null) throw new IOException();
            BufferedImage imageBuffer = ImageIO.read(imageFile);
            ImageIcon imageIcon = new ImageIcon(
                    new ImageIcon(imageBuffer).getImage().getScaledInstance(540, 720, Image.SCALE_DEFAULT));
            JLabel image = new JLabel(imageIcon);
            image.setBounds(640, 0, 640, 720);
            contentPane.add(image, BorderLayout.LINE_END);
        } catch (IOException e) {
            System.out.println("Error opening file: " + PATH_TO_IMAGE);
        }

        add(contentPane);
    }

    public JButton getBackButton() {
        return backButton;
    }

    public JButton getSelectButton() {
        return selectButton;
    }

    public JList<String> getList() {
        return list;
    }

    public JLabel getNoBuildingSelectedError() {
        return noBuildingSelectedError;
    }

    @Override
    public void actionPerformed(ActionEvent e) {}

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
