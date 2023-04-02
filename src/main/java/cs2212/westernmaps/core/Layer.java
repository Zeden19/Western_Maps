package cs2212.westernmaps.core;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.Icon;

/**
 * A layer that a POI can be assigned to.
 *
 * @see POI
 */
public enum Layer {
    ACCESSIBILITY("accessibility"),
    CLASSROOMS("classroom"),
    CUSTOM("custom"),
    EATERIES("eatery"),
    FITNESS("fitness"),
    LOUNGES("lounge"),
    MISCELLANEOUS("miscellaneous"),
    UTILITIES("utility"),
    WASHROOMS("washroom");

    private final FlatSVGIcon icon;

    Layer(String iconName) {
        this.icon = new FlatSVGIcon("cs2212/westernmaps/poi-icons/" + iconName + ".svg", Layer.class.getClassLoader());
    }

    public Icon getIcon() {
        return icon;
    }
}
