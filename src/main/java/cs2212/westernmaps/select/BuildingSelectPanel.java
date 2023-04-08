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

/**
 * Panel for the Building Selection screen.
 *
 * <p>The panel that allows the user to select a building. This class will send
 * the selected building to the map panel, which will then display the map for
 * that building.</p>
 *
 * <p>The panel is made with the {@link BuildingSelectPanel} constructor.</p>
 *
 * @author Christpher Chosang
 */
public class BuildingSelectPanel extends JPanel {

    // Path to the image of the map of the picture to the right
    private static final String PATH_TO_IMAGE = "/cs2212/westernmaps/building-select/mc.png";

    // The list of buildings that are being displayed in the panel
    private final JList<Building> buildingList;
    // The error message that is displayed when the user tries to select a building without selecting one
    private final JLabel noBuildingSelectedError;

    // the listeners that are called when the user selects a building
    private final List<Runnable> logOutListeners = new ArrayList<>();
    private final List<Consumer<Building>> buildingSelectListeners = new ArrayList<>();

    /**
     * Creates the panel that allows the user to select a building.
     *
     * @param buildings the list of buildings that are being displayed in the panel
     */
    public BuildingSelectPanel(List<Building> buildings) {
        // This determines what MainWindow will use as its title.
        setName("Building Select");

        setLayout(new BorderLayout());

        // Create selection pane components
        JLabel heading = new JLabel("Select a Building:");
        heading.putClientProperty(FlatClientProperties.STYLE_CLASS, "h0");

        // the building list
        buildingList = new JList<>(buildings.toArray(Building[]::new));
        buildingList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        buildingList.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        buildingList.setBorder(BorderFactory.createLineBorder(UIManager.getColor("Component.borderColor")));

        // the select building button
        var selectButton = new JButton("Select Building");
        selectButton.putClientProperty(FlatClientProperties.STYLE_CLASS, "large");
        selectButton.addActionListener(e -> validateAndSubmit());

        // Temporary code
        JPanel weather = new JPanel();
        weather.add(new JLabel("weather"));

        // Create help box
        JPanel helpBox = new JPanel();
        helpBox.setLayout(new BoxLayout(helpBox, BoxLayout.PAGE_AXIS));
        helpBox.add(Box.createRigidArea(new Dimension(0, 10)));

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
     * The log-out listener, which is called when the user clicks the log-out button.
     *
     * @param listener is the listener that will be added to the logOutListeners
     */
    public void addLogOutListener(Runnable listener) {
        logOutListeners.add(listener);
    }

    /**
     * The building select listener, which is called when the user clicks the select building button.
     *
     * @param listener is the listener that will be added to the buildingSelectListeners
     */
    public void addBuildingSelectListener(Consumer<Building> listener) {
        buildingSelectListeners.add(listener);
    }

    // Checking if a building has been submitted.
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
