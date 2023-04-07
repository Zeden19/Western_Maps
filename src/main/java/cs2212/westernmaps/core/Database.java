package cs2212.westernmaps.core;

import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;

// TODO: Add Javadoc.

public final class Database {
    private final Path directory;
    private final Path jsonFile;
    private final UndoHistory history;

    private Database(Path directory, Path jsonFile, UndoHistory history) {
        this.directory = directory;
        this.jsonFile = jsonFile;
        this.history = history;
    }

    public static Database openDirectory(Path directory) throws IOException {
        var jsonFile = directory.resolve("database.json");

        UndoHistory history;
        try (var stream = Files.newInputStream(jsonFile)) {
            var state = DatabaseState.loadFromStream(stream);
            history = new UndoHistory(state);
        }

        return new Database(directory, jsonFile, history);
    }

    public UndoHistory getHistory() {
        return history;
    }

    public DatabaseState getCurrentState() {
        return getHistory().getCurrentState();
    }

    public URI resolveFloorMapUri(Floor floor) {
        var mapPath = directory.resolve(floor.mapPath());
        return mapPath.toUri();
    }

    public void save() throws IOException {
        try (var stream = Files.newOutputStream(jsonFile)) {
            getCurrentState().saveToStream(stream);
        }
    }
}
