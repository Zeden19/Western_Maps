package cs2212.westernmaps;

import com.formdev.flatlaf.FlatClientProperties;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

/*
 * TODO: - Implement the Panel for displaying the current weather.
 *       - Add Javadoc comments.
 */

public class BuildingSelect extends JFrame implements ActionListener {
    final String[] BUILDING_LIST = {"Middlesex College", "Talbot College", "Recreation Centre"};
    final String PATH_TO_IMAGE = "mc.png";

    JList list;

    public BuildingSelect() {
        super(Main.APPLICATION_NAME + ": Building Selection");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Create selection pane components
        JLabel heading = new JLabel("Select a building:");
        heading.putClientProperty(FlatClientProperties.STYLE_CLASS, "h0");

        list = new JList(BUILDING_LIST);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        list.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        JButton selectButton = new JButton("Select Building");
        selectButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        selectButton.addActionListener(this);

        JPanel weather = new JPanel();
        weather.add(new JLabel("weather"));

        // Stack and center components in a grid bag layout
        JPanel selectPane = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 1;
        c.gridy = 0;
        selectPane.add(heading, c);

        c.gridy = 2;
        c.insets = new Insets(20, 0, 20, 0);
        selectPane.add(selectButton, c);

        c.gridx = 2;
        selectPane.add(weather, c);

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
            BufferedImage imageFile = ImageIO.read(new File(PATH_TO_IMAGE));
            ImageIcon imageIcon =
                    new ImageIcon(new ImageIcon(imageFile).getImage().getScaledInstance(540, 720, Image.SCALE_DEFAULT));

            JLabel image = new JLabel(imageIcon);
            image.setBounds(640, 0, 640, 720);
            contentPane.add(image, BorderLayout.LINE_END);
        } catch (IOException e) {
            System.out.println("Error: Couldn't open image: " + PATH_TO_IMAGE);
            System.out.println("Current working directory: " + System.getProperty("user.dir"));
        }

        setContentPane(contentPane);

        setPreferredSize(new Dimension(1280, 720));
        pack();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (list.getSelectedIndex()) {
            case 0:
                // Go to Middlesex College
                System.out.println("Middlesex College selected.");
                break;
            case 1:
                // Go to Talbot College
                System.out.println("Talbot College selected.");
                break;
            case 2:
                // Go to Recreation Centre
                System.out.println("Recreation Centre selected.");
                break;
        }
    }
}
