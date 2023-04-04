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
    /**
     * Alias of {@link #name()} used by Java Swing components.
     *
     * @return The display name of this POI.
     */
    @Override
    public String toString() {
        return name();
    }

    /**
     * Creates a copy of this POI with its location updated.
     *
     * @param x The x-coordinate of the new location.
     * @param y The y-coordinate of the new location.
     * @return  A copy of this POI with the new location.
     */
    public POI withLocation(int x, int y) {
        return new POI(name(), description(), x, y, favorite(), floor(), layer());
    }
}
