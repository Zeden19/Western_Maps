package cs2212.westernmaps.maps;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import cs2212.westernmaps.core.Floor;
import java.awt.Dimension;
import java.awt.Insets;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.*;

/**
 * A panel that allows the user to switch between different floors of the current building
 *
 * @author Connor Cummings
 */
public final class FloorSwitcher extends JPanel {

    // The listeners that will be notified when the floor is switched.
    private final List<Consumer<Floor>> floorSwitchListeners = new ArrayList<>();

    private final List<FloorButton> floorButtons = new ArrayList<>();
    private final ButtonGroup buttonGroup;

    /**
     * Creates a new floor switcher.
     *
     * <p>When the floor switcher is created, the first floor in the list
     * provided will be selected.</p>
     *
     * @param floors The list of floors to switch between.
     */
    public FloorSwitcher(List<Floor> floors) {
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        buttonGroup = new ButtonGroup();

        for (int index = 0; index < floors.size(); index++) {
            var floorButton = new FloorButton(floors.get(index));

            add(floorButton, 0);
            buttonGroup.add(floorButton);
            floorButtons.add(floorButton);

            if (index == 0) {
                floorButton.setSelected(true);
            }
        }

        setBorder(new FlatButtonBorder());
    }

    /**
<<<<<<< HEAD
<<<<<<< HEAD
     * Sets which floor button is currently displayed as selected.
     *
     * @param floor The floor whose button should be selected.
     */
    public void setSelectedFloor(Floor floor) {
        var button =
                floorButtons.stream().filter(b -> b.getFloor().equals(floor)).findFirst();
        button.ifPresent(b -> SwingUtilities.invokeLater(() -> {
            buttonGroup.clearSelection();
            b.setSelected(true);
        }));
    }

    /**
=======
     * adds listeners to the floorSwitchListeners
>>>>>>> 8f07257 (Add javadoc fixes to FloorSwitcher.java)
     * @param listener is the listener being added to the floor switcher
     */
    public void addFloorSwitchListener(Consumer<Floor> listener) {
        floorSwitchListeners.add(listener);
    }

    private final class FloorButton extends JToggleButton {
        private static final Dimension SIZE = new Dimension(40, 40);

        private final Floor floor;

        /**
         * Creates the floor button
         * @param floor is the floor of the button being made
         */
        public FloorButton(Floor floor) {
            super(floor.shortName());
            this.floor = floor;

            setPreferredSize(SIZE);
            setMinimumSize(SIZE);
            setMaximumSize(SIZE);

            // Prevent Java Swing from replacing the content with ...
            setMargin(new Insets(2, 2, 2, 2));

            putClientProperty(FlatClientProperties.BUTTON_TYPE, "borderless");

            addActionListener(e -> {
                if (isSelected()) {
                    floorSwitchListeners.forEach(listener -> listener.accept(floor));
                }
            });
        }

        public Floor getFloor() {
            return floor;
        }
    }
}
