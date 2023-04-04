package cs2212.westernmaps.core;

import java.util.List;

/**
 * A building that the application has map data for.
 *
 * @param name   The display name of this building.
 * @param floors The floors contained in this building.
 */
public record Building(String name, List<Floor> floors) {
    // constructor for the Building Class
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
