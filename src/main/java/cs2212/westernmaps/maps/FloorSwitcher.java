package cs2212.westernmaps.maps;

import cs2212.westernmaps.core.Floor;
import java.awt.Dimension;
import java.util.List;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;

public final class FloorSwitcher extends JComponent {
    private final List<Floor> floors;

    /**
     * Creates a new floor switcher.
     *
     * <p>When the floor switcher is created, the first floor in the list
     * provided will be selected.</p>
     *
     * @param floors The list of floors to switch between.
     */
    public FloorSwitcher(List<Floor> floors) {
        this.floors = floors;

        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        for (var floor : floors) {
            var floorButton = new JButton(floor.shortName());
            floorButton.setPreferredSize(new Dimension(48, 48));
            floorButton.setMinimumSize(floorButton.getPreferredSize());
            floorButton.setMaximumSize(floorButton.getPreferredSize());

            add(floorButton, 0);
        }
    }
}
