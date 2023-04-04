package cs2212.westernmaps.maps;

import com.formdev.flatlaf.FlatClientProperties;
import com.formdev.flatlaf.ui.FlatButtonBorder;
import cs2212.westernmaps.core.Floor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.event.ItemEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

public final class FloorSwitcher extends JPanel {
    private final List<Consumer<Floor>> floorSwitchListeners = new ArrayList<>();

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

        var buttonGroup = new ButtonGroup();

        for (int index = 0; index < floors.size(); index++) {
            var floorButton = new FloorButton(floors.get(index));
            if (index == 0) {
                floorButton.setSelected(true);
            }

            add(floorButton, 0);
            buttonGroup.add(floorButton);
        }

        setBorder(new FlatButtonBorder());
    }

    public void addFloorSwitchListener(Consumer<Floor> listener) {
        floorSwitchListeners.add(listener);
    }

    private final class FloorButton extends JToggleButton {
        private static final Dimension SIZE = new Dimension(40, 40);

        public FloorButton(Floor floor) {
            super(floor.shortName());

            setPreferredSize(SIZE);
            setMinimumSize(SIZE);
            setMaximumSize(SIZE);

            // Prevent Java Swing from replacing the content with ...
            setMargin(new Insets(2, 2, 2, 2));

            putClientProperty(FlatClientProperties.BUTTON_TYPE, "borderless");

            addItemListener(e -> {
                if (e.getStateChange() == ItemEvent.SELECTED) {
                    floorSwitchListeners.forEach(listener -> listener.accept(floor));
                }
            });
        }
    }
}
