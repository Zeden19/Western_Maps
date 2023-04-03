package cs2212.westernmaps.core;

/**
 * A point of interest (POI) on a map.
 *
 * @param name        The display name of this POI.
 * @param description A long-form description of this POI.
 * @param x           The X-coordinate of this POI on the map of its floor.
 * @param y           The Y-coordinate of this POI on the map of its floor.
 * @param favorite    Whether this POI is one of the user's favorites.
 * @param floor       The floor this POI is on.
 * @param layer       The layer this POI is assigned to.
 */
public record POI(String name, String description, int x, int y, boolean favorite, Floor floor, Layer layer) {

    @Override
    public String toString() {
        return name();
    }
}
