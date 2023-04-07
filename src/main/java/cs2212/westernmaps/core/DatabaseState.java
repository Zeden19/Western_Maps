package cs2212.westernmaps.core;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
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
    /**
     * A database state with no accounts, buildings, or POIs.
     */
    public static final DatabaseState EMPTY = new DatabaseState(List.of(), List.of(), List.of());

    public DatabaseState {
        accounts = List.copyOf(accounts);
        buildings = List.copyOf(buildings);
        pois = List.copyOf(pois);
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
        return JsonMapper.builder()
                .configure(JsonParser.Feature.AUTO_CLOSE_SOURCE, false)
                .configure(JsonGenerator.Feature.AUTO_CLOSE_TARGET, false)
                .configure(MapperFeature.SORT_PROPERTIES_ALPHABETICALLY, true)
                .configure(SerializationFeature.INDENT_OUTPUT, true)
                .defaultPrettyPrinter(new JsonPrettyPrinter())
                .build();
    }

    // https://stackoverflow.com/a/64670800
    // https://stackoverflow.com/a/33043697
    public static final class JsonPrettyPrinter extends DefaultPrettyPrinter {
        public JsonPrettyPrinter() {
            _arrayIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
            _objectIndenter = DefaultIndenter.SYSTEM_LINEFEED_INSTANCE;
        }

        public JsonPrettyPrinter(DefaultPrettyPrinter base) {
            super(base);
        }

        @Override
        public JsonPrettyPrinter createInstance() {
            return new JsonPrettyPrinter(this);
        }

        @Override
        public JsonPrettyPrinter withSeparators(Separators separators) {
            this._separators = separators;
            this._objectFieldValueSeparatorWithSpaces = separators.getObjectFieldValueSeparator() + " ";
            return this;
        }

        @Override
        public void writeEndArray(JsonGenerator g, int nrOfValues) throws IOException {
            if (!_arrayIndenter.isInline()) {
                --_nesting;
            }
            if (nrOfValues > 0) {
                _arrayIndenter.writeIndentation(g, _nesting);
            }
            g.writeRaw(']');
            // Add newline at end of file.
            if (_nesting == 0) {
                g.writeRaw('\n');
            }
        }

        @Override
        public void writeEndObject(JsonGenerator g, int nrOfEntries) throws IOException {
            if (!_objectIndenter.isInline()) {
                --_nesting;
            }
            if (nrOfEntries > 0) {
                _objectIndenter.writeIndentation(g, _nesting);
            }
            g.writeRaw('}');
            // Add newline at end of file.
            if (_nesting == 0) {
                g.writeRaw('\n');
            }
        }
    }
}
