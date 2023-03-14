package cs2212.westernmaps.core;

import java.nio.file.Path;
import java.util.List;

/**
 * A top-level container for most of the application's data.
 *
 * <p>Although this class is called {@code Database}, it does not represent an
 * actual database system or a connection to one; it simply holds data.</p>
 *
 * @param accounts  The accounts contained in this database.
 * @param buildings The buildings contained in this database.
 * @param pois      The points of interest (POIs) contained in this database.
 */
public record Database(List<Account> accounts, List<Building> buildings, List<POI> pois) {
    public Database {
        accounts = List.copyOf(accounts);
        buildings = List.copyOf(buildings);
        pois = List.copyOf(pois);
    }

    /**
     * Loads a database from a directory in the filesystem.
     *
     * @param path The path to the directory containing the data to load.
     * @return     A new database containing the loaded data.
     * @see #saveToDirectory
     */
    public static Database loadFromDirectory(Path path) {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Saves this database to a directory in the filesystem.
     *
     * @param path The path to the directory where the data will be saved.
     * @see #loadFromDirectory
     */
    public void saveToDirectory(Path path) {
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
