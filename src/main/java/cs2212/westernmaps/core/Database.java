package cs2212.westernmaps.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
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
     * Loads a database from an input stream containing JSON data.
     *
     * @param stream       The input stream to load data from.
     * @return             A new database containing the loaded data.
     * @throws IOException If an IO error occurred while reading the data, or
     *                     the JSON data was invalid.
     */
    public static Database loadFromStream(InputStream stream) throws IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Loads a database from a file containing JSON data.
     *
     * @param filePath     A path to the file to load data from.
     * @return             A new database containing the loaded data.
     * @throws IOException If an IO error occurred while reading the data, or
     *                     the JSON data was invalid.
     */
    public static Database loadFromFile(Path filePath) throws IOException {
        return loadFromStream(Files.newInputStream(filePath));
    }

    /**
     * Saves this database as JSON data to an output stream.
     *
     * @param stream       The output stream to save data to.
     * @throws IOException If an IO error occurred while writing the data, or
     *                     the database could not be serialized as JSON.
     */
    public void saveToStream(OutputStream stream) throws IOException {
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Saves this database as JSON data to a file.
     *
     * @param filePath     A path to the file to save data to.
     * @throws IOException If an IO error occurred while writing the data, or
     *                     the database could not be serialized as JSON.
     */
    public void saveToFile(Path filePath) throws IOException {
        saveToStream(Files.newOutputStream(filePath));
    }

    /**
     * Creates a Jackson {@link ObjectMapper} and configures it for serializing
     * and deserializing a database to JSON.
     *
     * <p>This method is package-private (instead of private) so it is visible
     * to unit tests.</p>
     *
     * @return A new properly-configured {@code ObjectMapper}.
     */
    static ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
