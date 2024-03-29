package cs2212.westernmaps.core;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.Icon;

/**
 * A layer that a POI can be assigned to. These layers also have the icons that are used to display them on the maps
 *
 * @see POI
 * @author Connor Cummings
 */
public enum Layer {
    ACCESSIBILITY("Accessibility", "accessibility"),
    CLASSROOMS("Classrooms", "classroom"),
    CUSTOM("Custom", "custom"),
    EATERIES("Eateries", "eatery"),
    FITNESS("Fitness", "fitness"),
    LOUNGES("Lounges", "lounge"),
    MISCELLANEOUS("Miscellaneous", "miscellaneous"),
    UTILITIES("Utilities", "utility"),
    WASHROOMS("Washrooms", "washroom");

    private final String displayName;
    private final FlatSVGIcon icon;

    Layer(String displayName, String iconName) {
        this.displayName = displayName;
        this.icon = new FlatSVGIcon("cs2212/westernmaps/poi-icons/" + iconName + ".svg", Layer.class.getClassLoader());
    }

    /**
     * Gets the name of this layer as it should be displayed to the user.
     *
     * @return The display name of this layer.
     */
    public String getDisplayName() {
        return displayName;
    }

    /**
     * Gets the icon that should be used to represent this layer.
     * @return The icon of this layer.
     */
    public Icon getIcon() {
        return icon;
    }

    /**
     * Alias of {@link #getDisplayName()} used by Java Swing components.
     *
     * @return The display name of this layer.
     */
    @Override
    public String toString() {
        return getDisplayName();
    }
}
