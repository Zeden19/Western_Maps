package cs2212.westernmaps;

import com.formdev.flatlaf.extras.FlatSVGIcon;
import javax.swing.Icon;

/**
 * A miscellaneous icon used by the application, specifically the close icon.
 * <p> It is used in the POI summary panel, and the layer panel in the map view to close those respective windows. </p>
 * @author Connor Cummings
 */
public final class MiscIcons {
    private MiscIcons() {}

    /** The close icon path. */
    public static final Icon CLOSE_ICON = new FlatSVGIcon("cs2212/westernmaps/misc-icons/x-small.svg");
}
