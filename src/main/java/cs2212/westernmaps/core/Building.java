package cs2212.westernmaps.core;

import java.util.List;

/**
 * A building that the application has map data for.
 *
 * @param name   The display name of this building.
 * @param floors The floors contained in this building.
 *
 * @author Connor Cummings
 */
public record Building(String name, List<Floor> floors) {

    /**
     * Creates a new building.
     *
     * @param name the name of the building
     * @param floors the floors the building contains, which of are type {@link Floor}
     */
    public Building {
        floors = List.copyOf(floors);
    }

    /**
     * Alias of {@link #name()} used by Java Swing components.
     *
     * @return The display name of this building.
     */
    @Override
    public String toString() {
        return name();
    }
}
