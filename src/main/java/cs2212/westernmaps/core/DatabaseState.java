package cs2212.westernmaps.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * A snapshot of all the application's data at a given point in time.
 *
 * <p>All fields in a {@code DatabaseState} object are immutable. Whenever the
 * data is changed, a new state is created.</p>
 *
 * @param accounts  The accounts contained in this database.
 * @param buildings The buildings contained in this database.
 * @param pois      The points of interest (POIs) contained in this database.
 */
public record DatabaseState(List<Account> accounts, List<Building> buildings, List<POI> pois) {
    public DatabaseState {
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
    public static DatabaseState loadFromStream(InputStream stream) throws IOException {
        return createObjectMapper().readValue(stream, DatabaseState.class);
    }

    /**
     * Loads a database from a file containing JSON data.
     *
     * @param filePath     A path to the file to load data from.
     * @return             A new database containing the loaded data.
     * @throws IOException If an IO error occurred while reading the data, or
     *                     the JSON data was invalid.
     */
    public static DatabaseState loadFromFile(Path filePath) throws IOException {
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
        createObjectMapper().writeValue(stream, this);
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
     * @return A new properly-configured {@code ObjectMapper}.
     */
    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper();
    }
}
