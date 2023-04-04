package cs2212.westernmaps.core;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.Icon;

/**
 * A layer that a POI can be assigned to.
 *
 * @see POI
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

    public Icon getIcon() {
        return icon;
    }
}
