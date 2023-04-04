package cs2212.westernmaps.select;

import com.formdev.flatlaf.FlatClientProperties;
import cs2212.westernmaps.core.Building;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import javax.swing.*;

/*
 * TODO: - Add Javadoc comments.
 */

/**
 * Panel for the Building Selection screen.
 */
public class BuildingSelectPanel extends JPanel {
    private static final String PATH_TO_IMAGE = "/cs2212/westernmaps/building-select/mc.png";
    private final JList<Building> buildingList;
    private final JLabel noBuildingSelectedError;

    private final List<Runnable> logOutListeners = new ArrayList<>();
    private final List<Consumer<Building>> buildingSelectListeners = new ArrayList<>();

    /**
     * Construct a new BuildingSelectPanel and initialize all of its fields.
     *
     * @param buildings List of all buildings.
     */
    public BuildingSelectPanel(List<Building> buildings) {
        // This determines what MainWindow will use as its title.
        setName("Building Select");

        setLayout(new BorderLayout());

        // Create selection pane components
        JLabel heading = new JLabel("Select a Building:");
        heading.putClientProperty(FlatClientProperties.STYLE_CLASS, "h0");

        buildingList = new JList<>(buildings.toArray(Building[]::new));
        buildingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        buildingList.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        buildingList.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));

        var selectButton = new JButton("Select Building");
        selectButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        selectButton.addActionListener(e -> validateAndSubmit());

        // Create back button
        var logOutButton = new JButton("Log Out");
        logOutButton.addActionListener(e -> logOutListeners.forEach(Runnable::run));

        // Create Error label
        noBuildingSelectedError = new JLabel("Please select a building");
        noBuildingSelectedError.setHorizontalAlignment(SwingConstants.CENTER);
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
        selectPane.add(logOutButton, c);

        c.insets = new Insets(10, 0, 0, 0);
        c.gridx = 1;
        selectPane.add(heading, c);

        c.gridy = 2;
        c.insets = new Insets(20, 0, 20, 0);
        selectPane.add(selectButton, c);

        // Add building list
        c.gridx = 1;
        c.gridy = 1;
        c.fill = GridBagConstraints.BOTH;
        c.insets = new Insets(30, 30, 30, 30);
        c.weightx = 1.0f;
        c.weighty = 1.0f;
        selectPane.add(buildingList, c);

        // Add error when nothing selected
        c.gridx = 1;
        c.gridy = 2;
        c.weightx = 0;
        c.weighty = 0;
        c.insets = new Insets(0, 0, 100, 0);
        selectPane.add(noBuildingSelectedError, c);

        // Add layout to final content pane
        JPanel contentPane = new JPanel(new BorderLayout());
        contentPane.add(selectPane, BorderLayout.CENTER);

        // Add image to final content pane
        try {
            @Nullable InputStream imageFile = BuildingSelectPanel.class.getResourceAsStream(PATH_TO_IMAGE);
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

    /**
     * Add a listener for the "Log Out" button.
     *
     * @param listener is the listener that will be added to the logOutListeners
     */
    public void addLogOutListener(Runnable listener) {
        logOutListeners.add(listener);
    }

    /**
     * Add a listener for the "Select Building" button.
     *
     * @param listener is the listener that will be added to the buildingSelectListeners
     */
    public void addBuildingSelectListener(Consumer<Building> listener) {
        buildingSelectListeners.add(listener);
    }

    private void validateAndSubmit() {
        // Determine which building was selected.
        var selectedBuilding = buildingList.getSelectedValue();
        if (selectedBuilding != null) {
            noBuildingSelectedError.setVisible(false);
            buildingSelectListeners.forEach(listener -> listener.accept(selectedBuilding));
        } else {
            noBuildingSelectedError.setVisible(true);
            buildingList.clearSelection();
        }
    }
}
