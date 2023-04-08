package cs2212.westernmaps.core;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class DatabaseTest {
    private Path directory;

    @BeforeEach
    void setUpTestDatabase() throws IOException {
        // Create a database in a temporary directory that the tests can work
        // with.
        directory = Files.createTempDirectory("test-data");
        var databaseJsonPath = directory.resolve("database.json");
        try (var outputStream = Files.newOutputStream(databaseJsonPath)) {
            DatabaseState.EMPTY.saveToStream(outputStream);
        }
    }

    @AfterEach
    void tearDownTestDatabase() throws IOException {
        // Delete the temporary directory and database.json.
        Files.delete(directory.resolve("database.json"));
        Files.delete(directory);
    }

    @Test
    void testOpenDirectory() throws IOException {
        // Make sure the content is the same as the sample file.
        Database database = Database.openDirectory(directory);
        assertEquals(DatabaseState.EMPTY, database.getCurrentState());
    }

    @Test
    void testGetHistory() throws IOException {
        Database database = Database.openDirectory(directory);
        // After first loading the database, the history should contain only the
        // current state and undo and redo should not be possible.
        assertEquals(DatabaseState.EMPTY, database.getHistory().getCurrentState());
        assertFalse(database.getHistory().undo());
        assertFalse(database.getHistory().redo());
    }

    @Test
    void testGetCurrentState() throws IOException {
        Database database = Database.openDirectory(directory);
        assertEquals(DatabaseState.EMPTY, database.getCurrentState());
    }

    @Test
    void testResolveFloorMapUri() throws IOException {
        Database database = Database.openDirectory(directory);
        var mapPath = Path.of("maps/something.svg");
        Floor floor = new Floor("G", "Ground floor", mapPath);
        var expectedMapPath = directory.resolve("maps/something.svg");
        assertEquals(expectedMapPath.toUri(), database.resolveFloorMapUri(floor));
    }

    @Test
    void testSave() throws IOException {
        Database database = Database.openDirectory(directory);
        Account testAccount = new Account("testUser", "testPassword", false);
        Floor floor = new Floor("G", "test name", Path.of(""));
        POI testPOI = new POI("Test", "test", 0, 0, Set.of(), floor, Layer.MISCELLANEOUS, testAccount);

        var state = database.getCurrentState().modifyPOIs(pois -> Lists.append(pois, testPOI));
        database.getHistory().pushState(state);
        database.save();

        // Load the state that is currently in the JSON file for comparison.
        DatabaseState stateFromFile;
        var databaseJsonPath = directory.resolve("database.json");
        try (var inputStream = Files.newInputStream(databaseJsonPath)) {
            stateFromFile = DatabaseState.loadFromStream(inputStream);
        }

        assertEquals(stateFromFile, database.getCurrentState());
    }
}
