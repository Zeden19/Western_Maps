package cs2212.westernmaps.core;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * This class is for the database of the program. It creates the database that is used for the program
 * with a directory, json file, and its history.
 * @author
 */
public final class Database {
    private final Path directory;
    private final Path jsonFile;
    private final UndoHistory history;

    /**
     * The constructor for the database
     * @param directory, the directory being created
     * @param jsonFile, the json file being used for the directory
     * @param history, the history of the new database being created
     */
    private Database(Path directory, Path jsonFile, UndoHistory history) {
        this.directory = directory;
        this.jsonFile = jsonFile;
        this.history = history;
    }

    /**
     * opens a new directory
     * @param directory, the new directory that will be opened
     * @return the new Database with the directory, jsonFile, and its history
     * @throws IOException when the file is not found
     */
    public static Database openDirectory(Path directory) throws IOException {
        var jsonFile = directory.resolve("database.json");

        UndoHistory history;
        try (var stream = Files.newInputStream(jsonFile)) {
            var state = DatabaseState.loadFromStream(stream);
            history = new UndoHistory(state);
        }

        return new Database(directory, jsonFile, history);
    }

    /**
     * getter for the history
     * @return the databases history
     */
    public UndoHistory getHistory() {
        return history;
    }

    /**
     *getter for the current state of database
     * @return the current state of the database
     */
    public DatabaseState getCurrentState() {
        return getHistory().getCurrentState();
    }

    public URI resolveFloorMapUri(Floor floor) {
        var mapPath = directory.resolve(floor.mapPath());
        return mapPath.toUri();
    }

    /**
     *This method saves the database
     * @throws IOException when the database file is not found
     */
    public void save() throws IOException {
        try (var stream = Files.newOutputStream(jsonFile)) {
            getCurrentState().saveToStream(stream);
        }
    }
}
