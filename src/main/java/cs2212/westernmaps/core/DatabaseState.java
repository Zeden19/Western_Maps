package cs2212.westernmaps.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.function.Function;

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
     * Creates a database state with no accounts, buildings, or POIs.
     *
     * @return A new database state.
     */
    public static DatabaseState empty() {
        return new DatabaseState(List.of(), List.of(), List.of());
    }

    /**
     * Updates the list of accounts in this database state.
     *
     * @param operation A function that updates the list of accounts.
     * @return          A new state containing the updated list of accounts.
     */
    public DatabaseState modifyAccounts(Function<List<Account>, List<Account>> operation) {
        return new DatabaseState(operation.apply(accounts()), buildings(), pois());
    }

    /**
     * Updates the list of buildings in this database state.
     *
     * @param operation A function that updates the list of buildings.
     * @return          A new state containing the updated list of buildings.
     */
    public DatabaseState modifyBuildings(Function<List<Building>, List<Building>> operation) {
        return new DatabaseState(accounts(), operation.apply(buildings()), pois());
    }

    /**
     * Updates the list of POIs in this database state.
     *
     * @param operation A function that updates the list of POIs.
     * @return          A new state containing the updated list of POIs.
     */
    public DatabaseState modifyPOIs(Function<List<POI>, List<POI>> operation) {
        return new DatabaseState(accounts(), buildings(), operation.apply(pois()));
    }

    /**
     * Loads a database state from an input stream containing JSON data.
     *
     * @param stream       The input stream to load data from.
     * @return             A new database state containing the loaded data.
     * @throws IOException If an IO error occurred while reading the data, or
     *                     the JSON data was invalid.
     */
    public static DatabaseState loadFromStream(InputStream stream) throws IOException {
        return createObjectMapper().readValue(stream, DatabaseState.class);
    }

    /**
     * Saves this database state as JSON data to an output stream.
     *
     * @param stream       The output stream to save data to.
     * @throws IOException If an IO error occurred while writing the data, or
     *                     the database could not be serialized as JSON.
     */
    public void saveToStream(OutputStream stream) throws IOException {
        createObjectMapper().writeValue(stream, this);
    }

    /**
     * Creates a Jackson {@link ObjectMapper} and configures it for serializing
     * and deserializing a database to JSON.
     *
     * @return A new properly-configured {@code ObjectMapper}.
     */
    private static ObjectMapper createObjectMapper() {
        return new ObjectMapper()
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
                .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false);
    }
}
